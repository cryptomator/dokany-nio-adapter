package org.cryptomator.frontend.dokan;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyOperations;
import com.dokany.java.DokanyUtils;
import com.dokany.java.constants.CreationDisposition;
import com.dokany.java.constants.ErrorCode;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.constants.NtStatus;
import com.dokany.java.constants.SecurityInformation;
import com.dokany.java.structure.FullFileInfo;
import com.dokany.java.structure.FreeSpace;
import com.dokany.java.structure.VolumeInformation;
import com.dokany.java.structure.DokanyFileInfo;
import com.dokany.java.structure.ByHandleFileInfo;

import com.google.common.collect.Sets;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * TODO: currently file deletion throws no error and just executes the command and after a refresh the file is still there -> change it!
 * TODO: the whole dekstop.ini or autorun.inf files is currently filtered
 */
public class ReadOnlyAdapter implements DokanyFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAdapter.class);

	protected final Path root;
	protected final OpenFileFactory fac;
	protected final FileStore fileStore;
	protected final UserPrincipal user;

	private final VolumeInformation volumeInformation;
	private final FreeSpace freeSpace;

	public ReadOnlyAdapter(Path root, VolumeInformation volumeInformation, FreeSpace freeSpace) {
		this.root = root;
		this.volumeInformation = volumeInformation;
		this.freeSpace = freeSpace;
		this.fac = new OpenFileFactory();

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


	/**
	 * Creates always to a given path a openFile-object and setting the {@link DokanyFileInfo#Context} appropriately
	 * currently only the createDispostion parameter is used
	 */
	@Override
	public long zwCreateFile(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		LOG.trace("zwCreateFile() is called for: " + path.toString());
		Set<OpenOption> openOptions = Sets.newHashSet();
		openOptions.add(StandardOpenOption.READ);
		openOptions.add(StandardOpenOption.WRITE);
		CreationDisposition creationDisposition = DokanyUtils.enumFromInt(rawCreateDisposition, CreationDisposition.values());
		LOG.debug("Create Disposition flag is " + creationDisposition.name());
		if (Files.exists(path)) {
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
		if (Files.isRegularFile(path)) {
			return createFile(path, creationDisposition, openOptions, rawFileAttributes, dokanyFileInfo);
		} else {
			return createDirectory(path, creationDisposition, openOptions, rawFileAttributes, dokanyFileInfo);
		}
	}


	/**
	 * TODO: should the alreadyExists check be atomical with respect to the function call ?
	 * dokanyFileInfo.Context == 1
	 *
	 * @return
	 */
	private long createDirectory(Path path, CreationDisposition creationDisposition, Set<OpenOption> openOptions, int rawFileAttributes, DokanyFileInfo dokanyFileInfo) {
		final int mask = creationDisposition.getMask();
		//createDirectory request
		if (mask == CreationDisposition.CREATE_NEW.getMask() || mask == CreationDisposition.OPEN_ALWAYS.getMask()) {
			try {
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
		// so, assuming we did not set the isDirectory flag
		if (Files.isRegularFile(path)) {
			//then we try to open a file as a directory
			//sh*t
			return 0xC00000BAL;
		} else {
			// we open the directory in some kinda way
			try {
				setFileAttributes(path, rawFileAttributes);
				dokanyFileInfo.Context = fac.open(path, openOptions, FileUtil.getStandardAclPermissions(user));
			} catch (IOException e) {
				//TODO: fine grained error handling
				return NtStatus.UNSUCCESSFUL.getMask();
			}

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
			//TODO
			e.printStackTrace();
		}
		//we want to create a file
		//system or hidden file?
		if (attr != null
				&& (mask == CreationDisposition.TRUNCATE_EXISTING.getMask()
				|| mask == CreationDisposition.CREATE_ALWAYS.getMask()
		)
				&& (((rawFileAttributes & FileAttribute.HIDDEN.getMask()) == 0 && attr.isHidden())
				|| ((rawFileAttributes & FileAttribute.SYSTEM.getMask()) == 0 && attr.isSystem())
		)
				) {
			//cannot overwrite hidden or system file
			return NtStatus.ACCESS_DENIED.getMask();
		}
		//read-only?
		else if ((attr != null && attr.isReadOnly() || ((rawFileAttributes & FileAttribute.READONLY.getMask()) != 0))
				&& dokanyFileInfo.DeleteOnClose != 0
				) {
			//cannot delete file
			return NtStatus.CANNOT_DELETE.getMask();

		} else {
			try {
				dokanyFileInfo.Context = fac.open(path, openOptions, FileUtil.getStandardAclPermissions(user));
				setFileAttributes(path, rawFileAttributes);
			} catch (FileAlreadyExistsException e) {
				if (mask == CreationDisposition.OPEN_ALWAYS.getMask() || mask == CreationDisposition.CREATE_ALWAYS.getMask()) {
					return NtStatus.OBJECT_NAME_COLLISION.getMask();
				} else {
					NtStatus.UNSUCCESSFUL.getMask();
				}
			} catch (IOException e) {
				return NtStatus.UNSUCCESSFUL.getMask();
			}

		}
		return ErrorCode.SUCCESS.getMask();
	}

	/**
	 * the flag of {@link DokanyFileInfo#DeleteOnClose} is ignored due to ReadOnly
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	@Override
	public void cleanup(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return;
		}
		LOG.trace("cleanup() is called for: " + getRootedPath(rawPath).toString());
		try {
			if (dokanyFileInfo.Context != 0) {
				fac.close(dokanyFileInfo.Context);
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
			LOG.warn("Attempt to read file " + getRootedPath(rawPath).toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			try {
				rawReadLength.setValue(fac.get(dokanyFileInfo.Context).read(rawBuffer, rawBufferLength, rawOffset));
			} catch (IOException e) {
				LOG.error("Error while reading file: ", e);
				return ErrorCode.ERROR_READ_FAULT.getMask();
			}
			return ErrorCode.SUCCESS.getMask();
		}

	}

	@Override
	public long writeFile(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("writeFile() is called for " + getRootedPath(rawPath).toString());
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long flushFileBuffers(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("flushFileBuffers() is called for " + getRootedPath(rawPath).toString());
		return NtStatus.UNSUCCESSFUL.getMask();
	}

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

	/**
	 * TODO: is this a read or write thing?
	 *
	 * @param rawPath
	 * @param rawAttributes
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return
	 */
	@Override
	public long setFileAttributes(WString rawPath, int rawAttributes, DokanyFileInfo dokanyFileInfo) {
		if (isSkipFile(rawPath)) {
			return ErrorCode.SUCCESS.getMask();
		}
		Path path = getRootedPath(rawPath);
		LOG.trace("setFileAttributes() is called for " + path.toString());
		//TODO; is this check necessary? we already checked this in zwCreateFile (via the open()-call
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
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long deleteDirectory(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long moveFile(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long setEndOfFile(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long setAllocationSize(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long lockFile(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long unlockFile(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
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
	 * TODO: check this method (this is just a copy-paste of the DokanyOperationsProxy of dokan-java-project!)
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
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long unmounted(DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long getFileSecurity(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, IntByReference rawSecurityDescriptorLengthNeeded, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		SecurityInformation securityInfo = DokanyUtils.enumFromInt(rawSecurityInformation, SecurityInformation.values());
		LOG.debug("getFileSecurity() is called for {} {}", path.toString(), securityInfo.name());
		if (Files.exists(path)) {
			byte[] emptySD = getEmptySecurityDescriptor();
			rawSecurityDescriptorLengthNeeded.setValue(emptySD.length);
			if (emptySD.length <= rawSecurityDescriptorLength) {
				rawSecurityDescriptor.write(0L, emptySD, 0, emptySD.length);
				return ErrorCode.SUCCESS.getMask();
			} else {
				return NtStatus.BUFFER_OVERFLOW.getMask();
			}
		} else {
			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		}
	}

	@Override
	public long setFileSecurity(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("setFileSecurity() is called for " + path.toString());
		if (Files.exists(path)) {
			byte[] emptySD = getEmptySecurityDescriptor();
			if (emptySD.length <= rawSecurityDescriptorLength) {
				rawSecurityDescriptor.write(0L, emptySD, 0, emptySD.length);
				return ErrorCode.SUCCESS.getMask();
			} else {
				return NtStatus.BUFFER_OVERFLOW.getMask();
			}
		} else {
			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		}
	}

	@Override
	public void fillWin32FindData(WinBase.WIN32_FIND_DATA rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return;
	}

	@Override
	public long findStreams(WString rawPath, DokanyOperations.FillWin32FindStreamData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
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

	public static byte[] getEmptySecurityDescriptor() {
		return new byte[]{
				0x01, //revision
				0x00, //sbz1
				0x01,
				-64, //control flag indicating a self relative sec. desc. and setting the last two bits (owner and group default)
				0x00,
				0x00,
				0x00,
				0x00, //owner offset
				0x00,
				0x00,
				0x00,
				0x00, //group offset
				0x00,
				0x00,
				0x00,
				0x00, //sacl offset
				0x00,
				0x00,
				0x00,
				0x00, //dacl offset
		};

	}

}
