package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyOperations;
import com.dokany.java.DokanyUtils;
import com.dokany.java.constants.AccessMask;
import com.dokany.java.constants.CreateOptions;
import com.dokany.java.constants.CreationDisposition;
import com.dokany.java.constants.FileAccessMask;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.constants.Win32ErrorCode;
import com.dokany.java.structure.ByHandleFileInfo;
import com.dokany.java.structure.DokanyFileInfo;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.FullFileInfo;
import com.dokany.java.structure.VolumeInformation;
import com.google.common.base.CharMatcher;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.cryptomator.frontend.dokany.locks.DataLock;
import org.cryptomator.frontend.dokany.locks.LockManager;
import org.cryptomator.frontend.dokany.locks.PathLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.Normalizer;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class ReadWriteAdapter implements DokanyFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(ReadWriteAdapter.class);

	private final Path root;
	private final LockManager lockManager;
	private final VolumeInformation volumeInformation;
	private final CompletableFuture didMount;
	private final OpenHandleFactory fac;
	private final FileStore fileStore;

	public ReadWriteAdapter(Path root, LockManager lockManager, VolumeInformation volumeInformation, CompletableFuture<?> didMount) {
		this.root = root;
		this.lockManager = lockManager;
		this.volumeInformation = volumeInformation;
		this.didMount = didMount;
		this.fac = new OpenHandleFactory();
		try {
			this.fileStore = Files.getFileStore(root);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public int zwCreateFile(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
		Path path;
		try {
			path = getRootedPath(rawPath);
		} catch (InvalidPathException e) {
			return Win32ErrorCode.ERROR_BAD_PATHNAME.getMask();
		}
		int rawCreationDispostion = FileUtil.convertCreateDispositionToCreationDispostion(rawCreateDisposition);
		int rawDesiredAccessWin32 = FileUtil.mapFileGenericAccessToGenericAccess(rawDesiredAccess);

		EnumIntegerSet<CreateOptions> createOptions = DokanyUtils.enumSetFromInt(rawCreateOptions, CreateOptions.values());
		EnumIntegerSet<AccessMask> accessMasks = DokanyUtils.enumSetFromInt(rawDesiredAccessWin32, AccessMask.values());
		EnumIntegerSet<FileAccessMask> fileAccessMasks = DokanyUtils.enumSetFromInt(rawDesiredAccessWin32, FileAccessMask.values());
		EnumIntegerSet<FileAttribute> fileAttributes = DokanyUtils.enumSetFromInt(rawFileAttributes, FileAttribute.values());
		CreationDisposition creationDisposition = CreationDisposition.fromInt(rawCreationDispostion);
		LOG.trace("zwCreateFile() is called for {} with the following parameters:\n\tCreateDisposition -- {}\n\tcreateOptions -- {}\n\taccessMasks -- {}\n\tfileAccessMasks -- {}\n\tfileAttributes -- {}.", path, creationDisposition, createOptions, accessMasks, fileAttributes, fileAttributes);
		Optional<BasicFileAttributes> attr;
		try {
			attr = Optional.of(Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS));
		} catch (NoSuchFileException e) {
			attr = Optional.empty();
		} catch (IOException e) {
			return Win32ErrorCode.ERROR_IO_DEVICE.getMask();
		}

		//is the file a directory and if yes, indicated as one?
		if (attr.isPresent() && attr.get().isDirectory()) {
			if ((rawCreateOptions & CreateOptions.FILE_NON_DIRECTORY_FILE.getMask()) == 0) {
				dokanyFileInfo.IsDirectory = 0x01;
				//TODO: set the share access like in the dokany mirror example
			} else {
				LOG.debug("Ressource {} is a Directory and cannot be opened as a file.", path);
				return Win32ErrorCode.ERROR_INVALID_STATE.getMask();
			}
		} else if (attr.isPresent() && !attr.get().isRegularFile()) {
			return Win32ErrorCode.ERROR_CANT_ACCESS_FILE.getMask(); // or ERROR_OPEN_FAILED or ERROR_CALL_NOT_IMPLEMENTED?
		}

		try (PathLock pathLock = lockManager.createPathLock(path.toString()).forWriting();
			 DataLock dataLock = pathLock.lockDataForWriting()) {
			if (dokanyFileInfo.isDirectory()) {
				return createDirectory(path, creationDisposition, rawFileAttributes, dokanyFileInfo);
			} else {
				Set<OpenOption> openOptions = FileUtil.buildOpenOptions(accessMasks, fileAccessMasks, fileAttributes, createOptions, creationDisposition, dokanyFileInfo.writeToEndOfFile(), attr.isPresent());
				return createFile(path, creationDisposition, openOptions, rawFileAttributes, dokanyFileInfo);
			}
		}
	}

	private int createDirectory(Path path, CreationDisposition creationDisposition, int rawFileAttributes, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("Try to open {} as Directory.", path);
		final int mask = creationDisposition.getMask();
		//createDirectory request
		if (mask == CreationDisposition.CREATE_NEW.getMask() || mask == CreationDisposition.OPEN_ALWAYS.getMask()) {
			// TODO: rename current method "createDirectory" to "openDirectory" and extract following part to new private method "createDirectory"
			try {
				Files.createDirectory(path);
				LOG.trace("Directory {} successful created ", path);
			} catch (FileAlreadyExistsException e) {
				if (mask == CreationDisposition.CREATE_NEW.getMask()) {
					//we got create_new flag -> there should be nuthing, but there is somthin!
					LOG.trace("Directory {} already exists.", path);
					return Win32ErrorCode.ERROR_ALREADY_EXISTS.getMask();
				}
			} catch (FileSystemException e) {
				final String reason = e.getReason().toLowerCase();
				if (reason.contains("path too long") || reason.contains("extension too long") || reason.contains("path too long")) {
					LOG.warn("zwCreateFile(): Creation of {} failed, file name too long.", path);
					return Win32ErrorCode.ERROR_FILENAME_EXCED_RANGE.getMask();
				} else {
					LOG.debug("zwCreateFile(): IO error occured during the creation of {}.", path);
					LOG.debug("zwCreateFile(): ", e);
					return Win32ErrorCode.ERROR_CANNOT_MAKE.getMask();
				}
			} catch (IOException e) {
				//we dont know what the hell happened
				LOG.debug("zwCreateFile(): IO error occured during the creation of {}.", path);
				LOG.debug("zwCreateFile(): ", e);
				return Win32ErrorCode.ERROR_CANNOT_MAKE.getMask();
			}
		}

		//some otha flags were used
		//here we check if we try to open a file as a directory
		if (Files.isRegularFile(path)) {
			//sh*t
			LOG.trace("Attempt to open file {} as a directory.", path);
			return Win32ErrorCode.ERROR_DIRECTORY.getMask();
		} else {
			// we open the directory in some kinda way
			try {
				setFileAttributes(path, rawFileAttributes);
				dokanyFileInfo.Context = fac.openDir(path);
				LOG.trace("({}) {} opened successful with handle {}.", dokanyFileInfo.Context, path, dokanyFileInfo.Context);
			} catch (NoSuchFileException e) {
				LOG.trace("{} not found.", path);
				return Win32ErrorCode.ERROR_PATH_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.debug("zwCreateFile(): IO error occurred during opening handle to {}.", path);
				LOG.debug("zwCreateFile(): ", e);
				return Win32ErrorCode.ERROR_OPEN_FAILED.getMask();
			}
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}
	}


	private int createFile(Path path, CreationDisposition creationDisposition, Set<OpenOption> openOptions, int rawFileAttributes, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("Try to open {} as File.", path);
		final int mask = creationDisposition.getMask();
		DosFileAttributes attr = null;
		try {
			attr = Files.readAttributes(path, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
		} catch (IOException e) {
			LOG.trace("Could not read file attributes.");
		}
		//we want to create a file
		//system or hidden file?
		if (attr != null
				&&
				(mask == CreationDisposition.TRUNCATE_EXISTING.getMask() || mask == CreationDisposition.CREATE_ALWAYS.getMask())
				&&
				(
						((rawFileAttributes & FileAttribute.HIDDEN.getMask()) == 0 && attr.isHidden())
								||
								((rawFileAttributes & FileAttribute.SYSTEM.getMask()) == 0 && attr.isSystem())
				)
		) {
			//cannot overwrite hidden or system file
			LOG.trace("{} is hidden or system file. Unable to overwrite.", path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		}
		//read-only?
		else if ((attr != null && attr.isReadOnly() || ((rawFileAttributes & FileAttribute.READONLY.getMask()) != 0))
				&& dokanyFileInfo.DeleteOnClose != 0
		) {
			//cannot overwrite file
			LOG.trace("{} is readonly. Unable to overwrite.", path);
			return Win32ErrorCode.ERROR_FILE_READ_ONLY.getMask();
		} else {
			try {
				dokanyFileInfo.Context = fac.openFile(path, openOptions);
				if (attr == null || mask == CreationDisposition.TRUNCATE_EXISTING.getMask() || mask == CreationDisposition.CREATE_ALWAYS.getMask()) {
					//according to zwCreateFile() documentation FileAttributes are ignored if no file is created or overwritten
					setFileAttributes(path, rawFileAttributes);
				}
				LOG.trace("({}) {} opened successful with handle {}.", dokanyFileInfo.Context, path, dokanyFileInfo.Context);
				//required by contract
				Win32ErrorCode returnCode;
				if (attr != null && (mask == CreationDisposition.OPEN_ALWAYS.getMask() || mask == CreationDisposition.CREATE_ALWAYS.getMask())) {
					returnCode = Win32ErrorCode.ERROR_ALREADY_EXISTS;
				} else {
					returnCode = Win32ErrorCode.ERROR_SUCCESS;
				}
				return returnCode.getMask();
			} catch (FileAlreadyExistsException e) {
				LOG.trace("File {} already exists.", path);
				return Win32ErrorCode.ERROR_FILE_EXISTS.getMask();
			} catch (NoSuchFileException e) {
				LOG.trace("File {} not found.", path);
				return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (AccessDeniedException e) {
				LOG.trace("zwCreateFile(): Access to file {} was denied.", path);
				LOG.trace("Cause:", e);
				return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
			} catch (FileSystemException e) {
				final String reason = e.getReason().toLowerCase();
				if (reason.contains("path too long") || reason.contains("extension too long") || reason.contains("path too long")) {
					LOG.warn("zwCreateFile(): Creation of {} failed, file name too long.", path);
					return Win32ErrorCode.ERROR_FILENAME_EXCED_RANGE.getMask();
				} else {
					LOG.debug("zwCreateFile(): IO error occured during the creation of {}.", path);
					LOG.debug("zwCreateFile(): ", e);
					return Win32ErrorCode.ERROR_CANNOT_MAKE.getMask();
				}
			} catch (IOException e) {
				if (attr != null) {
					LOG.debug("zwCreateFile(): IO error occurred during opening handle to {}.", path);
					LOG.debug("zwCreateFile(): ", e);
					return Win32ErrorCode.ERROR_OPEN_FAILED.getMask();
				} else {
					LOG.debug("zwCreateFile(): IO error occurred during creation of {}.", path);
					LOG.debug("zwCreateFile(): ", e);
					return Win32ErrorCode.ERROR_CANNOT_MAKE.getMask();
				}
			} catch (IllegalArgumentException e) {
				//special handling for cryptofs
				LOG.warn("createFile(): Exception occurred:", e);
				LOG.warn("{} seems to be modified on disk.", path);
				dokanyFileInfo.Context = fac.openRestrictedFile(path);
				LOG.warn("({}) {} opened in restricted mode with handle {}.", dokanyFileInfo.Context, path, dokanyFileInfo.Context);
				return Win32ErrorCode.ERROR_FILE_CORRUPT.getMask();
			}
		}
	}

	/**
	 * The handle is closed in this method, due to the requirements of the dokany implementation to delete a file in the cleanUp method
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	@Override
	public void cleanup(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) cleanup() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("cleanup(): Invalid handle to {}.", path);
		} else {
			try {
				fac.close(dokanyFileInfo.Context);
				if (dokanyFileInfo.deleteOnClose()) {
					try (PathLock pathLock = lockManager.createPathLock(path.toString()).forWriting();
						 DataLock dataLock = pathLock.lockDataForWriting()) {
						Files.getFileAttributeView(path, DosFileAttributeView.class).setReadOnly(false);
						Files.delete(path);
						LOG.trace("({}) {} successful deleted.", dokanyFileInfo.Context, path);
					} catch (DirectoryNotEmptyException e) {
						LOG.debug("({}) Directory {} not empty.", dokanyFileInfo.Context, path);
					} catch (IOException e) {
						LOG.debug("({}) cleanup(): IO error during deletion of {} ", dokanyFileInfo.Context, path, e);
						LOG.debug("cleanup(): ", e);
					}
				}
			} catch (IOException e) {
				LOG.debug("({}) cleanup(): Unable to close handle to {}", dokanyFileInfo.Context, path, e);
				LOG.debug("cleanup(): ", e);
			}
		}
	}

	@Override
	public void closeFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) closeFile() is called for {}.", dokanyFileInfo.Context, path);
		if (fac.exists(dokanyFileInfo.Context)) {
			LOG.debug("({}) Resource {} was not cleanuped. Closing handle now.", dokanyFileInfo.Context, path);
			try {
				fac.close(dokanyFileInfo.Context);
			} catch (IOException e) {
				LOG.warn("({}) closeFile(): Unable to close handle to resource {}. To close it please restart the adapter.", dokanyFileInfo.Context, path);
				LOG.warn("closeFile():", e);
			}
		}
		dokanyFileInfo.Context = 0;
	}

	@Override
	public int readFile(WString rawPath, Pointer rawBuffer, int rawBufferLength, IntByReference rawReadLength, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) readFile() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("readFile(): Invalid handle to {} ", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.debug("({}) {} is a directory. Unable to read Data from it.", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		}

		long usedHandleID = dokanyFileInfo.Context;
		boolean reopened = false;
		OpenFile handle = (OpenFile) fac.get(usedHandleID);
		if (handle == null) {
			LOG.debug("({}) readFile(): Unable to find handle for {}. Possible already cleanup() called. Try to reopen it.", usedHandleID, path);
			try {
				usedHandleID = fac.openFile(path, Collections.singleton(StandardOpenOption.READ));
				handle = (OpenFile) fac.get(usedHandleID);
				LOG.trace("({}) readFile(): Successful reopened {} with intermediate handle id {}.", dokanyFileInfo.Context, path, usedHandleID);
				reopened = true;
			} catch (IOException e1) {
				LOG.debug("({}) readFile(): Reopen of {} failed. Aborting.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_OPEN_FAILED.getMask();
			}
		}

		assert handle != null;
		try (PathLock pathLock = lockManager.createPathLock(path.toString()).forReading();
			 DataLock dataLock = pathLock.lockDataForReading()) {
			rawReadLength.setValue(handle.read(rawBuffer, rawBufferLength, rawOffset));
			LOG.trace("({}) Data successful read from {}.", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		} catch (NonReadableChannelException e) {
			LOG.trace("({}) readFile(): File {} not opened for reading.", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} catch (IOException e) {
			LOG.debug("({}) readFile(): IO error while reading file {}.", dokanyFileInfo.Context, path);
			LOG.debug("Error is:", e);
			return Win32ErrorCode.ERROR_READ_FAULT.getMask();
		} finally {
			if (reopened) {
				try {
					fac.close(usedHandleID);
					LOG.trace("({}) readFile(): Successful closed REOPENED file {}.", dokanyFileInfo.Context, path);
				} catch (IOException e) {
					LOG.debug("({}) readFile(): IO error while closing REOPENED file {}. File will be closed on exit.", dokanyFileInfo.Context, path);
				}
			}
		}
	}

	@Override
	public int writeFile(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) writeFile() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("writeFile(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.debug("({}) {} is a directory. Unable to write Data to it.", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		}

		long usedHandleID = dokanyFileInfo.Context;
		boolean reopened = false;
		OpenFile handle = (OpenFile) fac.get(usedHandleID);
		if (handle == null) {
			LOG.debug("({}) writeFile(): Unable to find handle for {}. Possible already cleanup() called. Try to reopen it.", dokanyFileInfo.Context, path);
			try {
				usedHandleID = fac.openFile(path, Collections.singleton(StandardOpenOption.WRITE));
				handle = (OpenFile) fac.get(usedHandleID);
				LOG.trace("({}) writeFile(): Successful reopened {} with handle {}.", dokanyFileInfo.Context, path, usedHandleID);
				reopened = true;
			} catch (IOException e1) {
				LOG.debug("({}) writeFile(): Reopen of {} failed. Aborting.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_OPEN_FAILED.getMask();
			}
		}

		try (PathLock pathLock = lockManager.createPathLock(path.toString()).forReading();
			 DataLock dataLock = pathLock.lockDataForWriting()) {
			if (dokanyFileInfo.writeToEndOfFile()) {
				rawNumberOfBytesWritten.setValue(handle.append(rawBuffer, rawNumberOfBytesToWrite));
			} else {
				rawNumberOfBytesWritten.setValue(handle.write(rawBuffer, rawNumberOfBytesToWrite, rawOffset));
			}
			LOG.trace("({}) Data successful written to {}.", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		} catch (NonWritableChannelException e) {
			LOG.trace("({}) File {} not opened for writing.", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} catch (IOException e) {
			LOG.debug("({}) writeFile(): IO Error while writing to {}.", dokanyFileInfo.Context, path);
			LOG.debug("Error is:", e);
			return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
		} finally {
			if (reopened) {
				try {
					fac.close(usedHandleID);
					LOG.trace("({}) writeFile(): Successful closed REOPENED file {}.", dokanyFileInfo.Context, path);
				} catch (IOException e) {
					LOG.debug("({}) writeFile(): IO error while closing REOPENED file {}. File will be closed on exit.", dokanyFileInfo.Context, path);
				}
			}
		}
	}

	@Override
	public int flushFileBuffers(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) flushFileBuffers() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("flushFileBuffers(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.debug("({}) {} is a directory. Unable to write data to it.", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			try {
				((OpenFile) handle).flush();
				LOG.trace("Flushed successful to {} with handle {}.", path, dokanyFileInfo.Context);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (IOException e) {
				LOG.debug("({}) flushFileBuffers(): IO Error while flushing to {}.", dokanyFileInfo.Context, path, e);
				LOG.debug("flushFileBuffers(): ", e);
				return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
		}
	}

	/**
	 * @param fileName
	 * @param handleFileInfo
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return
	 */
	@Override
	public int getFileInformation(WString fileName, ByHandleFileInfo handleFileInfo, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(fileName);
		LOG.trace("({}) getFileInformation() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("getFileInformation(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try (PathLock pathLock = lockManager.createPathLock(path.toString()).forReading();
				 DataLock dataLock = pathLock.lockDataForReading()) {
				DosFileAttributes attr = Files.readAttributes(path, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
				LOG.trace("({}) Filesize of {} is {}.", dokanyFileInfo.Context, path, attr.size());
				FullFileInfo data = toFullFileInfo(path, attr);
				data.copyTo(handleFileInfo);
				LOG.trace("({}) File Information successful read from {}.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (NoSuchFileException e) {
				LOG.trace("({}) Resource {} not found.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.debug("({}) getFileInformation(): IO error occurred reading meta data from {}.", dokanyFileInfo.Context, path);
				LOG.debug("getFileInformation(): ", e);
				return Win32ErrorCode.ERROR_READ_FAULT.getMask();
			}
		}
	}

	private FullFileInfo toFullFileInfo(Path path, DosFileAttributes attr) {
		long index = 0;
		if (attr.fileKey() != null) {
			index = (long) attr.fileKey(); // known to be a long for DosFileAttributes
		}
		Path filename = path.getFileName();
		FullFileInfo data = new FullFileInfo(filename != null ? filename.toString() : "", //case distinction necessary, because the root has no name!
				index,
				FileUtil.dosAttributesToEnumIntegerSet(attr),
				0, //currently just a stub
				DokanyUtils.getTime(attr.creationTime().toMillis()),
				DokanyUtils.getTime(attr.lastAccessTime().toMillis()),
				DokanyUtils.getTime(attr.lastModifiedTime().toMillis()));
		data.setSize(attr.size());
		return data;
	}

	@Override
	public int findFiles(WString rawPath, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		assert path.isAbsolute();
		LOG.trace("({}) findFiles() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("findFiles(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try (PathLock pathLock = lockManager.createPathLock(path.toString()).forReading();
				 DataLock dataLock = pathLock.lockDataForReading();
				 DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
				Spliterator<Path> spliterator = Spliterators.spliteratorUnknownSize(ds.iterator(), Spliterator.DISTINCT);
				StreamSupport.stream(spliterator, false)
						.map(p -> {
							assert p.isAbsolute();
							try {
								DosFileAttributes attr = Files.readAttributes(p, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
								if (attr.isDirectory() || attr.isRegularFile()) {
									return toFullFileInfo(p, attr).toWin32FindData();
								} else {
									LOG.warn("({}) findFiles(): Found node that is neither directory nor file: {}. Will be ignored in file listing.", dokanyFileInfo.Context, p);
									return null;
								}
							} catch (IOException e) {
								LOG.debug("({}) findFiles(): IO error accessing {}. Will be ignored in file listing.", dokanyFileInfo.Context, p);
								return null;
							}
						})
						.filter(Objects::nonNull)
						.forEach(file -> {
							assert file != null;
							LOG.trace("({}) findFiles(): found file {}", dokanyFileInfo.Context, file.getFileName());
							rawFillFindData.fillWin32FindData(file, dokanyFileInfo);
						});
				LOG.trace("({}) Successful searched content in {}.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (IOException e) {
				LOG.debug("({}) findFiles(): Unable to list content of directory {}.", dokanyFileInfo.Context, path);
				LOG.debug("(" + dokanyFileInfo.Context + ") findFiles(): Message and Stacktrace.", e);
				return Win32ErrorCode.ERROR_READ_FAULT.getMask();
			}
		}
	}

	/**
	 * @param fileName
	 * @param searchPattern
	 * @param rawFillFindData
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return
	 * @deprecated This method is not used anymore, since Windows has additional globbing characters which makes file name matching difficult. (for more Information, see the <a href="https://github.com/cryptomator/dokany-nio-adapter/issues/19">corresponding github issue</a>)
	 */
	@Override
	@Deprecated
	public int findFilesWithPattern(WString fileName, WString searchPattern, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(fileName);
		assert path.isAbsolute();
		LOG.trace("({}) findFilesWithPattern() is called for {} with search pattern {}.", dokanyFileInfo.Context, path, searchPattern.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("findFilesWithPattern(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			final DirectoryStream.Filter<Path> filter;
			if (searchPattern == null || searchPattern.toString().equals("*")) {
				filter = (Path p) -> true;  // match all
			} else {
				// we want to filter by glob
				// since the Java API does NOT specify on which string representation a pathMatcher compares a path to a given expression, we assume NFC
				String nfcSearchPattern = Normalizer.normalize(FileUtil.convertToGlobPattern(searchPattern.toString()), Normalizer.Form.NFC);
				PathMatcher matcher = path.getFileSystem().getPathMatcher("glob:" + nfcSearchPattern);
				filter = (Path p) -> matcher.matches(p.getFileName());
			}
			try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, filter)) {
				Spliterator<Path> spliterator = Spliterators.spliteratorUnknownSize(ds.iterator(), Spliterator.DISTINCT);
				Stream<Path> stream = StreamSupport.stream(spliterator, false);
				stream.map(p -> {
					assert p.isAbsolute();
					try (PathLock pathLock = lockManager.createPathLock(path.toString()).forReading();
						 DataLock dataLock = pathLock.lockDataForReading()) {
						DosFileAttributes attr = Files.readAttributes(p, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
						if (attr.isDirectory() || attr.isRegularFile()) {
							return toFullFileInfo(p, attr).toWin32FindData();
						} else {
							LOG.warn("({}) findFilesWithPattern(): Found node that is neither directory nor file: {}. Will be ignored in file listing.", dokanyFileInfo.Context, p);
							return null;
						}
					} catch (IOException e) {
						LOG.debug("({}) findFilesWithPattern(): IO error accessing {}. Will be ignored in file listing.", dokanyFileInfo.Context, p);
						return null;
					}
				}).filter(Objects::nonNull)
						.forEach(file -> {
							assert file != null;
							try {
								LOG.trace("({}) findFilesWithPattern(): found file {}", dokanyFileInfo.Context, file.getFileName());
								rawFillFindData.fillWin32FindData(file, dokanyFileInfo);
							} catch (Error e) {
								//TODO: invalid memory access can happen, which is an Java.Lang.Error
								LOG.error("({}) Error filling Win32FindData with file {}. Occurred error is {}", dokanyFileInfo.Context, file.getFileName());
								LOG.error("(" + dokanyFileInfo.Context + ") findFilesWithPattern(): Stacktrace.", e);
							}
						});
				LOG.trace("({}) Successful searched content in {}.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (IOException e) {
				LOG.error("({}) findFilesWithPattern(): Unable to list content of directory {}.", dokanyFileInfo.Context, path);
				LOG.error("(" + dokanyFileInfo.Context + ") findFilesWithPattern(): Message and Stacktrace.", e);
				return Win32ErrorCode.ERROR_READ_FAULT.getMask();
			}
		}
	}

	@Override
	public int setFileAttributes(WString rawPath, int rawAttributes, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) setFileAttributes() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("setFileAttribute(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try (PathLock pathLock = lockManager.createPathLock(path.toString()).forReading();
				 DataLock dataLock = pathLock.lockDataForWriting()) {
				setFileAttributes(path, rawAttributes);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (NoSuchFileException e) {
				LOG.trace("({}) setFileAttributes(): File {} not found.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.debug("({}) setFileAttributes(): IOException occurred during operation on {}.", dokanyFileInfo.Context, path);
				LOG.debug("setFileAttributes(): Stacktrace", e);
				return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
		}
	}

	private void setFileAttributes(Path path, int rawAttributes) throws IOException {
		DosFileAttributeView attrView = Files.getFileAttributeView(path, DosFileAttributeView.class);
		EnumIntegerSet<FileAttribute> attrsToUnset = DokanyUtils.enumSetFromInt(Integer.MAX_VALUE, FileUtil.supportedAttributeValuesToSet);
		EnumIntegerSet<FileAttribute> attrsToSet = DokanyUtils.enumSetFromInt(rawAttributes, FileAttribute.values());
		if (rawAttributes == 0) {
			// case FileAttributes == 0 :
			// MS-FSCC 2.6 File Attributes : There is no file attribute with the value 0x00000000
			// because a value of 0x00000000 in the FileAttributes field means that the file attributes for this file MUST NOT be changed when setting basic information for the file
			// NO-OP
		} else if ((rawAttributes & FileAttribute.NORMAL.getMask()) != 0 && (rawAttributes - FileAttribute.NORMAL.getMask() == 0)) {
			//contains only the NORMAL attribute
			//removes all removable fields
			for (FileAttribute attr : attrsToUnset) {
				FileUtil.setAttribute(attrView, attr, false);
			}
		} else {
			attrsToSet.remove(FileAttribute.NORMAL);
			for (FileAttribute attr : attrsToSet) {
				FileUtil.setAttribute(attrView, attr, true);
				attrsToUnset.remove(attr);
			}

			for (FileAttribute attr : attrsToUnset) {
				FileUtil.setAttribute(attrView, attr, false);
			}

		}
	}

	@Override
	public int setFileTime(WString rawPath, WinBase.FILETIME rawCreationTime, WinBase.FILETIME rawLastAccessTime, WinBase.FILETIME rawLastWriteTime, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) setFileTime() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("setFileTime(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try (PathLock pathLock = lockManager.createPathLock(path.toString()).forReading();
				 DataLock dataLock = pathLock.lockDataForWriting()) {
				FileTime lastModifiedTime = FileUtil.toFileTime(rawLastWriteTime).orElse(null);
				FileTime lastAccessTime = FileUtil.toFileTime(rawLastAccessTime).orElse(null);
				FileTime createdTime = FileUtil.toFileTime(rawCreationTime).orElse(null);
				Files.getFileAttributeView(path, BasicFileAttributeView.class).setTimes(lastModifiedTime, lastAccessTime, createdTime);
				LOG.trace("({}) Successful updated Filetime for {}.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (NoSuchFileException e) {
				LOG.trace("({}) File {} not found.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.debug("({}) setFileTime(): IO error occurred accessing {}.", dokanyFileInfo.Context, path);
				LOG.debug("setFileTime(): ", e);
				return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
		}
	}

	@Override
	public int deleteFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) deleteFile() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("deleteFile(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.debug("({}) {} is a directory. Unable to delete via deleteFile()", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} else {
			try (PathLock pathLock = lockManager.createPathLock(path.toString()).forWriting();
				 DataLock dataLock = pathLock.lockDataForWriting()) {
				//TODO: race condition with handle == null possible?
				OpenHandle handle = fac.get(dokanyFileInfo.Context);
				if (Files.exists(path)) {
					//TODO: what is the best condition for the deletion? and is this case analysis correct?
					if (((OpenFile) handle).canBeDeleted()) {
						LOG.trace("({}) Deletion of {} possible.", dokanyFileInfo.Context, path);
						return Win32ErrorCode.ERROR_SUCCESS.getMask();
					} else {
						LOG.trace("({}) Deletion of {} not possible.", dokanyFileInfo.Context, path);
						return Win32ErrorCode.ERROR_BUSY.getMask();
					}
				} else {
					LOG.debug("({}) deleteFile(): {} not found.", dokanyFileInfo.Context, path);
					return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
				}
			}
		}
	}

	@Override
	public int deleteDirectory(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) deleteDirectory() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("deleteDirectory(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (!dokanyFileInfo.isDirectory()) {
			LOG.debug("({}) {} is a file. Unable to delete via deleteDirectory()", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} else {
			try (PathLock pathLock = lockManager.createPathLock(path.toString()).forWriting();
				 DataLock dataLock = pathLock.lockDataForWriting()) {
				//TODO: check for directory existence
				//TODO: race condition with handle == null possible?
				try (DirectoryStream emptyCheck = Files.newDirectoryStream(path)) {
					if (emptyCheck.iterator().hasNext()) {
						LOG.trace("({}) Deletion of {} not possible.", dokanyFileInfo.Context, path);
						return Win32ErrorCode.ERROR_DIR_NOT_EMPTY.getMask();
					} else {
						LOG.trace("({}) Deletion of {} possible.", dokanyFileInfo.Context, path);
						return Win32ErrorCode.ERROR_SUCCESS.getMask();
					}
				} catch (IOException e) {
					LOG.debug("({}) deleteDirectory(): IO error occurred reading {}.", dokanyFileInfo.Context, path);
					LOG.debug("deleteDirectory(): ", e);
					return Win32ErrorCode.ERROR_CURRENT_DIRECTORY.getMask();
				}
			}
		}
	}

	@Override
	public int moveFile(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		Path newPath = getRootedPath(rawNewFileName);
		LOG.trace("({}) moveFile() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("moveFile(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try (PathLock oldPathLock = lockManager.createPathLock(path.toString()).forWriting();
				 DataLock oldDataLock = oldPathLock.lockDataForWriting();
				 PathLock newPathLock = lockManager.createPathLock(newPath.toString()).forWriting();
				 DataLock newDataLock = newPathLock.lockDataForWriting()) {
				if (rawReplaceIfExisting) {
					Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
				} else {
					Files.move(path, newPath);
				}
				LOG.trace("({}) Successful moved resource {} to {}.", dokanyFileInfo.Context, path, newPath);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (FileAlreadyExistsException e) {
				LOG.trace("({}) Resource {} already exists at {}.", dokanyFileInfo.Context, path, newPath);
				return Win32ErrorCode.ERROR_FILE_EXISTS.getMask();
			} catch (DirectoryNotEmptyException e) {
				LOG.trace("({}) Target directory {} is not emtpy.", dokanyFileInfo.Context, path);
				return Win32ErrorCode.ERROR_DIR_NOT_EMPTY.getMask();
			} catch (FileSystemException e) {
				final String reason = e.getReason().toLowerCase();
				if (reason.contains("path too long") || reason.contains("extension too long") || reason.contains("path too long")) {
					LOG.warn("({}) Moving resource {} failed, file name too long.", dokanyFileInfo.Context, path);
					return Win32ErrorCode.ERROR_FILENAME_EXCED_RANGE.getMask();
				} else {
					LOG.debug("({}) moveFile(): IO error while moving resource {}.", dokanyFileInfo.Context, path);
					LOG.debug("moveFile(): ", e);
					return Win32ErrorCode.ERROR_GEN_FAILURE.getMask();
				}
			} catch (IOException e) {
				LOG.debug("({}) moveFile(): IO error while moving resource {}.", dokanyFileInfo.Context, path);
				LOG.debug("moveFile(): ", e);
				return Win32ErrorCode.ERROR_GEN_FAILURE.getMask();
			}
		}
	}

	@Override
	public int setEndOfFile(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) setEndOfFile() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanyFileInfo.Context == 0) {
			LOG.debug("setEndOfFile(): Invalid handle to {}.", path);
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.debug("({}) setEndOfFile(): {} is a directory. Unable to truncate.", dokanyFileInfo.Context, path);
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} else {
			try (PathLock pathLock = lockManager.createPathLock(path.toString()).forReading();
				 DataLock dataLock = pathLock.lockDataForWriting()) {
				OpenHandle handle = fac.get(dokanyFileInfo.Context);
				((OpenFile) handle).truncate(rawByteOffset);
				LOG.trace("({}) Successful truncated {} to size {}.", dokanyFileInfo.Context, path, rawByteOffset);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (IOException e) {
				LOG.debug("({}) setEndOfFile(): IO error while truncating {}.", dokanyFileInfo.Context, path);
				LOG.debug("setEndOfFile(): ", e);
				return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
		}
	}

	@Override
	public int setAllocationSize(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) setAllocationSize() is called for {}.", dokanyFileInfo.Context, path);
		return setEndOfFile(rawPath, rawLength, dokanyFileInfo);
	}

	@Override
	public int lockFile(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public int unlockFile(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public int getDiskFreeSpace(LongByReference freeBytesAvailable, LongByReference totalNumberOfBytes, LongByReference totalNumberOfFreeBytes, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("getFreeDiskSpace() is called.");
		try {
			totalNumberOfBytes.setValue(fileStore.getTotalSpace());
			freeBytesAvailable.setValue(fileStore.getUsableSpace());
			totalNumberOfFreeBytes.setValue(fileStore.getUnallocatedSpace());
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		} catch (IOException e) {
			LOG.debug("({}) getFreeDiskSpace(): Unable to detect disk space status.", dokanyFileInfo.Context, e);
			return Win32ErrorCode.ERROR_READ_FAULT.getMask();
		}
	}

	/**
	 * TODO: this method is copy pasta. Check it!
	 *
	 * @param rawVolumeNameBuffer
	 * @param rawVolumeNameSize
	 * @param rawVolumeSerialNumber
	 * @param rawMaximumComponentLength
	 * @param rawFileSystemFlags
	 * @param rawFileSystemNameBuffer
	 * @param rawFileSystemNameSize
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return
	 */
	@Override
	public int getVolumeInformation(Pointer rawVolumeNameBuffer, int rawVolumeNameSize, IntByReference rawVolumeSerialNumber, IntByReference rawMaximumComponentLength, IntByReference rawFileSystemFlags, Pointer rawFileSystemNameBuffer, int rawFileSystemNameSize, DokanyFileInfo dokanyFileInfo) {
		try {
			rawVolumeNameBuffer.setWideString(0L, DokanyUtils.trimStrToSize(volumeInformation.getName(), rawVolumeNameSize));
			rawVolumeSerialNumber.setValue(volumeInformation.getSerialNumber());
			rawMaximumComponentLength.setValue(volumeInformation.getMaxComponentLength());
			rawFileSystemFlags.setValue(volumeInformation.getFileSystemFeatures().toInt());
			rawFileSystemNameBuffer.setWideString(0L, DokanyUtils.trimStrToSize(volumeInformation.getFileSystemName(), rawFileSystemNameSize));
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		} catch (Throwable t) {
			return Win32ErrorCode.ERROR_READ_FAULT.getMask();
		}
	}

	@Override
	public int mounted(DokanyFileInfo dokanyFileInfo) {
		LOG.trace("mounted() is called.");
		didMount.complete(null);
		return 0;
	}

	@Override
	public int unmounted(DokanyFileInfo dokanyFileInfo) {
		LOG.trace("unmounted() is called.");
		return 0;
	}

	@Override
	public int getFileSecurity(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, IntByReference rawSecurityDescriptorLengthNeeded, DokanyFileInfo dokanyFileInfo) {
//		Path path = getRootedPath(rawPath);
//		//SecurityInformation securityInfo = DokanyUtils.enumFromInt(rawSecurityInformation, SecurityInformation.values());
//		//LOG.debug("getFileSecurity() is called for {} {}", path.toString(), securityInfo.name());
//		if (Files.exists(path)) {
//			byte[] securityDescriptor = FileUtil.getStandardSecurityDescriptor();
//			rawSecurityDescriptorLengthNeeded.setValue(securityDescriptor.length);
//			if (securityDescriptor.length <= rawSecurityDescriptorLength) {
//				rawSecurityDescriptor.write(0L, securityDescriptor, 0, securityDescriptor.length);
//				return Win32ErrorCode.SUCCESS.getMask();
//			} else {
//				return Win32ErrorCode.BUFFER_OVERFLOW.getMask();
//			}
//		} else {
//			return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
//		}
		return 0;
	}

	@Override
	public int setFileSecurity(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, DokanyFileInfo dokanyFileInfo) {
//		Path path = getRootedPath(rawPath);
//		LOG.trace("setFileSecurity() is called for " + path.toString());
//		if (Files.exists(path)) {
//			byte[] securityDescriptor = FileUtil.getStandardSecurityDescriptor();
//			if (securityDescriptor.length <= rawSecurityDescriptorLength) {
//				rawSecurityDescriptor.write(0L, securityDescriptor, 0, securityDescriptor.length);
//				return Win32ErrorCode.SUCCESS.getMask();
//			} else {
//				return Win32ErrorCode.BUFFER_OVERFLOW.getMask();
//			}
//		} else {
//			return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
//		}
		return 0;
	}

	@Override
	public void fillWin32FindData(WinBase.WIN32_FIND_DATA rawFillFindData, DokanyFileInfo dokanyFileInfo) {

	}

	@Override
	public int findStreams(WString rawPath, DokanyOperations.FillWin32FindStreamData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	private Path getRootedPath(WString rawPath) {
		String unixPath = rawPath.toString().replace('\\', '/');
		String relativeUnixPath = CharMatcher.is('/').trimLeadingFrom(unixPath);
		assert root.isAbsolute();
		return root.resolve(relativeUnixPath);
	}
}
