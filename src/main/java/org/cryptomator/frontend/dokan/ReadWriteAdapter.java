package org.cryptomator.frontend.dokan;

import com.dokany.java.constants.ErrorCode;
import com.dokany.java.constants.NtStatus;
import com.dokany.java.structure.DokanyFileInfo;
import com.dokany.java.structure.FreeSpace;
import com.dokany.java.structure.VolumeInformation;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadWriteAdapter extends ReadOnlyAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAdapter.class);

	public ReadWriteAdapter(Path root, VolumeInformation volumeInformation, FreeSpace freeSpace) {
		super(root, volumeInformation, freeSpace);
	}

	/**
	 * The fileHandle is already closed here, due to the requirements of the dokany implementation to delete a file in the cleanUp method
	 *
	 * @param rawPath
	 * @param dokanyFileInfo {@link DokanyFileInfo} with information about the file or directory.
	 */
	@Override
	public void cleanup(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("cleanup() is called for: " + getRootedPath(rawPath).toString());
		try {
			if (dokanyFileInfo.Context != 0) {
				fac.close(dokanyFileInfo.Context);
			}
			if (dokanyFileInfo.deleteOnClose()) {
				try {
					Files.delete(getRootedPath(rawPath));
				} catch (IOException e) {
					LOG.warn("Unable to delete File: ", e);
				}
			}
		} catch (IOException e) {
			LOG.warn("Unable to close FileHandle: ", e);
		}

	}

	@Override
	public long writeFile(WString rawPath, Pointer rawBuffer, int rawNumberOfBytesToWrite, IntByReference rawNumberOfBytesWritten, long rawOffset, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("writeFile() is called for " + getRootedPath(rawPath).toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to read file " + getRootedPath(rawPath).toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			try {
				rawNumberOfBytesWritten.setValue(fac.get(dokanyFileInfo.Context).write(rawBuffer, rawNumberOfBytesToWrite, rawOffset));
			} catch (IOException e) {
				LOG.error("Error while reading file: ", e);
				return ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
			return ErrorCode.SUCCESS.getMask();
		}
	}

	/**
	 * Currently ignores other open file handles for this file
	 * TODO: flush only current fileHandle in context or every write operation?
	 *
	 * @param rawPath
	 * @param dokanyFileInfo
	 * @return
	 */
	@Override
	public long flushFileBuffers(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("flushFileBuffers() is called for " + getRootedPath(rawPath).toString());

		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to flush file " + getRootedPath(rawPath).toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			try {
				fac.get(dokanyFileInfo.Context).flush();
			} catch (IOException e) {
				LOG.error("Error while reading file: ", e);
				return ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
			return ErrorCode.SUCCESS.getMask();
		}
	}

	/**
	 * TODO: make the error distinciton more fine grained!
	 *
	 * @param rawPath
	 * @param dokanyFileInfo
	 * @return
	 */
	@Override
	public long deleteFile(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("deleteFile() is called for " + path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to call deleteFile() on " + path.toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			if (fac.get(dokanyFileInfo.Context).canBeDeleted()) {
				return ErrorCode.SUCCESS.getMask();
			} else {
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	/**
	 * TODO: do we have to check if the path points to a directory?
	 *
	 * @param rawPath
	 * @param dokanyFileInfo
	 * @return
	 */
	@Override
	public long deleteDirectory(WString rawPath, DokanyFileInfo dokanyFileInfo) {
		Path path = getRootedPath(rawPath);
		LOG.trace("deleteDirectory() is called for " + path.toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to call deleteDirectory() on " + path.toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			if (fac.get(dokanyFileInfo.Context).canBeDeleted()) {
				try (DirectoryStream emptyCheck = Files.newDirectoryStream(path)) {
					if (!emptyCheck.iterator().hasNext()) {
						return ErrorCode.SUCCESS.getMask();
					} else {
						return NtStatus.DIRECTORY_NOT_EMPTY.getMask();
					}

				} catch (IOException e) {
					return NtStatus.UNSUCCESSFUL.getMask();
				}
			} else {
				return NtStatus.UNSUCCESSFUL.getMask();
			}
		}
	}

	@Override
	public long moveFile(WString rawPath, WString rawNewFileName, boolean rawReplaceIfExisting, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long setEndOfFile(WString rawPath, long rawByteOffset, DokanyFileInfo dokanyFileInfo) {
		return NtStatus.UNSUCCESSFUL.getMask();
	}

	@Override
	public long setAllocationSize(WString rawPath, long rawLength, DokanyFileInfo dokanyFileInfo) {
		LOG.trace("setAllocationSize() is called for " + getRootedPath(rawPath).toString());
		if (dokanyFileInfo.Context == 0) {
			LOG.warn("Attempt to call setAllocationSize() on file " + getRootedPath(rawPath).toString() + " with invalid handle");
			return NtStatus.UNSUCCESSFUL.getMask();
		} else {
			try {
				fac.get(dokanyFileInfo.Context).truncate(rawLength);
			} catch (IOException e) {
				LOG.error("Error while reading file: ", e);
				return ErrorCode.ERROR_WRITE_FAULT.getMask();
			}
			return ErrorCode.SUCCESS.getMask();
		}
	}

}
