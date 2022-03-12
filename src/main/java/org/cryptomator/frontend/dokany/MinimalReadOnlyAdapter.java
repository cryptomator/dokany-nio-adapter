package org.cryptomator.frontend.dokany;

import com.dokany.java.next.DokanFileSystem;
import com.dokany.java.next.constants.CreateOptions;
import com.dokany.java.next.nativeannotations.Enum;
import com.dokany.java.next.nativeannotations.EnumSet;
import com.dokany.java.next.structures.DokanFileInfo;
import com.dokany.java.next.structures.DokanIOSecurityContext;
import com.google.common.base.CharMatcher;
import com.sun.jna.WString;
import org.cryptomator.frontend.dokany.locks.DataLock;
import org.cryptomator.frontend.dokany.locks.LockManager;
import org.cryptomator.frontend.dokany.locks.PathLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributes;

public class MinimalReadOnlyAdapter implements DokanFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(MinimalReadOnlyAdapter.class);
	private static final WString MATCH_ALL_PATTERN = new WString("*");
	private static final char[] EMPTY_ALT_FILE_NAME = new char[]{'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};

	private final Path root;
	private final LockManager lockManager;
	private final OpenHandleFactory fac;
	private final FileStore fileStore;

	public MinimalReadOnlyAdapter(Path root, LockManager lockManager) {
		this.root = root;
		this.lockManager = lockManager;
		this.fac = new OpenHandleFactory();
		try {
			this.fileStore = Files.getFileStore(root);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public int zwCreateFile(WString file, DokanIOSecurityContext securityContext, @EnumSet int desiredAccess, @EnumSet int fileAttributes, @EnumSet int shareAccess, @Enum int createDisposition, @EnumSet int createOptions, DokanFileInfo dokanFileInfo) {
		LOG.info("zwCreateFileCalled for {} ", file);
		Path actualFile;
		try {
			actualFile = getRootedPath(file);
		} catch (InvalidPathException e) {
			return NTStatus.OBJECT_NAME_INVALID;
		}

		//TODO: too restrictive
		/*
		if (containsAny(desiredAccess, WinNT.FILE_WRITE_DATA, WinNT.FILE_WRITE_ATTRIBUTES, WinNT.FILE_WRITE_EA, WinNT.FILE_GENERIC_WRITE, WinNT.FILE_APPEND_DATA, WinNT.GENERIC_ALL, WinNT.GENERIC_WRITE) || !(isAny(createDisposition, CreateDisposition.FILE_OPEN, CreateDisposition.FILE_OPEN_IF)) || containsAny(createOptions, CreateOptions.FILE_DELETE_ON_CLOSE)) {
			return NTStatus.STATUS_ACCESS_DENIED;
		}
		 */
		try (PathLock pathLock = lockManager.createPathLock(file.toString()).forWriting(); DataLock dataLock = pathLock.lockDataForWriting()) {
			DosFileAttributes attr;
			try {
				attr = Files.readAttributes(actualFile, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			} catch (NoSuchFileException e) {
				return NTStatus.NO_SUCH_FILE;
			} catch (IOException e) {
				return NTStatus.IO_DEVICE_ERROR;
			}

			if (attr.isDirectory()) {
				if (containsAny(createOptions, CreateOptions.FILE_NON_DIRECTORY_FILE)) {
					return NTStatus.FILE_IS_A_DIRECTORY;
				}
				dokanFileInfo.isDirectory = 0x01;
			} else {
				if (containsAny(createOptions, CreateOptions.FILE_DIRECTORY_FILE)) {
					return NTStatus.NOT_A_DIRECTORY;
				}
				dokanFileInfo.isDirectory = 0x00;
			}

			if (dokanFileInfo.getIsDirectory()) {
				//do we have the right set of options?
				LOG.info("{} is a directory.", file);

				//open a handle to the dir
				try {
					dokanFileInfo.context = fac.openDir(actualFile);
					LOG.info("({}) Opened directory handle for {}.", dokanFileInfo.context, file);
					return NTStatus.STATUS_SUCCESS;
				} catch (NoSuchFileException e) {
					return NTStatus.NO_SUCH_FILE;
				} catch (IOException e) {
					return NTStatus.IO_DEVICE_ERROR;
				}

			} else {

				LOG.info("{} is a file.", file);
				try {
					dokanFileInfo.context = fac.openFile(actualFile, StandardOpenOption.READ);
					return NTStatus.STATUS_SUCCESS;
				} catch (NoSuchFileException e) {
					return NTStatus.NO_SUCH_FILE;
				} catch (AccessDeniedException e) {
					return NTStatus.STATUS_ACCESS_DENIED;
				} catch (IOException e) {
					return NTStatus.IO_DEVICE_ERROR;
				}
			}
		}

	}

	//TODO: optimize?
	boolean containsAny(int set, int... options) {
		int tmp = 0;
		for (int i : options) {
			tmp |= i;
		}
		return (set & tmp) != 0;
	}

	@Override
	public void cleanup(WString file, DokanFileInfo dokanFileInfo) {
		if (dokanFileInfo.context == 0) {
			//LOG
			return;
		}
		try {
			fac.close(dokanFileInfo.context);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void closeFile(WString file, DokanFileInfo dokanFileInfo) {
		Path actualFile = getRootedPath(file);
		if (dokanFileInfo.context == 0) {
			//LOG
			return;
		}

		if (fac.exists(dokanFileInfo.context)) {
			try {
				fac.close(dokanFileInfo.context);
			} catch (IOException e) {
				//LOG.warn("({}) closeFile(): Unable to close handle to resource {}. To close it please restart the adapter.\n{}", dokanyFileInfo.Context, path, e);
			}
		}
		dokanFileInfo.context = 0;
	}

	@Override
	public int mounted(WString actualMountPoint, DokanFileInfo dokanFileInfo) {
		//return DokanFileSystem.super.mounted(actualMountPoint, dokanFileInfo);
		return NTStatus.STATUS_SUCCESS;
	}

	@Override
	public int unmounted(DokanFileInfo dokanFileInfo) {
		try {
			this.fac.close();
		} catch (IOException e) {
			//LOG.warn("Could not close all open handles.", e);
		}
		return NTStatus.STATUS_SUCCESS;
	}

	//TODO: is this necessary?
	private Path getRootedPath(WString rawPath) {
		String unixPath = rawPath.toString().replace('\\', '/');
		String relativeUnixPath = CharMatcher.is('/').trimLeadingFrom(unixPath);
		assert root.isAbsolute();
		return root.resolve(relativeUnixPath);
	}

}
