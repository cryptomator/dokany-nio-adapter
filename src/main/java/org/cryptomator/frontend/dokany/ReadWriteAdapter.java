package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyOperations;
import com.dokany.java.DokanyUtils;
import com.dokany.java.constants.CreateOptions;
import com.dokany.java.constants.CreationDisposition;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.constants.Win32ErrorCode;
import com.dokany.java.structure.ByHandleFileInfo;
import com.dokany.java.structure.DokanyFileInfo;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.FullFileInfo;
import com.dokany.java.structure.VolumeInformation;
import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.Files;
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
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * TODO: Beware of DokanyUtils.enumSetFromInt()!!!
 */
public class ReadWriteAdapter implements DokanyFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(ReadWriteAdapter.class);

	private final Path root;
	private final VolumeInformation volumeInformation;
	private final CompletableFuture didMount;
	private final OpenHandleFactory fac;
	private final FileStore fileStore;

	public ReadWriteAdapter(Path root, VolumeInformation volumeInformation, CompletableFuture<?> didMount) {
		this.root = root;
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
		//TODO: this should be removed in later versions
		if (isSkipFile(rawPath)) {
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		CreationDisposition creationDisposition = CreationDisposition.fromInt(rawCreateDisposition);
		LOG.debug("zwCreateFile() is called for {} with CreationDisposition {}.", path.toString(), creationDisposition.name());

		Optional<BasicFileAttributes> attr;
		try {
			attr = Optional.of(Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS));
		} catch (IOException e) {
			attr = Optional.empty();
		}

		//is the file a directory and if yes, indicated as one?
		EnumIntegerSet<CreateOptions> createOptions = DokanyUtils.enumSetFromInt(rawCreateOptions, CreateOptions.values());
		if (attr.isPresent() && attr.get().isDirectory()) {
			if ((rawCreateOptions & CreateOptions.FILE_NON_DIRECTORY_FILE.getMask()) == 0) {
				dokanyFileInfo.IsDirectory = 0x01;
				//TODO: set the share access like in the dokany mirror example
			} else {
				LOG.trace("Ressource {} is a Directory and cannot be opened as a file.");
				//TODO: maybe other error code? e.g. ACCESS DENIED
				return Win32ErrorCode.ERROR_GEN_FAILURE.getMask();
			}
		}

		if (dokanyFileInfo.isDirectory()) {
			return createDirectory(path, creationDisposition, rawFileAttributes, dokanyFileInfo);
		} else {
			Set<OpenOption> openOptions = Sets.newHashSet();
			openOptions.add(StandardOpenOption.READ);
			//TODO: mantle this statement with an if-statement which checks for write protection!
			openOptions.add(StandardOpenOption.WRITE);
			//TODO: ca we leave this check out?
			if (attr.isPresent()) {
				switch (creationDisposition) {
					case CREATE_NEW:
						//FAILS
						openOptions.add(StandardOpenOption.CREATE_NEW);
						break;
					case CREATE_ALWAYS:
						openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
						break;
					case OPEN_EXISTING:
						//SUCCESS
						break;
					case OPEN_ALWAYS:
						openOptions.add(StandardOpenOption.CREATE);
						break;
					case TRUNCATE_EXISTING:
						openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
						break;
					default:
						throw new IllegalStateException("Unknown createDispostion attribute: " + creationDisposition.name());
				}
			} else {
				switch (creationDisposition) {
					case CREATE_NEW:
						openOptions.add(StandardOpenOption.CREATE_NEW);
						break;
					case CREATE_ALWAYS:
						openOptions.add(StandardOpenOption.CREATE);
						break;
					case OPEN_EXISTING:
						//FAILS
						//return
						break;
					case OPEN_ALWAYS:
						openOptions.add(StandardOpenOption.CREATE);
						break;
					case TRUNCATE_EXISTING:
						openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
						break;
					default:
						throw new IllegalStateException("Unknown createDispostion attribute: " + creationDisposition.name());
				}
			}

			if (dokanyFileInfo.writeToEndOfFile()) {
				openOptions.add(StandardOpenOption.APPEND);
			}
			return createFile(path, creationDisposition, openOptions, rawFileAttributes, dokanyFileInfo);
		}


	}


	/**
	 * @return
	 */
	private int createDirectory(Path path, CreationDisposition creationDisposition, int rawFileAttributes, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("Try to open {} as Directory.", path.toString());
		final int mask = creationDisposition.getMask();
		//createDirectory request
		if (mask == CreationDisposition.CREATE_NEW.getMask() || mask == CreationDisposition.OPEN_ALWAYS.getMask()) {
			// TODO: rename current method "createDirectory" to "openDirectory" and extract following part to new private method "createDirectory"
			try {
				Files.createDirectory(path);
				LOG.trace("Directory {} successful created ", path.toString());
			} catch (FileAlreadyExistsException e) {
				if (mask == CreationDisposition.CREATE_NEW.getMask()) {
					//we got create_new flag -> there should be nuthing, but there is somthin!
					LOG.trace("Directory {} already exists.", path.toString());
					return Win32ErrorCode.ERROR_ALREADY_EXISTS.getMask();
				}
			} catch (IOException e) {
				//we dont know what the hell happened
				LOG.info("zwCreateFile(): IO error occured during the creation of {}.", path.toString());
				LOG.debug("zwCreateFile(): ", e);
				return Win32ErrorCode.ERROR_CANNOT_MAKE.getMask();
			}
		}

		//some otha flags were used
		//here we check if we try to open a file as a directory
		if (Files.isRegularFile(path)) {
			//sh*t
			LOG.trace("Attempt to open file {} as a directory.", path.toString());
			return Win32ErrorCode.ERROR_DIRECTORY.getMask();
		} else {
			// we open the directory in some kinda way
			setFileAttributes(path, rawFileAttributes);
			dokanyFileInfo.Context = fac.openDir(path);
			LOG.trace("({}) {} opened successful with handle {}.", dokanyFileInfo.Context, path.toString(), dokanyFileInfo.Context);
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}
	}

	private int createFile(Path path, CreationDisposition creationDisposition, Set<OpenOption> openOptions, int rawFileAttributes, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("Try to open {} as File.", path.toString());
		final int mask = creationDisposition.getMask();
		DosFileAttributes attr = null;
		try {
			attr = Files.readAttributes(path, DosFileAttributes.class);
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
			LOG.trace("{} is hidden or system file. Unable to overwrite.", path.toString());
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		}
		//read-only?
		else if ((attr != null && attr.isReadOnly() || ((rawFileAttributes & FileAttribute.READONLY.getMask()) != 0))
				&& dokanyFileInfo.DeleteOnClose != 0
				) {
			//cannot overwrite file
			LOG.trace("{} is readonly. Unable to overwrite.", path.toString());
			return Win32ErrorCode.ERROR_FILE_READ_ONLY.getMask();
		} else {
			try {
				dokanyFileInfo.Context = fac.openFile(path, openOptions);
				setFileAttributes(path, rawFileAttributes);
				LOG.trace("({}) {} opened successful with handle {}.", dokanyFileInfo.Context, path.toString(), dokanyFileInfo.Context);
				//required by contract
				Win32ErrorCode returnCode;
				if (attr != null && (mask == CreationDisposition.OPEN_ALWAYS.getMask() || mask == CreationDisposition.CREATE_ALWAYS.getMask())) {
					returnCode = Win32ErrorCode.ERROR_ALREADY_EXISTS;
				} else {
					returnCode = Win32ErrorCode.ERROR_SUCCESS;
				}
				return returnCode.getMask();
			} catch (FileAlreadyExistsException e) {
				LOG.trace("Unable to open {}.", path.toString());
				return Win32ErrorCode.ERROR_FILE_EXISTS.getMask();
			} catch (NoSuchFileException e) {
				LOG.trace("{} not found.", path.toString());
				return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (IOException e) {
				if (attr != null) {
					LOG.info("zwCreateFile(): IO error occurred during opening handle to {}.", path.toString());
					LOG.debug("zwCreateFile(): ", e);
					return Win32ErrorCode.ERROR_OPEN_FAILED.getMask();
				} else {
					LOG.info("zwCreateFile(): IO error occurred during creation of {}.", path.toString());
					LOG.debug("zwCreateFile(): ", e);
					return Win32ErrorCode.ERROR_CANNOT_MAKE.getMask();
				}
			} catch (IllegalArgumentException e) {
				LOG.warn("createFile(): Exception occured:", e);
				LOG.warn("{} seems to be modified by another source.", path.toString());
				dokanyFileInfo.Context = fac.openRestrictedFile(path);
				LOG.warn("({}) {} opended in restricted mode with handle {}.", dokanyFileInfo.Context, path.toString(), dokanyFileInfo.Context);
				//TODO: is this correct?
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
		if (isSkipFile(rawPath)) {
			return;
		}
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) cleanup() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("cleanup(): Invalid handle to {}.", path.toString());
		} else {
			try {
				fac.close(dokanyFileInfo.Context);
				if (dokanyFileInfo.deleteOnClose()) {
					try {
						Files.delete(path);
						LOG.trace("({}) {} successful deleted.", dokanyFileInfo.Context, path.toString());
					} catch (DirectoryNotEmptyException e) {
						LOG.trace("({}) Directory {} not empty.", dokanyFileInfo.Context, path.toString());
					} catch (IOException e) {
						LOG.info("({}) cleanup(): IO error during deletion of {} ", dokanyFileInfo.Context, path.toString(), e);
						LOG.debug("cleanup(): ", e);
					}
				}
			} catch (IOException e) {
				LOG.warn("({}) cleanup(): Unable to close handle to {}", dokanyFileInfo.Context, path.toString(), e);
				LOG.debug("cleanup(): ", e);
			}
		}
	}

	@Override
	public void closeFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return;
		}
		Path p = getRootedPath(rawPath);
		LOG.debug("({}) closeFile() is called for {}.", dokanyFileInfo.Context, p.toString());
		if (fac.exists(dokanyFileInfo.Context)) {
			LOG.info("({}) Resource {} was not cleanuped. Closing handle now.", dokanyFileInfo.Context, p.toString());
			try {
				fac.close(dokanyFileInfo.Context);
			} catch (IOException e) {
				LOG.warn("({}) closeFile(): Unable to close handle to resource {}. To close it please restart the adapter.", dokanyFileInfo.Context, p.toString());
				LOG.debug("closeFile():", e);
			}
		}
		dokanyFileInfo.Context = 0;
	}

	@Override
	public int readFile(WString rawPath, Pointer rawBuffer, int rawBufferLength, IntByReference rawReadLength, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) readFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("readFile(): Invalid handle to {} ", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.trace("({}) {} is a directory. Unable to read Data from it.", dokanyFileInfo.Context, path.toString());
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		}

		long handleID = dokanyFileInfo.Context;
		boolean reopened = false;
		OpenFile handle = (OpenFile) fac.get(handleID);
		if (handle == null) {
			LOG.trace("({}) readFile(): Unable to find handle for {}. Try to reopen it.", handleID, getRootedPath(rawPath).toString());
			try {
				handleID = fac.openFile(path, Collections.singleton(StandardOpenOption.READ));
				handle = (OpenFile) fac.get(handleID);
				LOG.trace("readFile(): Successful reopened {} with handle {}.", path.toString(), handleID);
				reopened = true;
			} catch (IOException e1) {
				LOG.debug("readFile(): Reopen of {} failed. Aborting.", path.toString());
				return Win32ErrorCode.ERROR_OPEN_FAILED.getMask();
			}
		}

		try {
			rawReadLength.setValue(handle.read(rawBuffer, rawBufferLength, rawOffset));
			LOG.trace("({}) Data successful read from {}.", handleID, path.toString());
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		} catch (IOException e) {
			LOG.info("({}) readFile(): IO error while reading file {}.", handleID, path.toString());
			LOG.info("Error is:", e);
			return Win32ErrorCode.ERROR_READ_FAULT.getMask();
		} finally {
			if (reopened) {
				try {
					handle.close();
					LOG.trace("({}) readFile(): Successful closed REOPENED file {}.", handleID, path.toString());
				} catch (IOException e) {
					LOG.info("({}) readFile(): IO error while closing REOPENED file {}. File will be closed on exit.", handleID, path.toString());
				}
			}
		}
	}

	@Override
	public int writeFile(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("({}) writeFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("writeFile(): Invalid handle to {}", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.trace("({}) {} is a directory. Unable to write Data to it.", dokanyFileInfo.Context, path.toString());
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		}

		long handleID = dokanyFileInfo.Context;
		boolean reopened = false;
		OpenFile handle = (OpenFile) fac.get(handleID);
		if (handle == null) {
			LOG.trace("({}) writeFile(): Unable to find handle for {}. Try to reopen it.", handleID, getRootedPath(rawPath).toString());
			try {
				handleID = fac.openFile(path, Collections.singleton(StandardOpenOption.WRITE));
				handle = (OpenFile) fac.get(handleID);
				LOG.trace("writeFile(): Successful reopened {} with handle {}.", path.toString(), handleID);
				reopened = true;
			} catch (IOException e1) {
				LOG.debug("writeFile(): Reopen of {} failed. Aborting.", path.toString());
				return Win32ErrorCode.ERROR_OPEN_FAILED.getMask();
			}
		}

		try {
			rawNumberOfBytesWritten.setValue(handle.write(rawBuffer, rawNumberOfBytesToWrite, rawOffset));
			LOG.trace("({}) Data successful written to {}.", handleID, path.toString());
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		} catch (IOException e) {
			LOG.info("({}) writeFile(): IO Error while writing to {} ", handleID, path.toString(), e);
			return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
		} finally {
			if (reopened) {
				try {
					handle.close();
					LOG.trace("({}) writeFile(): Successful closed REOPENED file {}.", handleID, path.toString());
				} catch (IOException e) {
					LOG.info("({}) writeFile(): IO error while closing REOPENED file {}. File will be closed on exit.", handleID, path.toString());
				}
			}
		}
	}

	@Override
	public int flushFileBuffers(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) flushFileBuffers() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("flushFileBuffers(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.trace("({}) {} is a directory. Unable to write Data to it.", dokanyFileInfo.Context, path.toString());
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			try {
				((OpenFile) handle).flush();
				LOG.trace("Flushed successful to {} with handle {}.", path.toString(), dokanyFileInfo.Context);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (IOException e) {
				LOG.info("({}) flushFileBuffers(): IO Error while flushing to {}.", dokanyFileInfo.Context, path.toString(), e);
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
		if (isSkipFile(fileName)) {
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}
		Path path = getRootedPath(fileName);
		LOG.debug("({}) getFileInformation() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("getFileInformation(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try {
				FullFileInfo data = getFileInformation(path, dokanyFileInfo);
				data.copyTo(handleFileInfo);
				LOG.trace("({}) File Information successful read from {}.", dokanyFileInfo.Context, path.toString());
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (NoSuchFileException e) {
				LOG.debug("({}) Resource {} not found.", dokanyFileInfo.Context, path.toString());
				return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.info("({}) getFileInformation(): IO error occurred reading meta data from {}.", dokanyFileInfo.Context, path.toString(), e);
				LOG.debug("getFileInformation(): ", e);
				return Win32ErrorCode.ERROR_READ_FAULT.getMask();
			}
		}
	}

	private FullFileInfo getFileInformation(Path p, DokanyFileInfo dokanyFileInfo) throws IOException {
		DosFileAttributes attr = Files.readAttributes(p, DosFileAttributes.class);
		long index = 0;
		if (attr.fileKey() != null) {
			index = (long) attr.fileKey();
		}
		Path filename = p.getFileName();
		FullFileInfo data = new FullFileInfo(filename != null ? filename.toString() : "", //case distinction necessary, because the root has no name!
				index,
				FileUtil.dosAttributesToEnumIntegerSet(attr),
				0, //currently just a stub
				DokanyUtils.getTime(attr.creationTime().toMillis()),
				DokanyUtils.getTime(attr.lastAccessTime().toMillis()),
				DokanyUtils.getTime(attr.lastModifiedTime().toMillis()));
		try {
			data.setSize(attr.size());
		} catch (IllegalArgumentException e) {
			LOG.warn("({}) getFileInformation(): Wrong ciphertext file size of {} . Displayed cleartext file size is set to zero.", dokanyFileInfo.Context, p.toString());
			data.setSize(0);
		}
		return data;
	}

	@Override
	public int findFiles(WString rawPath, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}
		LOG.debug("({}) findFiles() is called for {}.", dokanyFileInfo.Context, getRootedPath(rawPath).toString());
		return findFilesWithPattern(rawPath, new WString("*"), rawFillFindData, dokanyFileInfo);
	}

	@Override
	public int findFilesWithPattern(WString fileName, WString searchPattern, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(fileName)) {
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}
		Path path = getRootedPath(fileName);
		LOG.debug("({}) findFilesWithPattern() is called for {} with search pattern {}.", dokanyFileInfo.Context, path.toString(), searchPattern.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("findFilesWithPattern(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try (Stream<Path> stream = Files.list(path)) {
				Stream<Path> filteredStream;
				if (searchPattern == null || searchPattern.toString().equals("*")) {
					// we want to list all files
					filteredStream = stream;
				} else {
					// we want to filter by glob
					PathMatcher matcher = path.getFileSystem().getPathMatcher("glob:" + searchPattern.toString());
					filteredStream = stream.map(Path::getFileName).filter(matcher::matches);
				}
				filteredStream.map(p -> {
					try {
						return getFileInformation(path.resolve(p), dokanyFileInfo).toWin32FindData();
					} catch (IOException e) {
						LOG.warn("({}) findFilesWithPatter(): IO error accessing {}. Will be ignored in file listing.", dokanyFileInfo.Context, p.toString());
						return null;
					}
				}).forEach(file -> {
					try {
						if (file != null) {
							LOG.trace("({}) findFilesWithPattern(): found file {}", dokanyFileInfo.Context, file.getFileName());
							rawFillFindData.fillWin32FindData(file, dokanyFileInfo);
						}
					} catch (Error e) {
						//TODO: invalid memory access can happen, which is an Java.Lang.Error
						LOG.error("({}) Error filling Win32FindData with file {}. Occurred error is {}", dokanyFileInfo.Context, file.getFileName());
						LOG.error("(" + dokanyFileInfo.Context + ") findFilesWithPattern(): Stacktrace:", e);
					}
				});
				LOG.trace("({}) Successful searched content in {}.", dokanyFileInfo.Context, path.toString());
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (IOException e) {
				LOG.error("({}) findFilesWithPattern(): Unable to list content of directory {}. Error is {}", dokanyFileInfo.Context, path.toString(), e);
				return Win32ErrorCode.ERROR_READ_FAULT.getMask();
			}
		}
	}

	@Override
	public int setFileAttributes(WString rawPath, int rawAttributes, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}

		Path path = getRootedPath(rawPath);
		LOG.debug("({}) setFileAttributes() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("setFileAttribute(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			return setFileAttributes(path, rawAttributes);
		}
	}

	private int setFileAttributes(Path path, int rawAttributes) {
		if (Files.notExists(path)) {
			return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		} else {
			DosFileAttributeView attrView = Files.getFileAttributeView(path, DosFileAttributeView.class);
			try {
				for (FileAttribute attr : FileAttribute.fromInt(rawAttributes)) {
					FileUtil.setAttribute(attrView, attr);
				}
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (IOException e) {
				return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
		}
	}

	@Override
	public int setFileTime(WString rawPath, WinBase.FILETIME rawCreationTime, WinBase.FILETIME rawLastAccessTime, WinBase.FILETIME rawLastWriteTime, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) setFileTime() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("setFileTime(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try {
				FileTime lastModifiedTime = FileTime.fromMillis(rawLastWriteTime.toDate().getTime());
				FileTime lastAccessTime = FileTime.fromMillis(rawLastWriteTime.toDate().getTime());
				FileTime createdTime = FileTime.fromMillis(rawLastWriteTime.toDate().getTime());
				Files.getFileAttributeView(path, BasicFileAttributeView.class).setTimes(lastModifiedTime, lastAccessTime, createdTime);
				LOG.trace("({}) Successful updated Filetime for {}.", dokanyFileInfo.Context, path.toString());
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (NoSuchFileException e) {
				LOG.trace("({}) File {} not found.", dokanyFileInfo.Context, path.toString());
				return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.info("({}) setFileTime(): IO error occurred accessing {}.", dokanyFileInfo.Context, path.toString(), e);
				LOG.debug("setFileTime(): ", e);
				return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
		}
	}

	@Override
	public int deleteFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) deleteFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("deleteFile(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.warn("({}) {} is a directory. Unable to delete via deleteFile()", dokanyFileInfo.Context, path.toString());
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} else {
			//TODO: race condition with handle == null possible?
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (Files.exists(path)) {
				//TODO: what is the best condition for the deletion? and is this case analysis correct?
				if (((OpenFile) handle).canBeDeleted()) {
					LOG.trace("({}) Deletion of {} possible.", dokanyFileInfo.Context, path.toString());
					return Win32ErrorCode.ERROR_SUCCESS.getMask();
				} else {
					LOG.trace("({}) Deletion of {} not possible.", dokanyFileInfo.Context, path.toString());
					return Win32ErrorCode.ERROR_BUSY.getMask();
				}
			} else {
				LOG.trace("({}) {} not found.", dokanyFileInfo.Context, path.toString());
				return Win32ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			}
		}
	}

	@Override
	public int deleteDirectory(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) deleteDirectory() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("deleteDirectory(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (!dokanyFileInfo.isDirectory()) {
			LOG.warn("({}) {} is a file. Unable to delete via deleteDirectory()", dokanyFileInfo.Context, path.toString());
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} else {
			//TODO: check for directory existence
			//TODO: race condition with handle == null possible?
			try (DirectoryStream emptyCheck = Files.newDirectoryStream(path)) {
				if (emptyCheck.iterator().hasNext()) {
					LOG.trace("({}) Deletion of {} not possible.", dokanyFileInfo.Context, path.toString());
					return Win32ErrorCode.ERROR_DIR_NOT_EMPTY.getMask();
				} else {
					LOG.trace("({}) Deletion of {} possible.", dokanyFileInfo.Context, path.toString());
					return Win32ErrorCode.ERROR_SUCCESS.getMask();
				}
			} catch (IOException e) {
				LOG.info("({}) deleteDirectory(): IO error occurred reading {}.", dokanyFileInfo.Context, path.toString());
				LOG.debug("deleteDirectory(): ", e);
				return Win32ErrorCode.ERROR_CURRENT_DIRECTORY.getMask();
			}
		}
	}

	@Override
	public int moveFile(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		Path newPath = getRootedPath(rawNewFileName);
		LOG.debug("({}) moveFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("moveFile(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else {
			try {
				if (rawReplaceIfExisting) {
					Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
				} else {
					Files.move(path, newPath);
				}
				LOG.trace("({}) Successful moved resource {} to {}.", dokanyFileInfo.Context, path.toString(), newPath);
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (FileAlreadyExistsException e) {
				LOG.trace("({}) Ressource {} already exists at {}.", dokanyFileInfo.Context, path.toString(), newPath);
				return Win32ErrorCode.ERROR_FILE_EXISTS.getMask();
			} catch (DirectoryNotEmptyException e) {
				LOG.trace("({}) Target directoy {} is not emtpy.", dokanyFileInfo.Context, path.toString());
				return Win32ErrorCode.ERROR_DIR_NOT_EMPTY.getMask();
			} catch (IOException e) {
				LOG.info("({}) moveFile(): IO error occured while moving ressource {}.", dokanyFileInfo.Context, path.toString());
				LOG.debug("moveFile(): ", e);
				return Win32ErrorCode.ERROR_GEN_FAILURE.getMask();
			}
		}
	}

	@Override
	public int setEndOfFile(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) setEndOfFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("setEndOfFile(): Invalid handle to {}.", path.toString());
			return Win32ErrorCode.ERROR_INVALID_HANDLE.getMask();
		} else if (dokanyFileInfo.isDirectory()) {
			LOG.warn("({}) setEndOfFile(): {} is a directory. Unable to truncate.", dokanyFileInfo.Context, path.toString());
			return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
		} else {
			try {
				OpenHandle handle = fac.get(dokanyFileInfo.Context);
				((OpenFile) handle).truncate(rawByteOffset);
				LOG.trace("({}) Successful truncated {}.", dokanyFileInfo.Context, path.toString());
				return Win32ErrorCode.ERROR_SUCCESS.getMask();
			} catch (IOException e) {
				LOG.info("({}) setEndOfFile(): IO error while truncating {}.", dokanyFileInfo.Context, path.toString());
				LOG.debug("setEndOfFile(): ", e);
				return Win32ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
		}
	}

	@Override
	public int setAllocationSize(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) setAllocationSize() is called for {}.", dokanyFileInfo.Context, path.toString());
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
		LOG.debug("getFreeDiskSpace() is called.");
		try {
			totalNumberOfBytes.setValue(fileStore.getTotalSpace());
			freeBytesAvailable.setValue(fileStore.getUsableSpace());
			totalNumberOfFreeBytes.setValue(fileStore.getUnallocatedSpace());
			return Win32ErrorCode.ERROR_SUCCESS.getMask();
		} catch (IOException e) {
			LOG.info("({}) getFreeDiskSpace(): Unable to detect disk space status.", dokanyFileInfo.Context, e);
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
		LOG.debug("mounted() is called.");
		didMount.complete(null);
		return 0;
	}

	@Override
	public int unmounted(DokanyFileInfo dokanyFileInfo) {
		LOG.debug("unmounted() is called.");
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
		return root.resolve(relativeUnixPath);
	}

	private boolean isSkipFile(WString filepath) {
		String pathLowerCase = filepath.toString().toLowerCase();
		if (pathLowerCase.endsWith("desktop.ini")
				|| pathLowerCase.endsWith("autorun.inf")) {
			LOG.trace("Skipping file: " + getRootedPath(filepath));
			return true;
		} else {
			return false;
		}
	}
}
