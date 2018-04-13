package org.cryptomator.frontend.dokan;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyOperations;
import com.dokany.java.DokanyUtils;
import com.dokany.java.constants.ErrorCode;
import com.dokany.java.constants.FileAccess;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.ByHandleFileInfo;
import com.dokany.java.structure.DokanyFileInfo;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadOnlyAdapter implements DokanyFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAdapter.class);
	private final Path root;
	private final OpenFileFactory fac;

	public ReadOnlyAdapter(Path root) {
		this.root = root;
		this.fac = new OpenFileFactory();
	}


	@Override
	public long zwCreateFile(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
		Path path = root.resolve(rawPath.toString());
		//WinNT.HANDLE fileHandle = Kernel32.INSTANCE.CreateFile(path.toString(), rawDesiredAccess, rawShareAccess, securityContext, rawCreateDisposition, rawFileAttributes, null);
		//dokanyFileInfo.Context = Pointer.nativeValue(fileHandle.getPointer());
		try {
			dokanyFileInfo.Context = fac.open(path, FileUtil.accesRightsToOpenOptions(DokanyUtils.enumSetFromInt(rawDesiredAccess, FileAccess.values())));
		} catch (IOException e) {
			//TODO: does this method do the correct thingie?
			DokanyUtils.exceptionToErrorCode(e);
		}
		return ErrorCode.SUCCESS.getMask();
	}

	@Override
	public void cleanup(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		if(dokanyFileInfo.deleteOnClose()){
			try {
				Files.delete(root.resolve(rawPath.toString()));
			} catch (IOException e) {
				LOG.warn("Unable to delete File: "+e.getMessage());
			}
		}

	}

	@Override
	public void closeFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		//TODO: decide wether we use the jna functions or the classic java nio API
		//Kernel32.INSTANCE.CloseHandle() should be called here, if we Kernel32.INSCTANCE.CreateFile()
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
		return 0;
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
