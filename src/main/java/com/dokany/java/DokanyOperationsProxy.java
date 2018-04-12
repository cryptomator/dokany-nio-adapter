package com.dokany.java;


/**
 * Implementation of {@link com.dokany.java.DokanyOperations} which connects to {@link com.dokany.java.DokanyFileSystem}.
 */
final class DokanyOperationsProxy extends com.dokany.java.DokanyOperations {

	DokanyOperationsProxy(final DokanyFileSystem fileSystem) {
		super.ZwCreateFile = fileSystem::zwCreateFile;
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

}