package com.dokany.java;


import com.dokany.java.constants.NtStatus;
import com.dokany.java.constants.Win32ErrorCode;
import com.dokany.java.structure.ByHandleFileInfo;
import com.dokany.java.structure.DokanyFileInfo;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/**
 * Implementation of {@link com.dokany.java.DokanyOperations} which connects to {@link com.dokany.java.DokanyFileSystem}.
 */
final class DokanyOperationsProxy extends com.dokany.java.DokanyOperations {

	private final DokanyFileSystem fileSystem;

	DokanyOperationsProxy(final DokanyFileSystem fileSystem) {
		this.fileSystem = fileSystem;
		super.ZwCreateFile = new ZwCreateFileProxy();
		super.CloseFile = fileSystem::closeFile;
		super.Cleanup = fileSystem::cleanup;
		super.ReadFile = new ReadFileProxy();
		super.WriteFile = new WriteFileProxy();
		super.FlushFileBuffers = new FlushFileBuffersProxy();
		super.GetFileInformation = new GetFileInformationProxy();
		super.GetVolumeInformation = new GetVolumeInformationProxy();
		super.GetDiskFreeSpace = new GetDiskFreeSpaceProxy();
		super.FindFiles = new FindFilesProxy();
		super.FindFilesWithPattern = null;
		super.SetFileAttributes = new SetFileAttributesProxy();
		super.SetFileTime = new SetFileTimeProxy();
		super.DeleteFile = new DeleteFileProxy();
		super.DeleteDirectory = new DeleteDirectoryProxy();
		super.MoveFile = new MoveFileProxy();
		super.SetEndOfFile = new SetEndOfFileProxy();
		super.SetAllocationSize = new SetAllocationSizeProxy();
		super.LockFile = null;
		super.UnlockFile = null;
		super.Mounted = new MountedProxy();
		super.Unmounted = new UnmountedProxy();
		super.GetFileSecurity = null;
		super.SetFileSecurity = null;
		super.FindStreams = null;
	}

	class ZwCreateFileProxy implements ZwCreateFile {

		@Override
		public long callback(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
			IntByReference createDisposition = new IntByReference();
			IntByReference desiredAccess = new IntByReference();
			IntByReference fileAttributeFlags = new IntByReference();
			NativeMethods.DokanMapKernelToUserCreateFileFlags(rawDesiredAccess, rawFileAttributes, rawCreateOptions, rawCreateDisposition, desiredAccess, fileAttributeFlags, createDisposition);
			int win32ErrorCode = fileSystem.zwCreateFile(rawPath, securityContext, desiredAccess.getValue(), fileAttributeFlags.getValue(), rawShareAccess, createDisposition.getValue(), rawCreateOptions, dokanyFileInfo);
			//little cheat for issue #24
			if (win32ErrorCode == Win32ErrorCode.ERROR_INVALID_STATE.getMask()) {
				return NtStatus.FILE_IS_A_DIRECTORY.getMask();
			} else {
				return NativeMethods.DokanNtStatusFromWin32(win32ErrorCode);
			}
		}
	}

	/**
	 * CloseFileProxy is not needed, because its callback return type is void.
	 */

	/**
	 * CleanupProxy is not needed, because its callback return type is void.
	 */

	class ReadFileProxy implements ReadFile {

