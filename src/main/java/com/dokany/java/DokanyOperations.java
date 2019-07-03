package com.dokany.java;

import java.util.Arrays;
import java.util.List;

import com.dokany.java.constants.DokanOption;
import com.dokany.java.constants.FileSystemFeature;
import com.dokany.java.constants.NtStatus;
import com.dokany.java.structure.ByHandleFileInfo;
import com.dokany.java.structure.DokanyFileInfo;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.WIN32_FIND_DATA;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;


/**
 *
 * Dokany API callbacks interface. This is an internal class and should not used directly by code outside com.dokany.java.
 *
 * A struct of callbacks that describe all Dokany API operation that will be called when Windows accesses the file system.
 *
 * If an error occurs, return {@link NtStatus}.
 *
 * All these callbacks can be set to <i>null</i> or return {@link NtStatus#NOT_IMPLEMENTED} if you don't want to support one of them. Be aware that returning such a value to
 * important callbacks such as {@link DokanyOperations.ZwCreateFile} or {@link DokanyOperations.ReadFile} would make the file system not working or unstable.
 *
 * This is the same struct as <i>_DOKAN_OPERATIONS</i> (dokan.h) in the C++ version of Dokany.</remarks>
 *
 */
public class DokanyOperations extends Structure {

	public DokanyOperations() {
	}

