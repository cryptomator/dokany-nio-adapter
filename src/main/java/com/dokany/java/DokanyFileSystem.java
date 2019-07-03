package com.dokany.java;

import com.dokany.java.constants.DokanOption;
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
	 * CreateFile Dokan API callback.
	 *
	 * CreateFile is called each time a request is made on a file system object.
	 *
	 * In case OPEN_ALWAYS &amp; CREATE_ALWAYS are successfully opening an existing file, STATUS_OBJECT_NAME_COLLISION should be returned instead of STATUS_SUCCESS . This will inform Dokan that the file has been opened and not created during the request.
	 *
	 * If the file is a directory, CreateFile is also called. In this case, CreateFile should return {@link NtStatus#SUCCESS} when that directory can be opened and {@link DokanyFileInfo#IsDirectory} has to be set to TRUE. On the other hand, if {@link DokanyFileInfo#IsDirectory} is set to TRUE but the path targets a file, {@link NtStatus#NOT_A_DIRECTORY} must be returned.
	 *
	 * {@link DokanyFileInfo#Context} can be used to store Data (like a filehandle) that can be retrieved in all other requests related to the Context. To avoid memory leak, Context needs to be released in {@link DokanyFileSystem#cleanup(WString, DokanyFileInfo)} .
	 *
	 * @param rawPath Path requested by the Kernel on the File System.
	 * TODO: rewrite this parameter description to link to winBase
	 * @param securityContext the security context of the kernel (see also in the windows driver API <a href="https://docs.microsoft.com/en-us/windows-hardware/drivers/ddi/content/wdm/ns-wdm-_io_security_context">IO_SECURITY_CONTEXT</a>)
	 * @param rawDesiredAccess Permissions for file or directory. (see also in the windows API <a href="https://docs.microsoft.com/en-us/windows-hardware/drivers/kernel/access-mask">ACCESS_MASK</a>
	 * @param rawFileAttributes Provides attributes for files and directories. (see also in the .NET API <a href="https://docs.microsoft.com/en-us/dotnet/api/system.io.fileattributes">System.IO.FileAttributes</a>}
	 * @param rawShareAccess Type of share access to other threads. Device and intermediate drivers usually set ShareAccess to zero, which gives the caller exclusive access to
	 * the open file.
	 * @param rawCreateDisposition Specifies the action to perform if the file does or does not exist. Can be translated into a readable thing via {@link com.dokany.java.constants.CreationDisposition}
	 * @param rawCreateOptions Specifies the options to apply when the driver creates or opens the file. (see also in the .NET API <a href="https://docs.microsoft.com/de-de/dotnet/api/system.io.fileoptions">System.IO.FileOptions</a>)
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return integer code of a {@link NtStatus}
	 * @see <a href="https://dokan-dev.github.io/dokany-doc/html/struct_d_o_k_a_n___o_p_e_r_a_t_i_o_n_s.html#a40c2f61e1287237f5fd5c2690e795183">Dokany documentation of ZwCreateFile</a>
	 * @see <a href="https://docs.microsoft.com/en-us/windows-hardware/drivers/ddi/content/ntifs/nf-ntifs-ntcreatefile">Microsoft documentation of zwCreateFile</a>
	 * @see <a href="https://msdn.microsoft.com/en-us/library/aa363858%28VS.85%29.aspx">Microsoft FileManagement documentation of CreateFile</a>
	 */
	int zwCreateFile(
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
	 * Cleanup is requested before @{link {@link DokanyFileSystem#closeFile(WString, DokanyFileInfo)} is called.
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
	 * CloseFile is requested after {@link DokanyFileSystem#cleanup(WString, DokanyFileInfo)} is called. Anything remaining in {@link DokanyFileInfo#Context} has to be cleared
	 * before return.
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	void closeFile(
			WString rawPath,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * ReadFile callback on the file previously opened in {@link DokanyFileSystem#zwCreateFile(WString, WinBase.SECURITY_ATTRIBUTES, int, int, int, int, int, DokanyFileInfo)}. It can be called by different thread at the same time, therefore the read has to be
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
	int readFile(
			WString rawPath,
			Pointer rawBuffer,
			int rawBufferLength,
			IntByReference rawReadLength,
			long rawOffset,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * WriteFile callback on the file previously opened in {@link DokanyFileSystem#zwCreateFile(WString, WinBase.SECURITY_ATTRIBUTES, int, int, int, int, int, DokanyFileInfo)} It can be called by different thread at the same time, therefore the write/context has to
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
	int writeFile(
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
	int flushFileBuffers(
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
	int getFileInformation(
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
	int findFiles(
			WString rawPath,
			DokanyOperations.FillWin32FindData rawFillFindData,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Same as {@link DokanyFileSystem#findFiles(WString, DokanyOperations.FillWin32FindData, DokanyFileInfo)} but with a search pattern to filter the result.
	 *
	 * @param fileName
	 * @param searchPattern
	 * @param rawFillFindData
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	int findFilesWithPattern(
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
	int setFileAttributes(
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
	int setFileTime(
			WString rawPath,
			FILETIME rawCreationTime,
			FILETIME rawLastAccessTime,
			FILETIME rawLastWriteTime,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Check if it is possible to delete a file.
	 * <p>
	 * You should NOT delete the file in this method, but instead you must only check whether you can delete the file or not, and return {@link NtStatus#SUCCESS} (when you can
	 * delete it) or appropriate error codes such as {@link NtStatus#ACCESS_DENIED}, {@link NtStatus#OBJECT_NO_LONGER_EXISTS}, {@link NtStatus#OBJECT_NAME_NOT_FOUND}.
	 * <p>
	 * {@link DokanyFileSystem#deleteFile(WString, DokanyFileInfo)} will also be called with {@link DokanyFileInfo#DeleteOnClose} set to <i>false</i> to notify the driver when the file is no longer
	 * requested to be deleted.
	 * <p>
	 * When you return {@link NtStatus#SUCCESS}, you get a {@link DokanyFileSystem#cleanup(WString, DokanyFileInfo)} call afterwards with {@link DokanyFileInfo#DeleteOnClose} set to <i>true</i> and only
	 * then you have to actually delete the file being closed.
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file.
	 * @return {@link NtStatus}
	 * @see #deleteDirectory(WString, DokanyFileInfo)
	 */
	int deleteFile(
			WString rawPath,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Check if it is possible to delete a directory.
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the directory.
	 * @return {@link NtStatus}
	 * @see #deleteFile(WString, DokanyFileInfo)
	 */
	int deleteDirectory(
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
	int moveFile(
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
	int setEndOfFile(
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
	int setAllocationSize(
			WString rawPath,
			long rawLength,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Lock file at a specific offset and data length. This is only used if {@link DokanOption#FILELOCK_USER_MODE} is enabled.
	 *
	 * @param rawPath
	 * @param rawByteOffset
	 * @param rawLength
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	int lockFile(
			WString rawPath,
			long rawByteOffset,
			long rawLength,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Unlock file at a specific offset and data length. This is only used if {@link DokanOption#FILELOCK_USER_MODE} is enabled.
	 *
	 * @param rawPath
	 * @param rawByteOffset
	 * @param rawLength
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	int unlockFile(
			WString rawPath,
			long rawByteOffset,
			long rawLength,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Retrieves information about the amount of space that is available on a disk volume, which is the total amount of space, the total amount of free space, and the total amount
	 * of free space available to the user that is associated with the calling thread.
	 * <p>
	 * Neither this method nor {@link DokanyFileSystem#getVolumeInformation(Pointer, int, IntByReference, IntByReference, IntByReference, Pointer, int, DokanyFileInfo)} save the {@link DokanyFileInfo#Context}. Before these methods are called,
	 * {@link DokanyFileSystem#zwCreateFile(WString, WinBase.SECURITY_ATTRIBUTES, int, int, int, int, int, DokanyFileInfo)} may not be called. (ditto @{link DokanyOperations.CloseFile} and @{link DokanyOperations.Cleanup}).
	 *
	 * @param freeBytesAvailable
	 * @param totalNumberOfBytes
	 * @param totalNumberOfFreeBytes
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	int getDiskFreeSpace(
			LongByReference freeBytesAvailable,
			LongByReference totalNumberOfBytes,
			LongByReference totalNumberOfFreeBytes,
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Retrieves information about the file system and volume associated with the specified root directory.
	 * <p>
	 * Neither this method nor {@link DokanyFileSystem#getVolumeInformation(Pointer, int, IntByReference, IntByReference, IntByReference, Pointer, int, DokanyFileInfo)} save the {@link DokanyFileInfo#Context}. Before these methods are called,
	 * {@link DokanyFileSystem#zwCreateFile(WString, WinBase.SECURITY_ATTRIBUTES, int, int, int, int, int, DokanyFileInfo)} may not be called. (ditto @{link DokanyOperations.CloseFile} and @{link DokanyOperations.Cleanup}).
	 *
	 * {@link FileSystemFeature#READ_ONLY_VOLUME} is automatically added to the features if {@link DokanOption#WRITE_PROTECTION} was specified during mount.
	 * <p>
	 * If {@link NtStatus#NOT_IMPLEMENTED} is returned, the Dokany kernel driver use following settings by default:
	 *
	 * <ul>
	 * <li>rawVolumeSerialNumber = 0x19831116</li>
	 * <li>rawMaximumComponentLength = 256</li>
	 * <li>rawFileSystemFlags = CaseSensitiveSearch, CasePreservedNames, SupportsRemoteStorage, UnicodeOnDisk</li>
	 * <li>rawFileSystemNameBuffer = NTFS</li>
	 * </ul>
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
	 */
	int getVolumeInformation(
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
	int mounted(
			DokanyFileInfo dokanyFileInfo);

	/**
	 * Is called when Dokany succeeded unmounting the volume.
	 */
	int unmounted(
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
	int getFileSecurity(
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
	int setFileSecurity(
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
	 * Retrieve all NTFS Streams informations on the file. This is only called if {@link DokanOption#ALT_STREAM} is enabled.
	 *
	 * @param rawPath
	 * @param rawFillFindData
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 * @return {@link NtStatus}
	 */
	int findStreams(
			WString rawPath,
			DokanyOperations.FillWin32FindStreamData rawFillFindData,
			DokanyFileInfo dokanyFileInfo);

}
