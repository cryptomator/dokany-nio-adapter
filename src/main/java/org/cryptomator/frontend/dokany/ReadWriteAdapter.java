package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyOperations;
import com.dokany.java.DokanyUtils;
import com.dokany.java.constants.CreateOptions;
import com.dokany.java.constants.CreationDisposition;
import com.dokany.java.constants.ErrorCode;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.constants.NtStatus;
import com.dokany.java.structure.ByHandleFileInfo;
import com.dokany.java.structure.DokanyFileInfo;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.FullFileInfo;
import com.dokany.java.structure.VolumeInformation;
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
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
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
	private final UserPrincipal user;

	public ReadWriteAdapter(Path root, VolumeInformation volumeInformation, CompletableFuture<?> didMount) {
		this.root = root;
		this.volumeInformation = volumeInformation;
		this.didMount = didMount;
		this.fac = new OpenHandleFactory();
		try {
			this.fileStore = Files.getFileStore(root);
			this.user = ReadWriteAdapter.getUserPrincipal(root.getFileSystem());
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static UserPrincipal getUserPrincipal(FileSystem fileSystem) throws IOException {
		try {
			return fileSystem.getUserPrincipalLookupService().lookupPrincipalByName(System.getProperty("user.name"));
		} catch (UserPrincipalNotFoundException | UnsupportedOperationException e) {
			LOG.info("Unable to get UserPrincipal.");
			return null;
		}
	}

	@Override
	public long zwCreateFile(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
		//TODO: this should be removed in later versions
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
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
				return 0xC00000BAL; //TODO: rewrite this in a understandable err code
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
	 * TODO: should the alreadyExists check be atomical with respect to the function call ?
	 * TODO: currently we set the isDirectory flag, but never check it somewhere -> look into the mirror example how they handle it
	 *
	 * @return
	 */
	private long createDirectory(Path path, CreationDisposition creationDisposition, int rawFileAttributes, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("Try to open {} as Directory.");
		final int mask = creationDisposition.getMask();
		//createDirectory request
		if (mask == CreationDisposition.CREATE_NEW.getMask() || mask == CreationDisposition.OPEN_ALWAYS.getMask()) {
			try {
				Files.createDirectory(path);
				LOG.trace("Directory {} successful created ", path.toString());
			} catch (FileAlreadyExistsException e) {
				if (mask == CreationDisposition.CREATE_NEW.getMask()) {
					//we got create_new flag -> there should be nuthing, but there is somthin!
					LOG.trace("Directory {} already exists.", path.toString());
					return ErrorCode.ERROR_ALREADY_EXISTS.getMask();
				}
			} catch (IOException e) {
				//we dont know what the hell happened
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}

		//some otha flags were used
		//here we check if we try to open a file as a directory
		if (Files.isRegularFile(path)) {
			//sh*t
			LOG.trace("Attempt to open file {} as a directory.", path.toString());
			return 0xC00000BAL;
		} else {
			// we open the directory in some kinda way
			setFileAttributes(path, rawFileAttributes);
			dokanyFileInfo.Context = fac.openDir(path);
			LOG.trace("({}) {} opened successful with handle {}.", dokanyFileInfo.Context, path.toString(), dokanyFileInfo.Context);

			//it worked, hurray! but we must give a signal, that we opened it and not created it!
			if (mask == CreationDisposition.OPEN_ALWAYS.getMask()) {
				return ErrorCode.OBJECT_NAME_COLLISION.getMask();
			} else {
				return ErrorCode.SUCCESS.getMask();
			}
		}
	}

	private long createFile(Path path, CreationDisposition creationDisposition, Set<OpenOption> openOptions, int rawFileAttributes, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("Try to open {} as File.");
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
			return NtStatus.ACCESS_DENIED.getMask();
		}
		//read-only?
		else if ((attr != null && attr.isReadOnly() || ((rawFileAttributes & FileAttribute.READONLY.getMask()) != 0))
				&& dokanyFileInfo.DeleteOnClose != 0
				) {
			//cannot overwrite file
			LOG.trace("{} is readonly. Unable to overwrite.", path.toString());
			return NtStatus.CANNOT_DELETE.getMask();
		} else {
			try {
				dokanyFileInfo.Context = fac.openFile(path, openOptions);
				LOG.trace("({}) {} opened successful with handle {}.", dokanyFileInfo.Context, path.toString(), dokanyFileInfo.Context);
				setFileAttributes(path, rawFileAttributes);
			} catch (FileAlreadyExistsException e) {
				LOG.trace("Unable to open {}.", path.toString());
				return NtStatus.OBJECT_NAME_EXISTS.getMask();
			} catch (NoSuchFileException e) {
				LOG.trace("{} not found.", path.toString());
				return NtStatus.OBJECT_NAME_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.info("zwCreateFile(): IO error occurred during creation of {}.", path.toString());
				return NtStatus.UNSUCCESSFUL.getMask();
			}
			if (mask == CreationDisposition.OPEN_ALWAYS.getMask() || mask == CreationDisposition.CREATE_ALWAYS.getMask()) {
				return NtStatus.OBJECT_NAME_COLLISION.getMask();
			}
		}
		LOG.trace("({}) {} opened successful with handle {}.", dokanyFileInfo.Context, path.toString(), dokanyFileInfo.Context);
		return ErrorCode.SUCCESS.getMask();
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
						//TODO: more finegrained exception analysis!
					} catch (DirectoryNotEmptyException e) {
						LOG.trace("({}) Directory {} not empty.", dokanyFileInfo.Context, path.toString());
					} catch (IOException e) {
						LOG.info("({}) cleanup(): IO error during deletion of {} ", dokanyFileInfo.Context, path.toString(), e);
					}
				}
			} catch (IOException e) {
				LOG.warn("({}) cleanup(): Unable to close handle to {}", dokanyFileInfo.Context, path.toString(), e);
			}
		}
	}

	@Override
	public void closeFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return;
		}
		LOG.debug("({}) closeFile() is called for {}.", dokanyFileInfo.Context, getRootedPath(rawPath).toString());
		dokanyFileInfo.Context = 0;
	}

	@Override
	public long readFile(WString rawPath, Pointer rawBuffer, int rawBufferLength, IntByReference rawReadLength, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) readFile() is called for {}.", path.toString(), dokanyFileInfo.Context);
		if (dokanyFileInfo.Context == 0) {
			LOG.info("readFile(): Invalid handle to {} ", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			//TODO: what if the handle is null? how can this happen and what to do?
			//TODO: probably a race condition
			try {
				OpenHandle handle = fac.get(dokanyFileInfo.Context);
				if (!handle.isDirectory()) {
					try {
						rawReadLength.setValue(((OpenFile) handle).read(rawBuffer, rawBufferLength, rawOffset));
					} catch (IOException e) {
						LOG.info("({}) readFile(): Error while reading file {}.", dokanyFileInfo.Context, path.toString(), e);
						return ErrorCode.ERROR_READ_FAULT.getMask();
					}
					LOG.trace("({}) Data successful read from {}.", dokanyFileInfo.Context, path.toString());
					return ErrorCode.SUCCESS.getMask();
				} else {
					LOG.trace("({}) {} is a directory. Unable to read Data from it.", dokanyFileInfo.Context, path.toString());
					return NtStatus.ACCESS_DENIED.getMask();
				}
			} catch (NullPointerException e) {
				LOG.warn("({}) readFile(): Unable to find handle for {}.", dokanyFileInfo.Context, getRootedPath(rawPath).toString());
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	@Override
	public long writeFile(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) writeFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("writeFile(): Invalid handle to {}", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (!handle.isDirectory()) {
				try {
					rawNumberOfBytesWritten.setValue(((OpenFile) handle).write(rawBuffer, rawNumberOfBytesToWrite, rawOffset));
				} catch (IOException e) {
					LOG.info("({}) writeFile(): IO Error while writing to {} ", dokanyFileInfo.Context, path.toString(), e);
					return ErrorCode.ERROR_WRITE_FAULT.getMask();
				}
				LOG.trace("({}) Data successful written to {}.", dokanyFileInfo.Context, path.toString());
				return ErrorCode.SUCCESS.getMask();
			} else {
				LOG.trace("({}) {} is a directory. Unable to write Data to it.", dokanyFileInfo.Context, path.toString());
				return NtStatus.ACCESS_DENIED.getMask();
			}
		}
	}

	@Override
	public long flushFileBuffers(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) flushFileBuffers() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("flushFileBuffers(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (!handle.isDirectory()) {
				try {
					((OpenFile) handle).flush();
				} catch (IOException e) {
					LOG.info("({}) flushFileBuffers(): IO Error while flushing to {}.", dokanyFileInfo.Context, path.toString(), e);
					return ErrorCode.ERROR_WRITE_FAULT.getMask();
				}
				LOG.trace("Flushed successful to {} with handle {}.", path.toString(), dokanyFileInfo.Context);
				return ErrorCode.SUCCESS.getMask();
			} else {
				LOG.trace("({}) {} is a directory. Unable to write Data to it.", dokanyFileInfo.Context, path.toString());
				return NtStatus.ACCESS_DENIED.getMask();
			}
		}
	}

	/**
	 * TODO: do we have to check if the handle is valid?
	 *
	 * @param fileName
	 * @param handleFileInfo
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return
	 */
	@Override
	public long getFileInformation(WString fileName, ByHandleFileInfo handleFileInfo, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(fileName)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(fileName);
		LOG.debug("({}) getFileInformation() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("getFileInformation(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			try {
				FullFileInfo data = getFileInfo(path);
				data.copyTo(handleFileInfo);
				LOG.trace("({}) File Information successful read from {}.", dokanyFileInfo.Context, path.toString());
				return ErrorCode.SUCCESS.getMask();
			} catch (NoSuchFileException e) {
				LOG.debug("({}) Resource {} not found.", dokanyFileInfo.Context, path.toString());
				return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.info("({}) getFileInformation(): IO error occured in reading meta data from {}.", dokanyFileInfo.Context, path.toString(), e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	private FullFileInfo getFileInfo(Path p) throws IOException {
		DosFileAttributes attr = Files.readAttributes(p, DosFileAttributes.class);
		long index = 0;
		if (attr.fileKey() != null) {
			index = (long) attr.fileKey();
		}
		Path filename = p.getFileName();
		FullFileInfo data = new FullFileInfo(filename != null ? filename.toString() : "",
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
	public long findFiles(WString rawPath, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		LOG.debug("({}) findFiles() is called for {}.", dokanyFileInfo.Context, getRootedPath(rawPath).toString());
		return findFilesWithPattern(rawPath, new WString("*"), rawFillFindData, dokanyFileInfo);
	}

	@Override
	public long findFilesWithPattern(WString fileName, WString searchPattern, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(fileName)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(fileName);
		LOG.debug("({}) findFilesWithPattern() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("findFilesWithPattern(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			Set<WinBase.WIN32_FIND_DATA> findings = Sets.newHashSet();
			try (Stream<Path> stream = Files.list(path)) {
				Stream<Path> streamByPattern;
				if (searchPattern == null || searchPattern.toString().equals("*")) {
					//we want to list all files
					streamByPattern = stream;
				} else {
					streamByPattern = stream.filter(path1 -> path1.toString().contains(searchPattern));
				}
				findings = streamByPattern.map(path2 -> {
					try {
						return getFileInfo(path2).toWin32FindData();
					} catch (NoSuchFileException e) {
						LOG.debug("({}) FindFiles(): {} not found.", dokanyFileInfo.Context, path2.toString());
						return new WinBase.WIN32_FIND_DATA();
					} catch (IOException e) {
						LOG.debug("({}) FindFiles(): IO error accessing {}.", dokanyFileInfo.Context, path2.toString(), e);
						return new WinBase.WIN32_FIND_DATA();
					}
				}).collect(Collectors.toSet());
			} catch (IOException e) {
				LOG.debug("({}) Unable to list content of directory {}.", dokanyFileInfo.Context, path.toString(), e);
			}
			LOG.trace("({})Found {} paths", dokanyFileInfo.Context, findings.size());
			try {
				findings.forEach(file -> {
					LOG.trace("file in find: {}", file.getFileName());
					//TODO: invalid memory access
					rawFillFindData.fillWin32FindData(file, dokanyFileInfo);
				});
			} catch (Error e) {
				//TODO: error??
				LOG.warn("Error filling Win32FindData", e);
			}
			LOG.trace("({}) Successful searched content in {}.", dokanyFileInfo.Context, path.toString());
			return ErrorCode.SUCCESS.getMask();
		}
	}

	@Override
	public long setFileAttributes(WString rawPath, int rawAttributes, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}

		Path path = getRootedPath(rawPath);
		LOG.debug("({}) setFileAttributes() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("setFileAttribute(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			return setFileAttributes(path, rawAttributes);
		}
	}

	private long setFileAttributes(Path path, int rawAttributes) {
		if (Files.notExists(path)) {
			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		} else {
			DosFileAttributeView attrView = Files.getFileAttributeView(path, DosFileAttributeView.class);
			try {
				for (FileAttribute attr : FileAttribute.fromInt(rawAttributes)) {
					FileUtil.setAttribute(attrView, attr);
				}
			} catch (IOException e) {
				return ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
			return ErrorCode.SUCCESS.getMask();
		}
	}

	@Override
	public long setFileTime(WString rawPath, WinBase.FILETIME rawCreationTime, WinBase.FILETIME rawLastAccessTime, WinBase.FILETIME rawLastWriteTime, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) setFileTime() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("setFileTime(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			try {
				Files.setAttribute(path, "basic:creationTime", FileTime.fromMillis(rawCreationTime.toDate().getTime()));
				Files.setAttribute(path, "basic:lastAccessTime", FileTime.fromMillis(rawLastAccessTime.toDate().getTime()));
				Files.setLastModifiedTime(path, FileTime.fromMillis(rawLastWriteTime.toDate().getTime()));
				LOG.trace("({}) Successful updated Filetime for {}.", dokanyFileInfo.Context, path.toString());
				return ErrorCode.SUCCESS.getMask();
			} catch (NoSuchFileException e) {
				LOG.trace("({}) File {} not found.", dokanyFileInfo.Context, path.toString());
				return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.info("({}) setFileTime(): IO error occurred accessing {}.", dokanyFileInfo.Context, path.toString(), e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	@Override
	public long deleteFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) deleteFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("deleteFile(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			//TODO: race condition with handle == null possible?
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (Files.exists(path)) {
				if (!handle.isDirectory()) {
					//TODO: what is the best condition for the deletion? and is this case analysis correct?
					if (((OpenFile) handle).canBeDeleted()) {
						LOG.trace("({}) Deletion of {} possible.", dokanyFileInfo.Context, path.toString());
						return NtStatus.SUCCESS.getMask();
					} else {
						LOG.trace("({}) Deletion of {} not possible.", dokanyFileInfo.Context, path.toString());
						return NtStatus.CANNOT_DELETE.getMask();
					}
				} else {
					LOG.warn("({}) {} is a directory. Unable to delete via deleteFile()", dokanyFileInfo.Context, path.toString());
					return NtStatus.ACCESS_DENIED.getMask();
				}
			} else {
				LOG.trace("({}) {} not found.", dokanyFileInfo.Context, path.toString());
				return NtStatus.OBJECT_NAME_NOT_FOUND.getMask();
			}
		}
	}

	@Override
	public long deleteDirectory(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) deleteDirectory() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("deleteDirectory(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			//TODO: check for directory existence
			//TODO: race condition with handle == null possible?
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (handle.isDirectory()) {
				try (DirectoryStream emptyCheck = Files.newDirectoryStream(path)) {
					if (!emptyCheck.iterator().hasNext()) {
						LOG.trace("({}) Deletion of {} possible.", dokanyFileInfo.Context, path.toString());
						return ErrorCode.SUCCESS.getMask();
					} else {
						LOG.trace("({}) Deletion of {} not possible.", dokanyFileInfo.Context, path.toString());
						return NtStatus.DIRECTORY_NOT_EMPTY.getMask();
					}

				} catch (IOException e) {
					LOG.info("({}) deleteDirectory(): IO error occurred reading {}.", dokanyFileInfo.Context, path.toString());
					return NtStatus.UNSUCCESSFUL.getMask();
				}
			} else {
				LOG.warn("({}) {} is a file. Unable to delete via deleteDirectory()", dokanyFileInfo.Context, path.toString());
				return NtStatus.ACCESS_DENIED.getMask();
			}
		}
	}

	@Override
	public long moveFile(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		Path newPath = getRootedPath(rawNewFileName);
		LOG.debug("({}) moveFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("moveFile(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			try {
				if (rawReplaceIfExisting) {
					Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
				} else {
					Files.move(path, newPath);
				}
				LOG.trace("({}) Successful moved resource {} to {}.", dokanyFileInfo.Context, path.toString(), newPath);
				return ErrorCode.SUCCESS.getMask();
			} catch (FileAlreadyExistsException e) {
				LOG.trace("({}) Ressource {} already exists at {}.", dokanyFileInfo.Context, path.toString(), newPath);
				return ErrorCode.ERROR_FILE_EXISTS.getMask();
			} catch (DirectoryNotEmptyException e) {
				LOG.trace("({}) Directoy {} is not emtpy.", dokanyFileInfo.Context, path.toString());
				return NtStatus.DIRECTORY_NOT_EMPTY.getMask();
			} catch (IOException e) {
				LOG.info("({}) moveFile(): IO error occured while moving ressource {}.", dokanyFileInfo.Context, path.toString());
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	@Override
	public long setEndOfFile(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) setEndOfFile() is called for {}.", dokanyFileInfo.Context, path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.info("setEndOfFile(): Invalid handle to {}.", path.toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (!handle.isDirectory()) {
				try {
					((OpenFile) handle).truncate(rawByteOffset);
					LOG.trace("({}) Successful truncated {}.", dokanyFileInfo.Context, path.toString());
					return NtStatus.SUCCESS.getMask();
				} catch (IOException e) {
					LOG.info("({}) setEndOfFile(): IO error while truncating {}.", dokanyFileInfo.Context, path.toString());
					NtStatus.UNSUCCESSFUL.getMask();
				}
			} else {
				LOG.warn("({}) setEndOfFile(): {} is a directory. Unable to truncate.", dokanyFileInfo.Context, path.toString());
				return NtStatus.ACCESS_DENIED.getMask();
			}
			return 0;
		}
	}

	@Override
	public long setAllocationSize(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.debug("({}) setAllocationSize() is called for {}.", dokanyFileInfo.Context, path.toString());
		return setEndOfFile(rawPath, rawLength, dokanyFileInfo);
	}

	@Override
	public long lockFile(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long unlockFile(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long getDiskFreeSpace(LongByReference freeBytesAvailable, LongByReference totalNumberOfBytes, LongByReference totalNumberOfFreeBytes, DokanyFileInfo dokanyFileInfo) {
		LOG.debug("getFreeDiskSpace() is called.");
		if (fileStore != null) {
			try {
				totalNumberOfBytes.setValue(fileStore.getTotalSpace());
				freeBytesAvailable.setValue(fileStore.getUsableSpace());
				totalNumberOfFreeBytes.setValue(fileStore.getUnallocatedSpace());
				return ErrorCode.SUCCESS.getMask();
			} catch (IOException e) {
				LOG.info("({}) getFreeDiskSpace(): Unable to detect disk space status.", dokanyFileInfo.Context, e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		} else {
			LOG.trace("({}) Information about disk space not available.", dokanyFileInfo.Context);
			return NtStatus.UNSUCCESSFUL.getMask();
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
	public long getVolumeInformation(Pointer rawVolumeNameBuffer, int rawVolumeNameSize, IntByReference rawVolumeSerialNumber, IntByReference rawMaximumComponentLength, IntByReference rawFileSystemFlags, Pointer rawFileSystemNameBuffer, int rawFileSystemNameSize, DokanyFileInfo dokanyFileInfo) {
		try {
			rawVolumeNameBuffer.setWideString(0L, DokanyUtils.trimStrToSize(volumeInformation.getName(), rawVolumeNameSize));

			rawVolumeSerialNumber.setValue(volumeInformation.getSerialNumber());

			rawMaximumComponentLength.setValue(volumeInformation.getMaxComponentLength());

			rawFileSystemFlags.setValue(volumeInformation.getFileSystemFeatures().toInt());

			rawFileSystemNameBuffer.setWideString(0L, DokanyUtils.trimStrToSize(volumeInformation.getFileSystemName(), rawFileSystemNameSize));

			return ErrorCode.SUCCESS.getMask();
		} catch (final Throwable t) {
			return DokanyUtils.exceptionToErrorCode(t);
		}
	}

	@Override
	public long mounted(DokanyFileInfo dokanyFileInfo) {
		LOG.debug("mounted() is called.");
		didMount.complete(null);
		return 0;
	}

	@Override
	public long unmounted(DokanyFileInfo dokanyFileInfo) {
		LOG.debug("unmounted() is called.");
		return 0;
	}

	@Override
	public long getFileSecurity(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, IntByReference rawSecurityDescriptorLengthNeeded, DokanyFileInfo dokanyFileInfo) {
//		Path path = getRootedPath(rawPath);
//		//SecurityInformation securityInfo = DokanyUtils.enumFromInt(rawSecurityInformation, SecurityInformation.values());
//		//LOG.debug("getFileSecurity() is called for {} {}", path.toString(), securityInfo.name());
//		if (Files.exists(path)) {
//			byte[] securityDescriptor = FileUtil.getStandardSecurityDescriptor();
//			rawSecurityDescriptorLengthNeeded.setValue(securityDescriptor.length);
//			if (securityDescriptor.length <= rawSecurityDescriptorLength) {
//				rawSecurityDescriptor.write(0L, securityDescriptor, 0, securityDescriptor.length);
//				return ErrorCode.SUCCESS.getMask();
//			} else {
//				return NtStatus.BUFFER_OVERFLOW.getMask();
//			}
//		} else {
//			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
//		}
		return 0;
	}

	@Override
	public long setFileSecurity(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, DokanyFileInfo dokanyFileInfo) {
//		Path path = getRootedPath(rawPath);
//		LOG.trace("setFileSecurity() is called for " + path.toString());
//		if (Files.exists(path)) {
//			byte[] securityDescriptor = FileUtil.getStandardSecurityDescriptor();
//			if (securityDescriptor.length <= rawSecurityDescriptorLength) {
//				rawSecurityDescriptor.write(0L, securityDescriptor, 0, securityDescriptor.length);
//				return ErrorCode.SUCCESS.getMask();
//			} else {
//				return NtStatus.BUFFER_OVERFLOW.getMask();
//			}
//		} else {
//			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
//		}
		return 0;
	}

	@Override
	public void fillWin32FindData(WinBase.WIN32_FIND_DATA rawFillFindData, DokanyFileInfo dokanyFileInfo) {

	}

	@Override
	public long findStreams(WString rawPath, DokanyOperations.FillWin32FindStreamData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	protected Path getRootedPath(WString rawPath) {
		//TODO: edit raw path to be  relative
		return root.resolve(rawPath.toString().replace('\\', '/').replaceFirst("^/+", ""));
	}

	protected boolean isSkipFile(WString filepath) {
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
