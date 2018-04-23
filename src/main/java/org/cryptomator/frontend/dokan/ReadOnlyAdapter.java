package org.cryptomator.frontend.dokan;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyOperations;
import com.dokany.java.DokanyUtils;
import com.dokany.java.constants.CreationDisposition;
import com.dokany.java.constants.ErrorCode;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.constants.NtStatus;
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
import java.nio.file.FileStore;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: change the behaviour, sucht that
 * 3. in zwCreateFile() if we just create ( and NOT open the file), the context stays zero
 * <p>
 * TODO: currently file deletion throws no error and just executes the command and after a refresh the file is still there -> change it!
 * TODO: currently zwCreateFile()-call only sends a CREATE_NEW flag, thus setting the context fails und one cannot read the file content! what should we do?
 */
public class ReadOnlyAdapter implements DokanyFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAdapter.class);

	protected final Path root;
	protected final OpenFileFactory fac;
	protected final FileStore fileStore;

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
	}


	/**
	 * currently only the createDispostion parameter is used
	 */
	@Override
	public long zwCreateFile(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("zwCreateFile() is called for: " + path.toString());
		try {
			CreationDisposition creationDispositions = DokanyUtils.enumFromInt(rawCreateDisposition, CreationDisposition.values());

			ErrorCode err = ErrorCode.SUCCESS;
			Set<OpenOption> openOptions = Sets.newHashSet();
			LOG.debug("Create Disposition flag is " + creationDispositions.name());
			if (Files.exists(path)) {
				if (Files.isDirectory(path) && Files.isReadable(path)) {
					dokanyFileInfo.IsDirectory = 1;
					return ErrorCode.SUCCESS.getMask();
				} else {
					switch (creationDispositions) {
						case CREATE_NEW:
							openOptions.add(StandardOpenOption.CREATE);
							err = ErrorCode.ERROR_FILE_EXISTS;
							break;
						case CREATE_ALWAYS:
							openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
							err = ErrorCode.OBJECT_NAME_COLLISION;
							break;
						case OPEN_EXISTING:
							//READ, due to READONLY
							openOptions.add(StandardOpenOption.READ);
							//WRITE because the READWRITE adapter does not overwrite this method
							openOptions.add(StandardOpenOption.WRITE);
							break;
						case OPEN_ALWAYS:
							openOptions.add(StandardOpenOption.READ);
							//WRITE because the READWRITE adapter does not overwrite this method
							openOptions.add(StandardOpenOption.WRITE);
							err = ErrorCode.OBJECT_NAME_COLLISION;
							break;
						case TRUNCATE_EXISTING:
							openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
							break;
						default:
							throw new IllegalStateException("Unknown createDispostion attribute: " + creationDispositions.name());
					}
				}
			} else {
				switch (creationDispositions) {
					case CREATE_NEW:
						openOptions.add(StandardOpenOption.CREATE_NEW);
						break;
					case CREATE_ALWAYS:
						openOptions.add(StandardOpenOption.CREATE);
						break;
					case OPEN_EXISTING:
						//will fail
						return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
					case OPEN_ALWAYS:
						openOptions.add(StandardOpenOption.CREATE);
						err = ErrorCode.OBJECT_NAME_COLLISION;
						break;
					case TRUNCATE_EXISTING:
						//will fail
						return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
					default:
						throw new IllegalStateException("Unknown createDispostion attribute: " + creationDispositions.name());
				}
			}

			if (dokanyFileInfo.writeToEndOfFile()) {
				openOptions.add(StandardOpenOption.APPEND);
			}

			dokanyFileInfo.Context = fac.open(path, openOptions);
			return err.getMask();
		} catch (IOException e) {
			LOG.error("IO error: ", e);
			return NtStatus.UNSUCCESSFUL.getMask();
		}
	}

	/**
	 * the flag of {@link DokanyFileInfo#DeleteOnClose} is ignored due to ReadOnly
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	@Override
	public void cleanup(WString rawPath, DokanyFileInfo dokanyFileInfo) {
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
		LOG.trace("closeFile() is called for " + getRootedPath(rawPath).toString());
		dokanyFileInfo.Context = 0;
	}

	@Override
	public long readFile(WString rawPath, Pointer rawBuffer, int rawBufferLength, IntByReference rawReadLength, long rawOffset, DokanyFileInfo dokanyFileInfo) {
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
		LOG.trace("findFiles() is called for " + getRootedPath(rawPath).toString());
		return findFilesWithPattern(rawPath, new WString("*"), rawFillFindData, dokanyFileInfo);
	}

	@Override
	public long findFilesWithPattern(WString fileName, WString searchPattern, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
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
		Path path = getRootedPath(rawPath);
		LOG.trace("setFileAttributes() is called for " + path.toString());
		//TODO; is this check necessary? we already checked this in zwCreateFile (via the open()-call
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
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long setFileSecurity(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public void fillWin32FindData(WinBase.WIN32_FIND_DATA rawFillFindData, DokanyFileInfo dokanyFileInfo) {

	}

	@Override
	public long findStreams(WString rawPath, DokanyOperations.FillWin32FindStreamData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	protected Path getRootedPath(WString rawPath) {
		return Paths.get(root.toString(), rawPath.toString());
	}
}
