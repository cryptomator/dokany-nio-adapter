package org.cryptomator.frontend.dokan;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyOperations;
import com.dokany.java.DokanyUtils;
import com.dokany.java.constants.*;
import com.dokany.java.structure.*;
import com.google.common.collect.Sets;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: Beware of DokanyUtils.enumSetFromInt()!!!
 */
public class ReadWriteAdapter implements DokanyFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(ReadWriteAdapter.class);

	protected final Path root;
	protected final OpenHandleFactory fac;
	protected final FileStore fileStore;
	protected final UserPrincipal user;


	private final VolumeInformation volumeInformation;
	private final FreeSpace freeSpace;

	public ReadWriteAdapter(Path root, VolumeInformation volumeInformation, FreeSpace freeSpace) {
		this.root = root;
		this.volumeInformation = volumeInformation;
		this.freeSpace = freeSpace;
		this.fac = new OpenHandleFactory();

		FileStore fileStore1;
		try {
			fileStore1 = Files.getFileStore(root);
		} catch (IOException e) {
			LOG.warn("Could not detect FileStore: ", e);
			fileStore1 = null;
		}
		this.fileStore = fileStore1;

		UserPrincipal user1;
		try {
			user1 = root.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName(System.getProperty("user.name"));
		} catch (IOException e) {
			LOG.warn(("Could not get UserPrincipal"));
			user1 = null;
		}
		user = user1;
	}


	@Override
	public long zwCreateFile(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
		//TODO: this should be removed in later versions
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		LOG.trace("zwCreateFile() is called for: " + path.toString());

		boolean fileExists = Files.exists(path);

		//is the file a directory and if yes, indicated as one?
		CreationDisposition creationDisposition = CreationDisposition.fromInt(rawCreateDisposition);
		EnumIntegerSet<CreateOptions> createOptions = DokanyUtils.enumSetFromInt(rawCreateOptions, CreateOptions.values());
		if (fileExists && Files.isDirectory(path)) {
			if ((rawCreateOptions & CreateOptions.FILE_NON_DIRECTORY_FILE.getMask()) == 0) {
				dokanyFileInfo.IsDirectory = 0x01;
				//TODO: set the share access like in the dokany mirror example
			} else {
				LOG.info("Cannot open a directory as a file.");
				return 0xC00000BAL;
			}
		}

		if (dokanyFileInfo.isDirectory()) {
			return createDirectory(path, creationDisposition, rawFileAttributes, dokanyFileInfo);
		} else {
			Set<OpenOption> openOptions = Sets.newHashSet();
			openOptions.add(StandardOpenOption.READ);
			//TODO: mantle this statement with an if-statement which checks for write protection!
			openOptions.add(StandardOpenOption.WRITE);
			LOG.debug("Create Disposition flag is " + creationDisposition.name());
			if (fileExists) {
				switch (creationDisposition) {
					case CREATE_NEW:
						openOptions.add(StandardOpenOption.CREATE);
						break;
					case CREATE_ALWAYS:
						openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
						break;
					case OPEN_EXISTING:
						break;
					case OPEN_ALWAYS:
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
						break;
					case OPEN_ALWAYS:
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
		final int mask = creationDisposition.getMask();
		//createDirectory request
		if (mask == CreationDisposition.CREATE_NEW.getMask() || mask == CreationDisposition.OPEN_ALWAYS.getMask()) {
			try {
				//TODO: change the standardACLPermissions
				Files.createDirectory(path, FileUtil.getStandardAclPermissions(user));
			} catch (FileAlreadyExistsException e) {
				if (mask == CreationDisposition.CREATE_NEW.getMask()) {
					//we got create_new flag -> there should be nuthing, but there is somthin!
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
			return 0xC00000BAL;
		} else {
			// we open the directory in some kinda way
			setFileAttributes(path, rawFileAttributes);
			dokanyFileInfo.Context = fac.openDir(path);

			//it worked, hurray! but we must give a signal, that we opened it and not created it!
			if (mask == CreationDisposition.OPEN_ALWAYS.getMask()) {
				return ErrorCode.OBJECT_NAME_COLLISION.getMask();
			} else {
				return ErrorCode.SUCCESS.getMask();
			}
		}
	}

	private long createFile(Path path, CreationDisposition creationDisposition, Set<OpenOption> openOptions, int rawFileAttributes, DokanyFileInfo dokanyFileInfo) {
		final int mask = creationDisposition.getMask();
		DosFileAttributes attr = null;
		try {
			attr = Files.getFileAttributeView(path, DosFileAttributeView.class).readAttributes();
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
			return NtStatus.ACCESS_DENIED.getMask();
		}
		//read-only?
		else if ((attr != null && attr.isReadOnly() || ((rawFileAttributes & FileAttribute.READONLY.getMask()) != 0))
				&& dokanyFileInfo.DeleteOnClose != 0
				) {
			//cannot overwrite file
			return NtStatus.CANNOT_DELETE.getMask();
		} else {
			try {
				//TODO: what permissions of ACl view?
				dokanyFileInfo.Context = fac.openFile(path, openOptions, FileUtil.getStandardAclPermissions(user));
				setFileAttributes(path, rawFileAttributes);
			} catch (FileAlreadyExistsException e) {
				LOG.info("Unable to open File.");
				return NtStatus.OBJECT_NAME_EXISTS.getMask();
			} catch (NoSuchFileException e) {
				LOG.info("File not found.");
				return NtStatus.OBJECT_NAME_NOT_FOUND.getMask();
			} catch (IOException e) {
				LOG.warn("IO error occurred.");
				return NtStatus.UNSUCCESSFUL.getMask();
			}
			if (mask == CreationDisposition.OPEN_ALWAYS.getMask() || mask == CreationDisposition.CREATE_ALWAYS.getMask()) {
				return NtStatus.OBJECT_NAME_COLLISION.getMask();
			}
		}
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
		LOG.trace("cleanup() is called for " + getRootedPath(rawPath).toString());
		try {
			if (dokanyFileInfo.Context != 0) {
				fac.close(dokanyFileInfo.Context);
			}
			if (dokanyFileInfo.deleteOnClose()) {
				try {
					Files.delete(getRootedPath(rawPath));
				} catch (IOException e) {
					LOG.warn("Unable to delete File: ", e);
				}
			}
		} catch (IOException e) {
			LOG.warn("Unable to close FileHandle: ", e);
		}
	}

	@Override
	public void closeFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return;
		}
		LOG.trace("closeFile() is called for " + getRootedPath(rawPath).toString());
		dokanyFileInfo.Context = 0;
	}

	@Override
	public long readFile(WString rawPath, Pointer rawBuffer, int rawBufferLength, IntByReference rawReadLength, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		LOG.trace("readFile() is called for " + getRootedPath(rawPath).toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Invalid handle to object " + getRootedPath(rawPath).toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			//TODO: what if the handle is null? how can this happen and what to do?
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (handle.isRegularFile()) {
				try {
					rawReadLength.setValue(((OpenFile) handle).read(rawBuffer, rawBufferLength, rawOffset));
				} catch (IOException e) {
					LOG.error("Error while reading file: ", e);
					return ErrorCode.ERROR_READ_FAULT.getMask();
				}
				return ErrorCode.SUCCESS.getMask();
			} else {
				LOG.error("Attempt of reading from a directory.");
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	@Override
	public long writeFile(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("writeFile() is called for " + getRootedPath(rawPath).toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Invalid handle to object " + getRootedPath(rawPath).toString());
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (handle.isRegularFile()) {
				try {
					rawNumberOfBytesWritten.setValue(((OpenFile) handle).write(rawBuffer, rawNumberOfBytesToWrite, rawOffset));
				} catch (IOException e) {
					LOG.error("Error while reading file: ", e);
					return ErrorCode.ERROR_WRITE_FAULT.getMask();
				}
				return ErrorCode.SUCCESS.getMask();
			} else {
				LOG.error("Attempt of writing to a directory.");
				return NtStatus.ACCESS_DENIED.getMask();
			}
		}
	}

	@Override
	public long flushFileBuffers(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("flushFileBuffers() is called for " + getRootedPath(rawPath).toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to flush to object" + getRootedPath(rawPath).toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (handle.isRegularFile()) {
				try {
					((OpenFile) handle).flush();
				} catch (IOException e) {
					LOG.error("Error while flushing to file: ", e);
					return ErrorCode.ERROR_WRITE_FAULT.getMask();
				}
				return ErrorCode.SUCCESS.getMask();
			} else {
				LOG.error("Attempt of flushing to a directory.");
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
		LOG.trace("getFileInformation() is called for " + path.toString());
		try {
			FullFileInfo data = getFileInfo(path);
			data.copyTo(handleFileInfo);
			return ErrorCode.SUCCESS.getMask();
		} catch (NoSuchFileException e) {
			LOG.debug("File " + path.toString() + " not found.");
			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		} catch (IOException e) {
			LOG.error("IO error occured: ", e);
			return NtStatus.UNSUCCESSFUL.getMask();
		}
	}

	private FullFileInfo getFileInfo(Path p) throws IOException {
		try {
			DosFileAttributes attr = Files.getFileAttributeView(p, DosFileAttributeView.class).readAttributes();
			long index = 0;
			if (attr.fileKey() != null) {
				index = (long) attr.fileKey();
			}
			FullFileInfo data = new FullFileInfo(p.getFileName().toString(),
					index,
					FileUtil.dosAttributesToEnumIntegerSet(attr),
					0, //currently just a stub
					FileUtil.javaFileTimeToWindowsFileTime(attr.creationTime()),
					FileUtil.javaFileTimeToWindowsFileTime(attr.lastAccessTime()),
					FileUtil.javaFileTimeToWindowsFileTime(attr.lastModifiedTime()));
			data.setSize(attr.size());
			return data;
		} catch (NoSuchFileException e) {
			LOG.debug("File " + p.toString() + " not found.");
			throw e;
		} catch (IOException e) {
			LOG.error("IO error occured: ", e);
			throw e;
		}
	}

	@Override
	public long findFiles(WString rawPath, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		LOG.trace("findFiles() is called for " + getRootedPath(rawPath).toString());
		return findFilesWithPattern(rawPath, new WString("*"), rawFillFindData, dokanyFileInfo);
	}

	@Override
	public long findFilesWithPattern(WString fileName, WString searchPattern, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(fileName)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(fileName);
		LOG.trace("findFilesWithPattern() is called for " + path.toString());
		//TODO: do we have to check for a valid handle?
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
				} catch (IOException e) {
					return new WinBase.WIN32_FIND_DATA();
				}
			}).collect(Collectors.toSet());
		} catch (IOException e) {
			LOG.warn("Unable to list directory content: ", e);
		}
		LOG.debug("Found {} paths", findings.size());
		try {
			findings.forEach(file -> {
				LOG.trace("file in find: {}", file.getFileName());
				rawFillFindData.fillWin32FindData(file, dokanyFileInfo);
			});
		} catch (Error e) {
			LOG.warn("Error filling Win32FindData", e);
		}
		return ErrorCode.SUCCESS.getMask();
	}

	@Override
	public long setFileAttributes(WString rawPath, int rawAttributes, DokanyFileInfo dokanyFileInfo) {
		//TODO: check for valid handle?
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		LOG.trace("setFileAttributes() is called for " + path.toString());
		return setFileAttributes(path, rawAttributes);
	}

	private long setFileAttributes(Path path, int rawAttributes) {
		if (Files.exists(path)) {
			for (FileAttribute attr : FileAttribute.fromInt(rawAttributes)) {
				if (FileUtil.isFileAttributeSupported(attr)) {
					try {
						FileUtil.setAttribute(path, attr);
					} catch (IOException e) {
						return ErrorCode.ERROR_WRITE_FAULT.getMask();
					}
				} else {
					LOG.debug("Windows file attribute {} is currently not supported and thus will be ignored", attr.name());
				}
			}
		} else {
			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		}
		return ErrorCode.SUCCESS.getMask();
	}

	@Override
	public long setFileTime(WString rawPath, WinBase.FILETIME rawCreationTime, WinBase.FILETIME rawLastAccessTime, WinBase.FILETIME rawLastWriteTime, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		//TODO: valid handle?
		Path path = getRootedPath(rawPath);
		LOG.trace("setFileAttributes() is called for " + path.toString());
		try {
			Files.setAttribute(path, "basic:creationTime", FileTime.fromMillis(rawCreationTime.toDate().getTime()));
			Files.setAttribute(path, "basic:lastAccessTime", FileTime.fromMillis(rawLastAccessTime.toDate().getTime()));
			Files.setLastModifiedTime(path, FileTime.fromMillis(rawLastWriteTime.toDate().getTime()));
			return ErrorCode.SUCCESS.getMask();
		} catch (NoSuchFileException e) {
			LOG.debug("File " + path.toString() + " not found.");
			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		} catch (IOException e) {
			LOG.debug("IO error occurred: ", e);
			return NtStatus.UNSUCCESSFUL.getMask();
		}
	}

	@Override
	public long deleteFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("deleteFile() is called for " + path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to call deleteFile() on " + path.toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (Files.exists(path)) {
				if (handle.isRegularFile()) {
					//TODO: what is the best condition for the deletion? and is this case analysis correct?
					return ((OpenFile) handle).canBeDeleted() ? NtStatus.SUCCESS.getMask() : NtStatus.CANNOT_DELETE.getMask();
				} else {
					LOG.warn("Attempt of deleting a directory via deleteFile()");
					return NtStatus.ACCESS_DENIED.getMask();
				}
			} else {
				LOG.warn("File not found.");
				return NtStatus.OBJECT_NAME_NOT_FOUND.getMask();
			}
		}
	}

	@Override
	public long deleteDirectory(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("deleteDirectory() is called for " + path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to call deleteDirectory() on " + path.toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (!handle.isRegularFile()) {
				try (DirectoryStream emptyCheck = Files.newDirectoryStream(path)) {
					if (!emptyCheck.iterator().hasNext()) {
						return ErrorCode.SUCCESS.getMask();
					} else {
						return NtStatus.DIRECTORY_NOT_EMPTY.getMask();
					}

				} catch (IOException e) {
					return NtStatus.UNSUCCESSFUL.getMask();
				}
			} else {
				LOG.warn("Attempt of deleting a file with deleteDirectory().");
				return NtStatus.ACCESS_DENIED.getMask();
			}
		}
	}

	@Override
	public long moveFile(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("moveFile() is called for " + path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to call moveFile() on " + path.toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			try {
				if (rawReplaceIfExisting) {
					Files.move(path, getRootedPath(rawNewFileName), StandardCopyOption.REPLACE_EXISTING);
				} else {
					Files.move(path, getRootedPath(rawNewFileName));
				}
				return ErrorCode.SUCCESS.getMask();
			} catch (FileAlreadyExistsException e) {
				LOG.debug("File " + path.toString() + " already exists at new location.");
				return ErrorCode.ERROR_FILE_EXISTS.getMask();
			} catch (DirectoryNotEmptyException e) {
				LOG.debug("Directoy to move is not emtpy.");
				return NtStatus.DIRECTORY_NOT_EMPTY.getMask();
			} catch (IOException e) {
				LOG.warn("IO error occured while moving file" + path.toString());
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	@Override
	public long setEndOfFile(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("setEndOfFile() is called for " + path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to call setEndOfFile() on " + path.toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			OpenHandle handle = fac.get(dokanyFileInfo.Context);
			if (!handle.isRegularFile()) {
				try {
					((OpenFile) handle).truncate(rawByteOffset);
					return NtStatus.SUCCESS.getMask();
				} catch (IOException e) {
					LOG.error("Error while truncating file.");
					NtStatus.UNSUCCESSFUL.getMask();
				}
			} else {
				LOG.warn("Attempt of set EOF of a direcetory.");
				return NtStatus.ACCESS_DENIED.getMask();
			}
			return 0;
		}
	}

	@Override
	public long setAllocationSize(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("setAllocationSize() is called for " + path.toString());
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
		LOG.trace("getDiskFreeSpace() is called.");
		if (fileStore != null) {
			try {
				totalNumberOfBytes.setValue(fileStore.getTotalSpace());
				freeBytesAvailable.setValue(fileStore.getUsableSpace());
				totalNumberOfFreeBytes.setValue(fileStore.getUnallocatedSpace());
				return ErrorCode.SUCCESS.getMask();
			} catch (IOException e) {
				LOG.error("Unable to detect disk space status:", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		} else {
			LOG.info("Information about disk space is not available.");
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
		return 0;
	}

	@Override
	public long unmounted(DokanyFileInfo dokanyFileInfo) {
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
		return Paths.get(root.toString(), rawPath.toString());
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
