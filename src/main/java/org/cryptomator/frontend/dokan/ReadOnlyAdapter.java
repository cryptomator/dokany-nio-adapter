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
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Set;

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
	 * 2. currently only the createDispostion paramteter is used
	 */
	@Override
	public long zwCreateFile(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
		Path path = root.resolve(rawPath.toString());
		try {
			CreationDisposition creationDispositions = DokanyUtils.enumFromInt(rawCreateDisposition, CreationDisposition.values());

			ErrorCode err = ErrorCode.SUCCESS;
			Set<OpenOption> openOptions = Sets.newHashSet();
			if (Files.exists(path)) {
				switch (creationDispositions) {
					case CREATE_NEW:
						return ErrorCode.ERROR_FILE_EXISTS.getMask();
					case CREATE_ALWAYS:
						openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
						err = ErrorCode.ERROR_ALREADY_EXISTS;
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
						throw new IllegalStateException("Unknow createDispostion attribute: " + creationDispositions.name());
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
						err = ErrorCode.ERROR_ALREADY_EXISTS;
					case TRUNCATE_EXISTING:
						//will fail
						return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
					default:
						throw new IllegalStateException("Unknow createDispostion attribute: " + creationDispositions.name());
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
	 * The fileHandle is already closed here, due to the requierements of the dokany implemenation to delete a file in the cleanUp method
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
					Files.delete(root.resolve(rawPath.toString()));
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
		Path p = root.resolve(fileName.toString());
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
			data.copyTo(handleFileInfo);
			return ErrorCode.SUCCESS.getMask();
		} catch (FileNotFoundException e){
			LOG.error("Could not found File");
			return ErrorCode.ERROR_FILE_NOT_FOUND.getMask();
		} catch (IOException e) {
			LOG.error("IO error occured: ",e);
			return NtStatus.UNSUCCESSFUL.getMask();
		}
	}

	@Override
	public long findFiles(WString rawPath, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long findFilesWithPattern(WString fileName, WString searchPattern, DokanyOperations.FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
		return 0;
	}

	@Override
	public long setFileAttributes(WString rawPath, int rawAttributes, DokanyFileInfo dokanyFileInfo) {
		Path path = root.resolve(rawPath.toString());
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
		return 0;
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
}