	// @Override
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(
		        "ZwCreateFile",
		        "Cleanup",
		        "CloseFile",
		        "ReadFile",
		        "WriteFile",
		        "FlushFileBuffers",
		        "GetFileInformation",
		        "FindFiles",
		        "FindFilesWithPattern",
		        "SetFileAttributes",
		        "SetFileTime",
		        "DeleteFile",
		        "DeleteDirectory",
		        "MoveFile",
		        "SetEndOfFile",
		        "SetAllocationSize",
		        "LockFile",
		        "UnlockFile",
		        "GetDiskFreeSpace",
		        "GetVolumeInformation",
		        "Mounted",
		        "Unmounted",
		        "GetFileSecurity",
		        "SetFileSecurity",
		        "FindStreams");
	}

	public ZwCreateFile ZwCreateFile;
	public Cleanup Cleanup;
	public CloseFile CloseFile;
	public ReadFile ReadFile;
	public WriteFile WriteFile;
	public FlushFileBuffers FlushFileBuffers;
	public GetFileInformation GetFileInformation;
	public FindFiles FindFiles;
	public FindFilesWithPattern FindFilesWithPattern;
	public SetFileAttributes SetFileAttributes;
	public SetFileTime SetFileTime;
	public DeleteFile DeleteFile;
	public DeleteDirectory DeleteDirectory;
	public MoveFile MoveFile;
	public SetEndOfFile SetEndOfFile;
	public SetAllocationSize SetAllocationSize;
	public LockFile LockFile;
	public UnlockFile UnlockFile;
	public GetDiskFreeSpace GetDiskFreeSpace;
	public GetVolumeInformation GetVolumeInformation;
	public Mounted Mounted;
	public Unmounted Unmounted;
	public GetFileSecurity GetFileSecurity;
	public SetFileSecurity SetFileSecurity;
	public FindStreams FindStreams;

	/**
	 * CreateFile is called each time a request is made on a file system object.
	 *
	 * If the file is a directory, this method is also called. In this case, the method should return {@link NtStatus#SUCCESS} when that directory can be opened and
	 * {@link com.dokany.java.structure.DokanyFileInfo#IsDirectory} has to be set to <i>true</i>. {@link com.dokany.java.structure.DokanyFileInfo#Context} can be used to store
	 * data FileStream that can be retrieved in all other request related to the context.
	 *
	 * @see <a href="https://msdn.microsoft.com/en-us/library/windows/hardware/ff566424(v=vs.85).aspx">MSDN for more information about the parameters of this callback.</a>
	 */
	@FunctionalInterface
	interface ZwCreateFile extends Callback {
		/**
		 * @param rawPath Path requested by the Kernel on the File System.
		 * @param securityContext ??
		 * @param rawDesiredAccess ?? Permissions for file or directory.
		 * @param rawFileAttributes Provides attributes for files and directories. See <a href="https://msdn.microsoft.com/en-us/library/system.io.fileattributes(v=vs.110).aspx">MSDN</a>
		 * @param rawShareAccess Type of share access to other threads. Device and intermediate drivers usually set ShareAccess to zero, which gives the caller exclusive access to
		 *            the open file.
		 * @param rawCreateDisposition
		 * @param rawCreateOptions Represents advanced options for creating a File object. See <a href="https://msdn.microsoft.com/en-us/library/system.io.fileoptions(v=vs.110).aspx">MSDN</a>
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          WinBase.SECURITY_ATTRIBUTES securityContext,
		        int rawDesiredAccess,
		        int rawFileAttributes,
		        int rawShareAccess,
		        int rawCreateDisposition,
		        int rawCreateOptions,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 * Receipt of this request indicates that the last handle for a file object that is associated with the target device object has been closed (but, due to outstanding I/O
	 * requests, might not have been released).
	 *
	 * Cleanup is requested before @{link {@link DokanyOperations#CloseFile} is called.
	 *
	 */
	@FunctionalInterface
	interface Cleanup extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 */
		void callback(
		          WString rawPath,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 * CloseFile is called at the end of the life of the context. Receipt of this request indicates that the last handle of the file object that is associated with the target
	 * device object has been closed and released. All outstanding I/O requests have been completed or canceled.
	 *
	 * CloseFile is requested after {@link DokanyOperations.Cleanup} is called. Anything remaining in {@link com.dokany.java.structure.DokanyFileInfo#Context} has to be cleared
	 * before return.
	 *
	 */
	@FunctionalInterface
	interface CloseFile extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 */
		void callback(
		          WString rawPath,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 * ReadFile callback on the file previously opened in {@link DokanyOperations.ZwCreateFile}. It can be called by different thread at the same time, therefore the read has to be
	 * thread safe.
	 *
	 */
	interface ReadFile extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawBuffer
		 * @param rawBufferLength
		 * @param rawReadLength
		 * @param rawOffset
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          Pointer rawBuffer,
		        int rawBufferLength,
		          IntByReference rawReadLength,
		        long rawOffset,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 * WriteFile callback on the file previously opened in {@link DokanyOperations.ZwCreateFile} It can be called by different thread at the same time, therefore the write/context has to
	 * be thread safe.
	 *
	 */
	@FunctionalInterface
	interface WriteFile extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawBuffer
		 * @param rawNumberOfBytesToWrite
		 * @param rawNumberOfBytesWritten
		 * @param rawOffset
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          Pointer rawBuffer,
		        int rawNumberOfBytesToWrite,
		          IntByReference rawNumberOfBytesWritten,
		        long rawOffset,
		          DokanyFileInfo dokanyFileInfo);

	}

	/**
	 * Clears buffers for this context and causes any buffered data to be written to the file.
	 *
	 */
	@FunctionalInterface
	interface FlushFileBuffers extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param dokanyFileInfo {@link com.dokany.java.structure.DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Get specific informations on a file.
	 *
	 */
	@FunctionalInterface
	interface GetFileInformation extends Callback {
		/**
		 *
		 * @param fileName
		 * @param handleFileInfo
		 * @param dokanyFileInfo {@link com.dokany.java.structure.DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString fileName,
		          ByHandleFileInfo handleFileInfo,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * List all files in the path requested.
	 */
	@FunctionalInterface
	interface FindFiles extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawFillFindData
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          FillWin32FindData rawFillFindData,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Same as {@link DokanyOperations.FindFiles} but with a search pattern to filter the result.
	 *
	 */
	@FunctionalInterface
	interface FindFilesWithPattern extends Callback {
		/**
		 *
		 * @param fileName
		 * @param searchPattern
		 * @param rawFillFindData
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString fileName,
		          WString searchPattern,
		          FillWin32FindData rawFillFindData,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Set file attributes on a specific file.
	 */
	@FunctionalInterface
	interface SetFileAttributes extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawAttributes
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		        int rawAttributes,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Set file times on a specific file.
	 */
	@FunctionalInterface
	interface SetFileTime extends Callback {
		/**
		 *
		 * @param rawPath path to file or directory
		 * @param rawCreationTime time of creation
		 * @param rawLastAccessTime time of last access
		 * @param rawLastWriteTime time of last modification
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          FILETIME rawCreationTime,
		          FILETIME rawLastAccessTime,
		          FILETIME rawLastWriteTime,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Check if it is possible to delete a file.
	 *
	 * You should NOT delete the file in this method, but instead you must only check whether you can delete the file or not, and return {@link NtStatus#SUCCESS} (when you can
	 * delete it) or appropriate error codes such as {@link NtStatus#ACCESS_DENIED}, {@link NtStatus#OBJECT_NO_LONGER_EXISTS}, {@link NtStatus#OBJECT_NAME_NOT_FOUND}.
	 *
	 * {@link DokanyOperations.DeleteFile} will also be called with {@link DokanyFileInfo#DeleteOnClose} set to <i>false</i> to notify the driver when the file is no longer
	 * requested to be deleted.
	 *
	 * When you return {@link NtStatus#SUCCESS}, you get a {@link DokanyOperations.Cleanup}> call afterwards with {@link DokanyFileInfo#DeleteOnClose} set to <i>true</i> and only
	 * then you have to actually delete the file being closed.
	 *
	 * @see {@link DokanyOperations.DeleteDirectory}
	 */
	@FunctionalInterface
	interface DeleteFile extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Check if it is possible to delete a directory.
	 *
	 * @see {@link DokanyOperations.DeleteFile} for more specifics.
	 *
	 */
	@FunctionalInterface
	interface DeleteDirectory extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Move a file or directory to a new location.
	 */
	@FunctionalInterface
	interface MoveFile extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawNewFileName
		 * @param rawReplaceIfExisting
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          WString rawNewFileName,
		        boolean rawReplaceIfExisting,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * SetEndOfFile is used to truncate or extend a file (physical file size).
	 */
	@FunctionalInterface
	interface SetEndOfFile extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawByteOffset
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		        long rawByteOffset,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * SetAllocationSize is used to truncate or extend a file.
	 */
	@FunctionalInterface
	interface SetAllocationSize extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawLength
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		        long rawLength,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Lock file at a specific offset and data length. This is only used if {@link DokanOption#FILELOCK_USER_MODE} is enabled.
	 */
	@FunctionalInterface
	interface LockFile extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawByteOffset
		 * @param rawLength
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		        long rawByteOffset,
		        long rawLength,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Unlock file at a specific offset and data length. This is only used if {@link DokanOption#FILELOCK_USER_MODE} is enabled.
	 */
	@FunctionalInterface
	interface UnlockFile extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawByteOffset
		 * @param rawLength
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		        long rawByteOffset,
		        long rawLength,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Retrieves information about the amount of space that is available on a disk volume, which is the total amount of space, the total amount of free space, and the total amount
	 * of free space available to the user that is associated with the calling thread.
	 *
	 * Neither this method nor {@link DokanyOperations.GetVolumeInformation} save the {@link com.dokany.java.structure.DokanyFileInfo#Context}. Before these methods are called,
	 * {@link DokanyOperations.ZwCreateFile} may not be called. (ditto @{link DokanyOperations.CloseFile} and @{link DokanyOperations.Cleanup}).
	 *
	 */
	@FunctionalInterface
	interface GetDiskFreeSpace extends Callback {
		/**
		 *
		 * @param freeBytesAvailable
		 * @param totalNumberOfBytes
		 * @param totalNumberOfFreeBytes
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          LongByReference freeBytesAvailable,
		          LongByReference totalNumberOfBytes,
		          LongByReference totalNumberOfFreeBytes,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Retrieves information about the file system and volume associated with the specified root directory.
	 *
	 * Neither this method nor {@link DokanyOperations.GetVolumeInformation} save the {@link com.dokany.java.structure.DokanyFileInfo#Context}. Before these methods are called,
	 * {@link DokanyOperations.ZwCreateFile} may not be called. (ditto @{link DokanyOperations.CloseFile} and @{link DokanyOperations.Cleanup}).
	 *
	 * @see {@link FileSystemFeature#READ_ONLY_VOLUME} is automatically added to the <paramref name="features"/> if <see cref="DokanOptions.WriteProtection"/> was specified when
	 *      the volume was mounted.
	 *
	 *      If {@link NtStatus#NOT_IMPLEMENTED} is returned, the Dokany kernel driver use following settings by default:
	 *
	 *      <ul>
	 *      <li>rawVolumeSerialNumber = 0x19831116</li>
	 *      <li>rawMaximumComponentLength = 256</li>
	 *      <li>rawFileSystemFlags = CaseSensitiveSearch, CasePreservedNames, SupportsRemoteStorage, UnicodeOnDisk</li>
	 *      <li>rawFileSystemNameBuffer = NTFS</li>
	 *      </ul>
	 */
	@FunctionalInterface
	interface GetVolumeInformation extends Callback {
		/**
		 *
		 * @param rawVolumeNameBuffer
		 * @param rawVolumeNameSize
		 * @param rawVolumeSerialNumber
		 * @param rawMaximumComponentLength
		 * @param rawFileSystemFlags
		 * @param rawFileSystemNameBuffer
		 * @param rawFileSystemNameSize
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          Pointer rawVolumeNameBuffer,
		        int rawVolumeNameSize,
		          IntByReference rawVolumeSerialNumber,
		          IntByReference rawMaximumComponentLength,
		          IntByReference /* FileSystemFeatures */ rawFileSystemFlags,
		          Pointer rawFileSystemNameBuffer,
		        int rawFileSystemNameSize,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Is called when Dokany succeeded mounting the volume.
	 */
	@FunctionalInterface
	interface Mounted extends Callback {
		long mounted(
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Is called when Dokany succeeded unmounting the volume.
	 */
	@FunctionalInterface
	interface Unmounted extends Callback {
		long unmounted(
		          final DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Get specified information about the security of a file or directory.
	 *
	 * Supported since version 0.6.0. You must specify the version in {@link com.dokany.java.structure.DeviceOptions#Version}.
	 */
	@FunctionalInterface
	interface GetFileSecurity extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawSecurityInformation
		 * @param rawSecurityDescriptor
		 * @param rawSecurityDescriptorLength
		 * @param rawSecurityDescriptorLengthNeeded
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		        int /* SecurityInformation */ rawSecurityInformation,
		          Pointer rawSecurityDescriptor,
		        int rawSecurityDescriptorLength,
		          IntByReference rawSecurityDescriptorLengthNeeded,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Sets the security of a file or directory object.
	 *
	 * Supported since version 0.6.0. You must specify the version in {@link com.dokany.java.structure.DeviceOptions#Version}.
	 */
	@FunctionalInterface
	interface SetFileSecurity extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawSecurityInformation
		 * @param rawSecurityDescriptor
		 * @param rawSecurityDescriptorLength
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		        int rawSecurityInformation,
		        // @TODO: This is a pointer??
		          Pointer rawSecurityDescriptor,
		        int rawSecurityDescriptorLength,
		          DokanyFileInfo dokanyFileInfo);
	}

	@FunctionalInterface
	public interface FillWin32FindData extends Callback {
		/**
		 *
		 * @param rawFillFindData
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 */
		void fillWin32FindData(
		          WIN32_FIND_DATA rawFillFindData,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 * Retrieve all NTFS Streams informations on the file. This is only called if {@link DokanOption#ALT_STREAM} is enabled.
	 */
	@FunctionalInterface
	interface FindStreams extends Callback {
		/**
		 *
		 * @param rawPath
		 * @param rawFillFindData
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 * @return {@link com.dokany.java.constants.NtStatus}
		 */
		long callback(
		          WString rawPath,
		          FillWin32FindStreamData rawFillFindData,
		          DokanyFileInfo dokanyFileInfo);
	}

	/**
	 *
	 *
	 */
	@FunctionalInterface
	public interface FillWin32FindStreamData extends Callback {
		/**
		 *
		 * @param rawFillFindData
		 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
		 */
		void callback(
		          Win32FindStreamData rawFillFindData,
		          DokanyFileInfo dokanyFileInfo);
	}

	interface Win32FindStreamDataInterface {
		public void length(long val);

		public char[] cFileName();
	}
}
