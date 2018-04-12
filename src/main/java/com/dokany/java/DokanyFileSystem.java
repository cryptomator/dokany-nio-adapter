package com.dokany.java;

import com.dokany.java.constants.FileSystemFeature;
import com.dokany.java.constants.NtStatus;
import com.dokany.java.structure.ByHandleFileInfo;
import com.dokany.java.structure.DokanyFileInfo;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.WIN32_FIND_DATA;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;


public interface DokanyFileSystem {

	/**
	 * CreateFile is called each time a request is made on a file system object.
	 * <p>
	 * If the file is a directory, this method is also called. In this case, the method should return {@link NtStatus#Success} when that directory can be opened and
	 * {@link DokanyFileInfo#_isDirectory} has to be set to <i>true</i>. {@link DokanyFileInfo#_context} can be used to store
	 * data FileStream that can be retrieved in all other request related to the context.
	 *
	 * @param rawPath Path requested by the Kernel on the File System.
	 * @param securityContext ??
	 * @param rawDesiredAccess ?? Permissions for file or directory.
	 * @param rawFileAttributes Provides attributes for files and directories. @see
	 * {@linkplain https://msdn.microsoft.com/en-us/library/system.io.fileattributes(v=vs.110).aspx}
	 * @param rawShareAccess Type of share access to other threads. Device and intermediate drivers usually set ShareAccess to zero, which gives the caller exclusive access to
	 * the open file.
	 * @param rawCreateDisposition
	 * @param rawCreateOptions Represents advanced options for creating a File object. @see
	 * {@linkplain https://msdn.microsoft.com/en-us/library/system.io.fileoptions(v=vs.110).aspx}
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 * @see {@linkplain https://msdn.microsoft.com/en-us/library/windows/hardware/ff566424(v=vs.85).aspx} for more information about the parameters of this callback.
	 */
	long zwCreateFile(
			WString rawPath,
			WinBase.SECURITY_ATTRIBUTES securityContext,
			int rawDesiredAccess,
			int rawFileAttributes,
			int rawShareAccess,
			int rawCreateDisposition,
			int rawCreateOptions,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Receipt of this request indicates that the last handle for a file object that is associated with the target device object has been closed (but, due to outstanding I/O
	 * requests, might not have been released).
	 * <p>
	 * Cleanup is requested before @{link {@link DokanyFileSystem.Close} is called.
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	void cleanup(
			WString rawPath,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * CloseFile is called at the end of the life of the context. Receipt of this request indicates that the last handle of the file object that is associated with the target
	 * device object has been closed and released. All outstanding I/O requests have been completed or canceled.
	 * <p>
	 * CloseFile is requested after {@link DokanyFileSystem.Cleanup} is called. Anything remaining in {@link DokanyFileInfo#_context} has to be cleared
	 * before return.
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	void closeFile(
			WString rawPath,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * ReadFile callback on the file previously opened in {@link DokanyFileSystem.Create}. It can be called by different thread at the same time, therefore the read has to be
	 * thread safe.
	 *
	 * @param rawPath
	 * @param rawBuffer
	 * @param rawBufferLength
	 * @param rawReadLength
	 * @param rawOffset
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long readFile(
			WString rawPath,
			Pointer rawBuffer,
			int rawBufferLength,
			IntByReference rawReadLength,
			long rawOffset,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * WriteFile callback on the file previously opened in {@link DokanyFileSystem.Create} It can be called by different thread at the same time, therefore the write/context has to
	 * be thread safe.
	 *
	 * @param rawPath
	 * @param rawBuffer
	 * @param rawNumberOfBytesToWrite
	 * @param rawNumberOfBytesWritten
	 * @param rawOffset
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long writeFile(
			WString rawPath,
			Pointer rawBuffer,
			int rawNumberOfBytesToWrite,
			IntByReference rawNumberOfBytesWritten,
			long rawOffset,
			DokanyFileInfo dokanyFileInfo);


	/**
	 * Clears buffers for this context and causes any buffered data to be written to the file.
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long flushFileBuffers(
			WString rawPath,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Get specific informations on a file.
	 *
	 * @param fileName
	 * @param handleFileInfo
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long getFileInformation(
			WString fileName,
			ByHandleFileInfo handleFileInfo,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * List all files in the path requested.
	 *
	 * @param rawPath
	 * @param rawFillFindData
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long findFiles(
			WString rawPath,
			DokanyOperations.FillWin32FindData rawFillFindData,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Same as {@link DokanyFileSystem.FindFiles} but with a search pattern to filter the result.
	 *
	 * @param fileName
	 * @param searchPattern
	 * @param rawFillFindData
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long findFilesWithPattern(
			WString fileName,
			WString searchPattern,
			DokanyOperations.FillWin32FindData rawFillFindData,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Set file attributes on a specific file.
	 *
	 * @param rawPath
	 * @param rawAttributes
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long setFileAttributes(
			WString rawPath,
			int rawAttributes,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Set file times on a specific file.
	 *
	 * @param rawPath path to file or directory
	 * @param rawCreationTime time of creation
	 * @param rawLastAccessTime time of last access
	 * @param rawLastWriteTime time of last modification
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long setFileTime(
			WString rawPath,
			FILETIME rawCreationTime,
			FILETIME rawLastAccessTime,
			FILETIME rawLastWriteTime,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Check if it is possible to delete a file.
	 * <p>
	 * You should NOT delete the file in this method, but instead you must only check whether you can delete the file or not, and return {@link NtStatus#Success} (when you can
	 * delete it) or appropriate error codes such as {@link NtStatus#ACCESS_DENIED}, {@link NtStatus#OBJECT_NO_LONGER_EXISTS}, {@link NtStatus#ObjectNameNotFound}.
	 * <p>
	 * {@link DokanyFileSystem.DeleteFile} will also be called with {@link DokanyFileInfo#_deleteOnClose} set to <i>false</i> to notify the driver when the file is no longer
	 * requested to be deleted.
	 * <p>
	 * When you return {@link NtStatus#Success}, you get a {@link DokanyFileSystem.Cleanup}> call afterwards with {@link DokanyFileInfo#_deleteOnClose} set to <i>true</i> and only
	 * then you have to actually delete the file being closed.
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file.
	 * @return {@link NtStatus}
	 * @see {@link DokanyFileSystem.DeleteDirectory}
	 */
	long deleteFile(
			WString rawPath,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Check if it is possible to delete a directory.
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the directory.
	 * @return {@link NtStatus}
	 * @see {@link DokanyFileSystem.DeleteFile} for more specifics.
	 */
	long deleteDirectory(
			WString rawPath,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Move a file or directory to a new location.
	 *
	 * @param rawPath
	 * @param rawNewFileName
	 * @param rawReplaceIfExisting
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long moveFile(
			WString rawPath,
			WString rawNewFileName,
			boolean rawReplaceIfExisting,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * SetEndOfFile is used to truncate or extend a file (physical file size).
	 *
	 * @param rawPath
	 * @param rawByteOffset
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long setEndOfFile(
			WString rawPath,
			long rawByteOffset,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * SetAllocationSize is used to truncate or extend a file.
	 *
	 * @param rawPath
	 * @param rawLength
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long setAllocationSize(
			WString rawPath,
			long rawLength,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Lock file at a specific offset and data length. This is only used if {@link com.dokany.java.constants.MountOption#FILELOCK_USER_MODE} is enabled.
	 *
	 * @param rawPath
	 * @param rawByteOffset
	 * @param rawLength
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long lockFile(
			WString rawPath,
			long rawByteOffset,
			long rawLength,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Unlock file at a specific offset and data length. This is only used if {@link com.dokany.java.constants.MountOption#FILELOCK_USER_MODE} is enabled.
	 *
	 * @param rawPath
	 * @param rawByteOffset
	 * @param rawLength
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long unlockFile(
			WString rawPath,
			long rawByteOffset,
			long rawLength,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Retrieves information about the amount of space that is available on a disk volume, which is the total amount of space, the total amount of free space, and the total amount
	 * of free space available to the user that is associated with the calling thread.
	 * <p>
	 * Neither this method nor {@link DokanyFileSystem.GetVolumeInformation} save the {@link DokanyFileInfo#_context}. Before these methods are called,
	 * {@link DokanyFileSystem.Create} may not be called. (ditto @{link DokanyOperations.CloseFile} and @{link DokanyOperations.Cleanup}).
	 *
	 * @param freeBytesAvailable
	 * @param totalNumberOfBytes
	 * @param totalNumberOfFreeBytes
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long getDiskFreeSpace(
			LongByReference freeBytesAvailable,
			LongByReference totalNumberOfBytes,
			LongByReference totalNumberOfFreeBytes,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Retrieves information about the file system and volume associated with the specified root directory.
	 * <p>
	 * Neither this method nor {@link DokanyFileSystem.GetVolumeInformation} save the {@link DokanyFileInfo#_context}. Before these methods are called,
	 * {@link DokanyFileSystem.Create} may not be called. (ditto @{link DokanyOperations.CloseFile} and @{link DokanyOperations.Cleanup}).
	 *
	 * @param rawVolumeNameBuffer
	 * @param rawVolumeNameSize
	 * @param rawVolumeSerialNumber
	 * @param rawMaximumComponentLength
	 * @param rawFileSystemFlags
	 * @param rawFileSystemNameBuffer
	 * @param rawFileSystemNameSize
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 * @see {@link FileSystemFeature#READ_ONLY_VOLUME} is automatically added to the <paramref name="features"/> if <see cref="DokanOptions.WriteProtection"/> was specified when
	 * the volume was mounted.
	 * <p>
	 * If {@link NtStatus#NotImplemented} is returned, the Dokany kernel driver use following settings by default:
	 *
	 * <ul>
	 * <li>rawVolumeSerialNumber = 0x19831116</li>
	 * <li>rawMaximumComponentLength = 256</li>
	 * <li>rawFileSystemFlags = CaseSensitiveSearch, CasePreservedNames, SupportsRemoteStorage, UnicodeOnDisk</li>
	 * <li>rawFileSystemNameBuffer = NTFS</li>
	 * </ul>
	 */
	long getVolumeInformation(
			Pointer rawVolumeNameBuffer,
			int rawVolumeNameSize,
			IntByReference rawVolumeSerialNumber,
			IntByReference rawMaximumComponentLength,
			IntByReference /* FileSystemFeatures */ rawFileSystemFlags,
			Pointer rawFileSystemNameBuffer,
			int rawFileSystemNameSize,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Is called when Dokany succeeded mounting the volume.
	 */
	long mounted(
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Is called when Dokany succeeded unmounting the volume.
	 */
	long unmounted(
			final DokanyFileInfo dokanyFileInfo);

	/**
	 * Get specified information about the security of a file or directory.
	 * <p>
	 * Supported since version 0.6.0. You must specify the version in {@link com.dokany.java.structure.DeviceOptions#Version}.
	 *
	 * @param rawPath
	 * @param rawSecurityInformation
	 * @param rawSecurityDescriptor
	 * @param rawSecurityDescriptorLength
	 * @param rawSecurityDescriptorLengthNeeded
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long getFileSecurity(
			WString rawPath,
			int /* SecurityInformation */ rawSecurityInformation,
			Pointer rawSecurityDescriptor,
			int rawSecurityDescriptorLength,
			IntByReference rawSecurityDescriptorLengthNeeded,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Sets the security of a file or directory object.
	 * <p>
	 * Supported since version 0.6.0. You must specify the version in {@link com.dokany.java.structure.DeviceOptions#Version}.
	 *
	 * @param rawPath
	 * @param rawSecurityInformation
	 * @param rawSecurityDescriptor
	 * @param rawSecurityDescriptorLength
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long setFileSecurity(
			WString rawPath,
			int rawSecurityInformation,
			// @TODO: This is a pointer??
			Pointer rawSecurityDescriptor,
			int rawSecurityDescriptorLength,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * @param rawFillFindData
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	void fillWin32FindData(
			WIN32_FIND_DATA rawFillFindData,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Retrieve all NTFS Streams informations on the file. This is only called if {@link com.dokany.java.constants.MountOption#ALT_STREAM} is enabled.
	 *
	 * @param rawPath
	 * @param rawFillFindData
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	long findStreams(
			WString rawPath,
			DokanyOperations.FillWin32FindStreamData rawFillFindData,
			DokanyFileInfo dokanyFileInfo);

}
