package org.cryptomator.frontend.dokany.internal;


import org.cryptomator.frontend.dokany.internal.constants.NtStatus;
import org.cryptomator.frontend.dokany.internal.constants.Win32ErrorCode;
import org.cryptomator.frontend.dokany.internal.structure.ByHandleFileInfo;
import org.cryptomator.frontend.dokany.internal.structure.DokanyFileInfo;
import org.cryptomator.frontend.dokany.internal.structure.DokanyOperations;
import com.sun.jna.Callback;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link DokanyOperations} which connects to {@link DokanyFileSystem}.
 */
final class DokanyOperationsProxy extends DokanyOperations {

	private final static Logger LOG = LoggerFactory.getLogger(DokanyOperationsProxy.class);

	private final CountDownLatch mountSuccessSignaler;
	private final DokanyFileSystem fileSystem;
	private final CallbackThreadInitializer callbackThreadInitializer;
	private final List<Callback> usedCallbacks = new ArrayList<>();

	DokanyOperationsProxy(CountDownLatch mountSuccessSignaler, final DokanyFileSystem fileSystem, String nativeThreadNamePrefix) {
		this.mountSuccessSignaler = mountSuccessSignaler;
		this.fileSystem = fileSystem;
		this.callbackThreadInitializer = new DokanCallbackThreadInitializer(nativeThreadNamePrefix);
		super.ZwCreateFile = new ZwCreateFileProxy();
		usedCallbacks.add(super.ZwCreateFile);

		super.CloseFile = fileSystem::closeFile;
		usedCallbacks.add(super.CloseFile);

		super.Cleanup = fileSystem::cleanup;
		usedCallbacks.add(super.Cleanup);

		super.ReadFile = new ReadFileProxy();
		usedCallbacks.add(super.ReadFile);

		super.WriteFile = new WriteFileProxy();
		usedCallbacks.add(super.WriteFile);

		super.FlushFileBuffers = new FlushFileBuffersProxy();
		usedCallbacks.add(super.FlushFileBuffers);

		super.GetFileInformation = new GetFileInformationProxy();
		usedCallbacks.add(super.GetFileInformation);

		super.GetVolumeInformation = new GetVolumeInformationProxy();
		usedCallbacks.add(super.GetVolumeInformation);

		super.GetDiskFreeSpace = new GetDiskFreeSpaceProxy();
		usedCallbacks.add(super.GetDiskFreeSpace);

		super.FindFiles = new FindFilesProxy();
		usedCallbacks.add(super.FindFiles);

		super.FindFilesWithPattern = new FindFilesWithPatternProxy();
		usedCallbacks.add(super.FindFilesWithPattern);

		super.SetFileAttributes = new SetFileAttributesProxy();
		usedCallbacks.add(super.SetFileAttributes);

		super.SetFileTime = new SetFileTimeProxy();
		usedCallbacks.add(super.SetFileTime);

		super.DeleteFile = new DeleteFileProxy();
		usedCallbacks.add(super.DeleteFile);

		super.DeleteDirectory = new DeleteDirectoryProxy();
		usedCallbacks.add(super.DeleteDirectory);

		super.MoveFile = new MoveFileProxy();
		usedCallbacks.add(super.MoveFile);

		super.SetEndOfFile = new SetEndOfFileProxy();
		usedCallbacks.add(super.SetEndOfFile);

		super.SetAllocationSize = new SetAllocationSizeProxy();
		usedCallbacks.add(super.SetAllocationSize);

		super.LockFile = null;
		//usedCallbacks.add(super.LockFile);

		super.UnlockFile = null;
		//usedCallbacks.add(super.UnlockFile);

		super.Mounted = new MountedProxy();
		usedCallbacks.add(super.Mounted);

		super.Unmounted = new UnmountedProxy();
		usedCallbacks.add(super.Unmounted);

		super.GetFileSecurity = ((rawPath, rawSecurityInformation, rawSecurityDescriptor, rawSecurityDescriptorLength, rawSecurityDescriptorLengthNeeded, dokanyFileInfo) -> NtStatus.NOT_IMPLEMENTED.getMask());
		usedCallbacks.add(super.GetFileSecurity);

		super.SetFileSecurity = null;
		//callbacks.add(super.SetFileSecurity);

		super.FindStreams = null;
		//callbacks.add(super.FindStreams);

		usedCallbacks.forEach(callback -> Native.setCallbackThreadInitializer(callback, callbackThreadInitializer));
	}

	class ZwCreateFileProxy implements ZwCreateFile {

