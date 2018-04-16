package com.dokany.java.legacy;

import com.dokany.java.constants.FileAttribute;
import com.dokany.java.constants.MountOption;
import com.dokany.java.structure.*;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinBase.WIN32_FIND_DATA;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

/**
 * This should be extended by file system providers.
 */
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class DokanyFileSystem_OLD {


	VolumeInformation volumeInfo;

	FreeSpace freeSpace;
	long allocationUnitSize;
	long sectorSize;
	long timeout;

	Date rootCreationDate;

	String root;
	boolean isDebug;
	boolean isDebugStdErr;

	public DokanyFileSystem_OLD(
	          final DeviceOptions deviceOptions,
	          final VolumeInformation volumeInfo,
	          final FreeSpace freeSpace,
	          final Date rootCreationDate,
	          final String rootPath) {
		this.volumeInfo = volumeInfo;

		this.freeSpace = freeSpace;

		timeout = deviceOptions.Timeout;
		allocationUnitSize = deviceOptions.AllocationUnitSize;
		sectorSize = deviceOptions.SectorSize;
		this.rootCreationDate = rootCreationDate;

		root = DokanyUtils.normalize(rootPath);
		isDebug = deviceOptions.getMountOptions().contains(MountOption.DEBUG_MODE);
		isDebugStdErr = deviceOptions.getMountOptions().contains(MountOption.STD_ERR_OUTPUT);
	}

	public abstract void mounted() throws IOException;

	public abstract void unmounted() throws IOException;

	/**
	 * Used  currently nowhere used
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public abstract boolean doesPathExist(  final String path) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.FindFilesWithPattern} and indirect in {@link DokanyOperationsProxy.FindFiles}
	 * @param pathToSearch
	 * @param dokanyFileInfo
	 * @param pattern
	 * @return
	 * @throws IOException
	 */
	public abstract Set<WIN32_FIND_DATA> findFilesWithPattern(  final String pathToSearch,   DokanyFileInfo dokanyFileInfo, final String pattern) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.FindStreams}
	 * @param pathToSearch
	 * @return
	 * @throws IOException
	 */
	public abstract Set<Win32FindStreamData> findStreams(  final String pathToSearch) throws IOException;

	/**
	 * Only used if dokan option UserModeLock is enabled
	 * Used in {@link DokanyOperationsProxy.UnlockFile}
	 */
	public abstract void unlock(  final String path, final int offset, final int length) throws IOException;

	/**
	 * Only used if dokan option UserModeLock is enabled
	 * Used in {@link DokanyOperationsProxy.LockFile}
	 */
	public abstract void lock(  final String path, final int offset, final int length) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.MoveFile}
	 * @param oldPath
	 * @param newPath
	 * @param replaceIfExisting
	 * @throws IOException
	 */
	public abstract void move(  final String oldPath,   final String newPath, final boolean replaceIfExisting) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.DeleteFile}
	 * @param path
	 * @param dokanyFileInfo
	 * @throws IOException
	 */
	public abstract void deleteFile(  final String path,   final DokanyFileInfo dokanyFileInfo) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.DeleteDirectory}
	 * @param path
	 * @param dokanyFileInfo
	 * @throws IOException
	 */
	public abstract void deleteDirectory(  final String path,   final DokanyFileInfo dokanyFileInfo) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.ReadFile}
	 * @param path
	 * @param offset
	 * @param readLength
	 * @return
	 * @throws IOException
	 */
	public abstract FileData read(  final String path, final long offset, final int readLength) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.WriteFile}
	 * @param path
	 * @param offset
	 * @param data
	 * @param writeLength
	 * @return
	 * @throws IOException
	 */
	public abstract int write(  final String path, final int offset, final byte[] data, final int writeLength) throws IOException;


	/**
	 * Used NOWHERE
	 * TODO: Add SecurityContext and ShareAccess and DesiredAccess
	 * @param path
	 * @param options
	 * @param attributes
	 * @throws IOException
	 */
	public abstract void createEmptyFile(final String path, long options, final EnumIntegerSet<FileAttribute> attributes) throws IOException;


	/**
	 * Used NOWHERE
	 * TODO: Add SecurityContext and ShareAccess and DesiredAccess
	 * @param path
	 * @param options
	 * @param attributes
	 * @throws IOException
	 */
	public abstract void createEmptyDirectory(  final String path, final long options,   final EnumIntegerSet<FileAttribute> attributes) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.FlushFileBuffers}
	 * @param path
	 * @throws IOException
	 */
	public abstract void flushFileBuffers(  final String path) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.Cleanup}
	 * @param path
	 * @param dokanyFileInfo
	 * @throws IOException
	 */
	public abstract void cleanup(  final String path,   final DokanyFileInfo dokanyFileInfo) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.CloseFile}
	 * @param path
	 * @param dokanyFileInfo
	 * @throws IOException
	 */
	public abstract void close(  final String path,   final DokanyFileInfo dokanyFileInfo) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.GetFileSecurity}
	 * @param path
	 * @param kind
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public abstract int getSecurity(  final String path, final int kind,   final byte[] out) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.SetFileSecurity}
	 * @param path
	 * @param kind
	 * @param data
	 * @throws IOException
	 */
	public abstract void setSecurity(  final String path, final int kind,   final byte[] data) throws IOException;

	/**
	 * Used nowhere
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public abstract long truncate(  final String path) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.SetAllocationSize}
	 * @param path
	 * @param length
	 * @throws IOException
	 */
	public abstract void setAllocationSize(  final String path, final int length) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.SetEndOfFile}
	 * @param path
	 * @param offset
	 * @throws IOException
	 */
	public abstract void setEndOfFile(  final String path, final int offset) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.SetFileAttributes}
	 * @param path
	 * @param attributes
	 * @throws IOException
	 */
	public abstract void setAttributes(  final String path,   final EnumIntegerSet<FileAttribute> attributes) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.GetFileInformation}
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public abstract FullFileInfo getInfo(  final String path) throws IOException;

	/**
	 * Used in {@link DokanyOperationsProxy.SetFileTime}
	 * @param path
	 * @param creation
	 * @param lastAccess
	 * @param lastModification
	 * @throws IOException
	 */
	public abstract void setTime(  final String path,   final FILETIME creation,   final FILETIME lastAccess,   final FILETIME lastModification) throws IOException;
}