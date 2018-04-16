package org.cryptomator.frontend.dokan;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyOperations;
import com.dokany.java.DokanyUtils;
import com.dokany.java.constants.CreationDisposition;
import com.dokany.java.constants.ErrorCode;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.constants.NtStatus;
import com.dokany.java.structure.ByHandleFileInfo;
import com.dokany.java.structure.DokanyFileInfo;
import com.dokany.java.structure.FullFileInfo;
import com.google.common.collect.Sets;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
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

public class ReadOnlyAdapter implements DokanyFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAdapter.class);
	private final Path root;
	private final OpenFileFactory fac;

	public ReadOnlyAdapter(Path root) {
		this.root = root;
		this.fac = new OpenFileFactory();
	}


	/**
	 * 1. A FileChannel is ALWAYS OPENED! (no matter the openOption)
	 * 2. currently only the createDispostion parameter is used
	 */
	@Override
	public long zwCreateFile(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath.toString());
		try {
			CreationDisposition creationDispositions = DokanyUtils.enumFromInt(rawCreateDisposition, CreationDisposition.values());

			ErrorCode err = ErrorCode.SUCCESS;
			Set<OpenOption> openOptions = Sets.newHashSet();
			if (Files.exists(path)) {
				if (Files.isDirectory(path) && Files.isReadable(path)) {
					dokanyFileInfo.IsDirectory = 1;
					return ErrorCode.SUCCESS.getMask();
				} else {
					switch (creationDispositions) {
						case CREATE_NEW:
							return ErrorCode.ERROR_FILE_EXISTS.getMask();
						case CREATE_ALWAYS:
							openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
							err = ErrorCode.OBJECT_NAME_COLLISION;
							break;
						case OPEN_EXISTING:
							//READ, due to READONLY
							openOptions.add(StandardOpenOption.READ);
							break;
						case OPEN_ALWAYS:
							openOptions.add(StandardOpenOption.READ);
							err = ErrorCode.ERROR_ALREADY_EXISTS;
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
						openOptions.add(StandardOpenOption.CREATE);
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
					case TRUNCATE_EXISTING:
						//will fail
						return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
					default:
						throw new IllegalStateException("Unknown createDispostion attribute: " + creationDispositions.name());
				}
			}
			dokanyFileInfo.Context = fac.open(path, openOptions);
			return err.getMask();
		} catch (IOException e) {
			LOG.error("IO error: ", e);
			return NtStatus.UNSUCCESSFUL.getMask();
		}
	}

	/**
	 * The fileHandle is already closed here, due to the requirements of the dokany implementation to delete a file in the cleanUp method
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	@Override
	public void cleanup(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		try {
			fac.close(dokanyFileInfo.Context);
			if (dokanyFileInfo.deleteOnClose()) {
				try {
					Files.delete(getRootedPath(rawPath.toString()));
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
		dokanyFileInfo.Context = 0;
	}

	@Override
	public long readFile(WString rawPath, Pointer rawBuffer, int rawBufferLength, IntByReference rawReadLength, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long writeFile(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long flushFileBuffers(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long getFileInformation(WString fileName, ByHandleFileInfo handleFileInfo, DokanyFileInfo dokanyFileInfo) {
		Path p = getRootedPath(fileName.toString());
		try {
			FullFileInfo data = getFileInfo(p);
			data.copyTo(handleFileInfo);
			return ErrorCode.SUCCESS.getMask();
		} catch (FileNotFoundException e) {
			LOG.error("Could not found File");
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
		} catch (FileNotFoundException e) {
			LOG.error("Could not found File");
			throw e;
		} catch (IOException e) {
			LOG.error("IO error occured: ", e);
			throw e;
		}
	}

	@Override
	public long findFiles(WString rawPath, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long findFilesWithPattern(WString fileName, WString searchPattern, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(fileName.toString());

		LOG.trace("FindFilesWithPattern {}", path.toString());
		Set<WinBase.WIN32_FIND_DATA> findings = Sets.newHashSet();
		try (Stream<Path> stream = Files.list(path)) {
			//stream.filter(path1 -> path.toString().contains(pattern)).map(FileUtil::pathToFindData);
			Stream<Path> streamByPattern;
			if (searchPattern == null || searchPattern.equals("*")) {
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
		Path path = getRootedPath(rawPath.toString());
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
		Path p = getRootedPath(rawPath.toString());
		try {
			Files.setAttribute(p, "basic:creationTime", FileTime.fromMillis(rawCreationTime.toDate().getTime()));
			Files.setAttribute(p, "basic:lastAccessTime", FileTime.fromMillis(rawLastAccessTime.toDate().getTime()));
			Files.setAttribute(p, "basic:lastModificationTime", FileTime.fromMillis(rawLastWriteTime.toDate().getTime()));
			return ErrorCode.SUCCESS.getMask();
		} catch (FileNotFoundException e) {
			LOG.trace("File not found.");
			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		} catch (IOException e) {
			LOG.debug("IO exception occurred: ", e);
			return NtStatus.UNSUCCESSFUL.getMask();
		}
	}

	@Override
	public long deleteFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long deleteDirectory(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long moveFile(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long setEndOfFile(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long setAllocationSize(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
		//TODO: do we need the path?
		//Path path = root.resolve(rawPath.toString());
		try {
			fac.get(dokanyFileInfo.Context).truncate(rawLength);
		} catch (IOException e) {
			return ErrorCode.ERROR_WRITE_FAULT.getMask();
		}
		return ErrorCode.SUCCESS.getMask();
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
		return 0;
	}

	@Override
	public long getVolumeInformation(Pointer rawVolumeNameBuffer, int rawVolumeNameSize, IntByReference rawVolumeSerialNumber, IntByReference rawMaximumComponentLength, IntByReference rawFileSystemFlags, Pointer rawFileSystemNameBuffer, int rawFileSystemNameSize, DokanyFileInfo dokanyFileInfo) {
		return 0;
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
		return 0;
	}

	@Override
	public long setFileSecurity(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public void fillWin32FindData(WinBase.WIN32_FIND_DATA rawFillFindData, DokanyFileInfo dokanyFileInfo) {

	}

	@Override
	public long findStreams(WString rawPath, DokanyOperations.FillWin32FindStreamData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	private Path getRootedPath(String rawPath) {
		return Paths.get(root.toString(), rawPath);
	}
}