		@Override
		public long callback(WString rawPath, Pointer rawBuffer, int rawBufferLength, IntByReference rawReadLength, long rawOffset, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.readFile(rawPath, rawBuffer, rawBufferLength, rawReadLength, rawOffset, dokanyFileInfo));
		}
	}

	class WriteFileProxy implements WriteFile {

		@Override
		public long callback(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.writeFile(rawPath, rawBuffer, rawNumberOfBytesToWrite, rawNumberOfBytesWritten, rawOffset, dokanyFileInfo));
		}
	}

	class FlushFileBuffersProxy implements FlushFileBuffers {

		@Override
		public long callback(WString rawPath, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.flushFileBuffers(rawPath, dokanyFileInfo));
		}
	}

	class GetFileInformationProxy implements GetFileInformation {

		@Override
		public long callback(WString fileName, ByHandleFileInfo handleFileInfo, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.getFileInformation(fileName, handleFileInfo, dokanyFileInfo));
		}
	}

	class FindFilesProxy implements FindFiles {

		@Override
		public long callback(WString rawPath, FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.findFiles(rawPath, rawFillFindData, dokanyFileInfo));
		}
	}

	class FindFilesWithPatternProxy implements FindFilesWithPattern {

		@Override
		public long callback(WString fileName, WString searchPattern, FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.findFilesWithPattern(fileName, searchPattern, rawFillFindData, dokanyFileInfo));
		}
	}

	class SetFileAttributesProxy implements SetFileAttributes {

		@Override
		public long callback(WString rawPath, int rawAttributes, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.setFileAttributes(rawPath, rawAttributes, dokanyFileInfo));
		}
	}

	class SetFileTimeProxy implements SetFileTime {

		@Override
		public long callback(WString rawPath, WinBase.FILETIME rawCreationTime, WinBase.FILETIME rawLastAccessTime, WinBase.FILETIME rawLastWriteTime, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.setFileTime(rawPath, rawCreationTime, rawLastAccessTime, rawLastWriteTime, dokanyFileInfo));
		}
	}

	class DeleteFileProxy implements DeleteFile {

		@Override
		public long callback(WString rawPath, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.deleteFile(rawPath, dokanyFileInfo));
		}
	}

	class DeleteDirectoryProxy implements DeleteDirectory {

		@Override
		public long callback(WString rawPath, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.deleteDirectory(rawPath, dokanyFileInfo));
		}
	}

	class MoveFileProxy implements MoveFile {

		@Override
		public long callback(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.moveFile(rawPath, rawNewFileName, rawReplaceIfExisting, dokanyFileInfo));
		}
	}

	class SetEndOfFileProxy implements SetEndOfFile {

		@Override
		public long callback(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.setEndOfFile(rawPath, rawByteOffset, dokanyFileInfo));
		}
	}

	class SetAllocationSizeProxy implements SetAllocationSize {

		@Override
		public long callback(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.setAllocationSize(rawPath, rawLength, dokanyFileInfo));
		}
	}

	class LockFileProxy implements LockFile {

		@Override
		public long callback(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.lockFile(rawPath, rawByteOffset, rawLength, dokanyFileInfo));
		}
	}

	class UnlockFileProxy implements UnlockFile {

		@Override
		public long callback(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.unlockFile(rawPath, rawByteOffset, rawLength, dokanyFileInfo));
		}
	}

	class GetDiskFreeSpaceProxy implements GetDiskFreeSpace {

		@Override
		public long callback(LongByReference freeBytesAvailable, LongByReference totalNumberOfBytes, LongByReference totalNumberOfFreeBytes, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.getDiskFreeSpace(freeBytesAvailable, totalNumberOfBytes, totalNumberOfFreeBytes, dokanyFileInfo));
		}
	}

	class GetVolumeInformationProxy implements GetVolumeInformation {

		@Override
		public long callback(Pointer rawVolumeNameBuffer, int rawVolumeNameSize, IntByReference rawVolumeSerialNumber, IntByReference rawMaximumComponentLength, IntByReference rawFileSystemFlags, Pointer rawFileSystemNameBuffer, int rawFileSystemNameSize, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.getVolumeInformation(rawVolumeNameBuffer, rawVolumeNameSize, rawVolumeSerialNumber, rawMaximumComponentLength, rawFileSystemFlags, rawFileSystemNameBuffer, rawFileSystemNameSize, dokanyFileInfo));
		}
	}

	class MountedProxy implements Mounted {

		@Override
		public long mounted(DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.mounted(dokanyFileInfo));
		}
	}

	class UnmountedProxy implements Unmounted {

		@Override
		public long unmounted(DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.unmounted(dokanyFileInfo));
		}
	}

	class GetFileSecurityProxy implements GetFileSecurity {

		@Override
		public long callback(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, IntByReference rawSecurityDescriptorLengthNeeded, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.getFileSecurity(rawPath, rawSecurityInformation, rawSecurityDescriptor, rawSecurityDescriptorLength, rawSecurityDescriptorLengthNeeded, dokanyFileInfo));
		}
	}

	class SetFileSecurityProxy implements SetFileSecurity {

		@Override
		public long callback(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.setFileSecurity(rawPath, rawSecurityInformation, rawSecurityDescriptor, rawSecurityDescriptorLength, dokanyFileInfo));
		}
	}

	class FindStreamsProxy implements FindStreams {

		@Override
		public long callback(WString rawPath, FillWin32FindStreamData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
			return NativeMethods.DokanNtStatusFromWin32(fileSystem.findStreams(rawPath, rawFillFindData, dokanyFileInfo));
		}
	}

}