		@Override
		public long callback(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
			try {
				int win32ErrorCode = fileSystem.zwCreateFile(rawPath, securityContext, rawDesiredAccess, rawFileAttributes, rawShareAccess, rawCreateDisposition, rawCreateOptions, dokanyFileInfo);
				//little cheat for issue #24
				if (win32ErrorCode == Win32ErrorCode.ERROR_INVALID_STATE.getMask()) {
					return NtStatus.FILE_IS_A_DIRECTORY.getMask();
				} else {
					return NativeMethods.DokanNtStatusFromWin32(win32ErrorCode);
				}
			} catch (Exception e) {
				LOG.warn("zwCreateFile(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
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
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.readFile(rawPath, rawBuffer, rawBufferLength, rawReadLength, rawOffset, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("readFile(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class WriteFileProxy implements WriteFile {

		@Override
		public long callback(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.writeFile(rawPath, rawBuffer, rawNumberOfBytesToWrite, rawNumberOfBytesWritten, rawOffset, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("writeFile(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class FlushFileBuffersProxy implements FlushFileBuffers {

		@Override
		public long callback(WString rawPath, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.flushFileBuffers(rawPath, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("flushFileBuffers(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class GetFileInformationProxy implements GetFileInformation {

		@Override
		public long callback(WString fileName, ByHandleFileInfo handleFileInfo, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.getFileInformation(fileName, handleFileInfo, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("getFileInformation(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class FindFilesProxy implements FindFiles {

		@Override
		public long callback(WString rawPath, FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.findFiles(rawPath, rawFillFindData, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("findFiles(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class FindFilesWithPatternProxy implements FindFilesWithPattern {

		@Override
		public long callback(WString fileName, WString searchPattern, FillWin32FindData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.findFilesWithPattern(fileName, searchPattern, rawFillFindData, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("findFilesWithPattern(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class SetFileAttributesProxy implements SetFileAttributes {

		@Override
		public long callback(WString rawPath, int rawAttributes, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.setFileAttributes(rawPath, rawAttributes, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("setFileAttributes(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class SetFileTimeProxy implements SetFileTime {

		@Override
		public long callback(WString rawPath, WinBase.FILETIME rawCreationTime, WinBase.FILETIME rawLastAccessTime, WinBase.FILETIME rawLastWriteTime, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.setFileTime(rawPath, rawCreationTime, rawLastAccessTime, rawLastWriteTime, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("setFileTime(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class DeleteFileProxy implements DeleteFile {

		@Override
		public long callback(WString rawPath, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.deleteFile(rawPath, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("deleteFile(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class DeleteDirectoryProxy implements DeleteDirectory {

		@Override
		public long callback(WString rawPath, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.deleteDirectory(rawPath, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("deleteDirectory(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class MoveFileProxy implements MoveFile {

		@Override
		public long callback(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.moveFile(rawPath, rawNewFileName, rawReplaceIfExisting, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("moveFile(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class SetEndOfFileProxy implements SetEndOfFile {

		@Override
		public long callback(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.setEndOfFile(rawPath, rawByteOffset, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("setEndOfFile(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class SetAllocationSizeProxy implements SetAllocationSize {

		@Override
		public long callback(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.setAllocationSize(rawPath, rawLength, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("setAllocationSize(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class LockFileProxy implements LockFile {

		@Override
		public long callback(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.lockFile(rawPath, rawByteOffset, rawLength, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("lockFile(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class UnlockFileProxy implements UnlockFile {

		@Override
		public long callback(WString rawPath, long rawByteOffset, long rawLength, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.unlockFile(rawPath, rawByteOffset, rawLength, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("unlockFile(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class GetDiskFreeSpaceProxy implements GetDiskFreeSpace {

		@Override
		public long callback(LongByReference freeBytesAvailable, LongByReference totalNumberOfBytes, LongByReference totalNumberOfFreeBytes, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.getDiskFreeSpace(freeBytesAvailable, totalNumberOfBytes, totalNumberOfFreeBytes, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("getDiskFreeSpace(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class GetVolumeInformationProxy implements GetVolumeInformation {

		@Override
		public long callback(Pointer rawVolumeNameBuffer, int rawVolumeNameSize, IntByReference rawVolumeSerialNumber, IntByReference rawMaximumComponentLength, IntByReference rawFileSystemFlags, Pointer rawFileSystemNameBuffer, int rawFileSystemNameSize, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.getVolumeInformation(rawVolumeNameBuffer, rawVolumeNameSize, rawVolumeSerialNumber, rawMaximumComponentLength, rawFileSystemFlags, rawFileSystemNameBuffer, rawFileSystemNameSize, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("getVolumeInformation(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class MountedProxy implements Mounted {

		@Override
		public long mounted(DokanyFileInfo dokanyFileInfo) {
			mountSuccessSignaler.countDown();
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.mounted(dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("mounted(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class UnmountedProxy implements Unmounted {

		@Override
		public long unmounted(DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.unmounted(dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("unmounted(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class GetFileSecurityProxy implements GetFileSecurity {

		@Override
		public long callback(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, IntByReference rawSecurityDescriptorLengthNeeded, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.getFileSecurity(rawPath, rawSecurityInformation, rawSecurityDescriptor, rawSecurityDescriptorLength, rawSecurityDescriptorLengthNeeded, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("getFileSecurity(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class SetFileSecurityProxy implements SetFileSecurity {

		@Override
		public long callback(WString rawPath, int rawSecurityInformation, Pointer rawSecurityDescriptor, int rawSecurityDescriptorLength, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.setFileSecurity(rawPath, rawSecurityInformation, rawSecurityDescriptor, rawSecurityDescriptorLength, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("setFileSecurity(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	class FindStreamsProxy implements FindStreams {

		@Override
		public long callback(WString rawPath, FillWin32FindStreamData rawFillFindData, DokanyFileInfo dokanyFileInfo) {
			try {
				return NativeMethods.DokanNtStatusFromWin32(fileSystem.findStreams(rawPath, rawFillFindData, dokanyFileInfo));
			} catch (Exception e) {
				LOG.warn("findStreams(): Uncaught exception. Returning generic failure code.", e);
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	private static class DokanCallbackThreadInitializer extends CallbackThreadInitializer {

		private String prefix;
		private AtomicInteger counter;

		DokanCallbackThreadInitializer(String prefix) {
			super(true, false, prefix, new ThreadGroup(prefix));
			this.prefix = prefix;
			this.counter = new AtomicInteger(0);
		}

		@Override
		public String getName(Callback cb) {
			return prefix + counter.getAndIncrement();
		}
	}

}