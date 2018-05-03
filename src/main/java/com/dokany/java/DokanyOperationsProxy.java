package com.dokany.java;


import com.dokany.java.structure.DokanyFileInfo;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.ptr.IntByReference;

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
		super.ReadFile = fileSystem::readFile;
		super.WriteFile = fileSystem::writeFile;
		super.FlushFileBuffers = fileSystem::flushFileBuffers;
		super.GetFileInformation = fileSystem::getFileInformation;
		super.GetVolumeInformation = fileSystem::getVolumeInformation;
		super.GetDiskFreeSpace = fileSystem::getDiskFreeSpace;
		super.FindFiles = fileSystem::findFiles;
		super.FindFilesWithPattern = fileSystem::findFilesWithPattern;
		super.SetFileAttributes = fileSystem::setFileAttributes;
		super.SetFileTime = fileSystem::setFileTime;
		super.DeleteFile = fileSystem::deleteFile;
		super.DeleteDirectory = fileSystem::deleteDirectory;
		super.MoveFile = fileSystem::moveFile;
		super.SetEndOfFile = fileSystem::setEndOfFile;
		super.SetAllocationSize = fileSystem::setAllocationSize;
		super.LockFile = fileSystem::lockFile;
		super.UnlockFile = fileSystem::unlockFile;
		super.Mounted = fileSystem::mounted;
		super.Unmounted = fileSystem::unmounted;
		super.GetFileSecurity = fileSystem::getFileSecurity;
		super.SetFileSecurity = fileSystem::setFileSecurity;
		super.FindStreams = fileSystem::findStreams;
	}

	class ZwCreateFileProxy implements ZwCreateFile{

		@Override
		public long callback(WString rawPath, WinBase.SECURITY_ATTRIBUTES securityContext, int rawDesiredAccess, int rawFileAttributes, int rawShareAccess, int rawCreateDisposition, int rawCreateOptions, DokanyFileInfo dokanyFileInfo) {
			IntByReference createDisposition = new IntByReference();
			IntByReference desiredAccess = new IntByReference();
			IntByReference fileAttributeFlags = new IntByReference();
			NativeMethods.DokanMapKernelToUserCreateFileFlags(rawDesiredAccess, rawFileAttributes,rawCreateOptions, rawCreateDisposition,desiredAccess, fileAttributeFlags, createDisposition);
			return fileSystem.zwCreateFile(rawPath, securityContext, desiredAccess.getValue(), fileAttributeFlags.getValue(), rawShareAccess, createDisposition.getValue(), rawCreateOptions, dokanyFileInfo );
		}
	}

}