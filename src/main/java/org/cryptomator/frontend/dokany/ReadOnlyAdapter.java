package org.cryptomator.frontend.dokany;

import com.dokany.java.constants.Win32ErrorCode;
import com.dokany.java.next.DokanAPI;
import com.dokany.java.next.DokanFileSystem;
import com.dokany.java.next.constants.CreateDispositions;
import com.dokany.java.next.constants.CreateOptions;
import com.dokany.java.next.nativeannotations.Enum;
import com.dokany.java.next.nativeannotations.EnumSet;
import com.dokany.java.next.nativeannotations.Out;
import com.dokany.java.next.nativeannotations.Unsigned;
import com.dokany.java.next.structures.DokanFileInfo;
import com.dokany.java.next.structures.DokanIOSecurityContext;
import com.dokany.java.next.structures.DokanOperations;
import com.google.common.base.CharMatcher;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.cryptomator.frontend.dokany.locks.DataLock;
import org.cryptomator.frontend.dokany.locks.LockManager;
import org.cryptomator.frontend.dokany.locks.PathLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class ReadOnlyAdapter implements DokanFileSystem {

	private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAdapter.class);
	private static final WString MATCH_ALL_PATTERN = new WString("*");
	private static final char[] EMPTY_ALT_FILE_NAME = new char[]{'\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0'};

	private final Path root;
	private final LockManager lockManager;
	private final OpenHandleFactory fac;
	private final FileStore fileStore;

	public ReadOnlyAdapter(Path root, LockManager lockManager) {
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
			return NTStatus.OBJECT_PATH_INVALID;
		}

		//TODO: too restrictive
		/*
		if (containsAny(desiredAccess, WinNT.FILE_WRITE_DATA, WinNT.FILE_WRITE_ATTRIBUTES, WinNT.FILE_WRITE_EA, WinNT.FILE_GENERIC_WRITE, WinNT.FILE_APPEND_DATA, WinNT.GENERIC_ALL, WinNT.GENERIC_WRITE) || !(isAny(createDisposition, CreateDisposition.FILE_OPEN, CreateDisposition.FILE_OPEN_IF)) || containsAny(createOptions, CreateOptions.FILE_DELETE_ON_CLOSE)) {
			return NTStatus.STATUS_ACCESS_DENIED;
		}

		 */


		assert (createDisposition == CreateDispositions.FILE_OPEN || createDisposition == CreateDispositions.FILE_OPEN_IF);
		try (PathLock pathLock = lockManager.createPathLock(file.toString()).forWriting(); DataLock dataLock = pathLock.lockDataForWriting()) {
			DosFileAttributes attr;

			try {
				attr = Files.readAttributes(actualFile, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			} catch (NoSuchFileException e) {
				return NTStatus.OBJECT_NAME_NOT_FOUND;
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
					return NTStatus.OBJECT_NAME_NOT_FOUND;
				} catch (IOException e) {
					return NTStatus.IO_DEVICE_ERROR;
				}

			} else {

				LOG.info("{} is a file.", file);
				try {
					dokanFileInfo.context = fac.openFile(actualFile, StandardOpenOption.READ);
					return NTStatus.STATUS_SUCCESS;
				} catch (NoSuchFileException e) {
					return NTStatus.OBJECT_NAME_NOT_FOUND;
				} catch (AccessDeniedException e) {
					return Win32ErrorCode.ERROR_ACCESS_DENIED.getMask();
				} catch (IOException e) {
					return NTStatus.IO_DEVICE_ERROR;
				}
			}
		}

	}

	private boolean isAny(int value, int... comparsions) {
		for (int i : comparsions) {
			if (value == i) {
				return true;
			}
		}
		return false;
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
		Path actualFile = getRootedPath(file);
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

	/*
	@Override
	public int readFile(WString file, ByteBuffer buffer, @Out @Unsigned IntByReference numberOfBytesRead, @Unsigned long offset, DokanFileInfo dokanFileInfo) {
		//LOG.trace("({}) readFile() is called for {}.", dokanyFileInfo.Context, path);
		if (dokanFileInfo.context == 0) {
			//LOG.debug("readFile(): Invalid handle to {} ", path);
			return NTStatus.INVALID_HANDLE;
		} else if (dokanFileInfo.getIsDirectory()) {
			//LOG.trace("({}) {} is a directory. Unable to read Data from it.", dokanyFileInfo.Context, path);
			return NTStatus.STATUS_ACCESS_DENIED;
		}

		OpenFile handle = (OpenFile) fac.get(dokanFileInfo.context);
		if (handle == null) {
			return NTStatus.INVALID_HANDLE;
		}


		assert handle != null;
		try (PathLock pathLock = lockManager.createPathLock(file.toString()).forReading(); DataLock dataLock = pathLock.lockDataForReading()) {
			numberOfBytesRead.setValue(handle.read(buffer, offset));
			//LOG.trace("({}) Data successful read from {}.", dokanyFileInfo.Context, path);
			return NTStatus.STATUS_SUCCESS;
		} catch (NonReadableChannelException e) {
			//LOG.trace("({}) readFile(): File {} not opened for reading.", dokanyFileInfo.Context, path);
			return NTStatus.STATUS_ACCESS_DENIED;
		} catch (IOException e) {
			//LOG.warn("({}) readFile(): IO error while reading file {}.\n{}", dokanyFileInfo.Context, path, e);
			return NTStatus.IO_DEVICE_ERROR;
		}
	}

	 */

	/*
	@Override
	public int getFileInformation(WString fileName, @Out ByHandleFileInformation handleFileInfo, DokanFileInfo dokanFileInfo) {
		Path path = getRootedPath(fileName);
		LOG.info("({}) getFileInformation() is called for {}.", dokanFileInfo.context, fileName);
		if (dokanFileInfo.context == 0) {
			//LOG.debug("getFileInformation(): Invalid handle to {}.", path);
			return NTStatus.INVALID_HANDLE;
		}
		try (PathLock pathLock = lockManager.createPathLock(fileName.toString()).forReading(); DataLock dataLock = pathLock.lockDataForReading()) {
			DosFileAttributes attr = Files.readAttributes(path, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			long size = attr.size();
			var key = attr.fileKey();

			//TODO refactor out?
			handleFileInfo.dwFileAttributes = dosAttributesToInt(attr);
			handleFileInfo.ftCreationTime = new WinBase.FILETIME(new Date(attr.creationTime().toMillis()));
			handleFileInfo.ftLastAccessTime = new WinBase.FILETIME(new Date(attr.lastAccessTime().toMillis()));
			handleFileInfo.ftLastWriteTime = new WinBase.FILETIME(new Date(attr.lastModifiedTime().toMillis()));

			handleFileInfo.nNumberOfLinks = 1;
			handleFileInfo.dwVolumeSerialNumber = root.hashCode();
			handleFileInfo.nFileSizeLow = (int) (size & 0xFFFFFFFFFL);
			handleFileInfo.nFileSizeHigh = (int) ((size >> 32) & 0xFFFFFFFFFL); //since size can only be postive long, we do not need to use the unsigned shift
			if (key == null) {
				handleFileInfo.nFileIndexHigh = 0;
				handleFileInfo.nFileIndexLow = 0;//path.hashCode(); //TODO
			} else {
				handleFileInfo.nFileIndexLow = (int) ((long) key & 0xFFFFFFFFFL);
				handleFileInfo.nFileIndexHigh = (int) (((long) key >>> 32) & 0xFFFFFFFFFL);
			}

			//LOG.trace("({}) File Information successful read from {}.", dokanyFileInfo.Context, path);
			return NTStatus.STATUS_SUCCESS;
		} catch (NoSuchFileException e) {
			return NTStatus.OBJECT_NAME_NOT_FOUND;
		} catch (IOException e) {
			//LOG.warn("({}) getFileInformation(): IO error occurred reading meta data from {}.\n{}", dokanyFileInfo.Context, path, e);
			return NTStatus.IO_DEVICE_ERROR;
		}
	}
	 */

	//TODO: place somewhere else
	private int dosAttributesToInt(DosFileAttributes attr) {
		int out = 0;
		if (attr.isReadOnly()) {
			out |= WinNT.FILE_ATTRIBUTE_READONLY;
		}
		if (attr.isArchive()) {
			out |= WinNT.FILE_ATTRIBUTE_ARCHIVE;
		}
		if (attr.isSystem()) {
			out |= WinNT.FILE_ATTRIBUTE_SYSTEM;
		}
		if (attr.isHidden()) {
			out |= WinNT.FILE_ATTRIBUTE_HIDDEN;
		}


		if (attr.isDirectory()) {
			out |= WinNT.FILE_ATTRIBUTE_DIRECTORY;
		}
		if (attr.isSymbolicLink()) {
			out |= WinNT.FILE_ATTRIBUTE_REPARSE_POINT;
		}

		if (attr.isRegularFile() && out == 0) {
			out |= WinNT.FILE_ATTRIBUTE_NORMAL;
		}
		return out;
	}

	@Override
	public int findFiles(WString directory, DokanOperations.FillWin32FindData fillFindDataFunction, DokanFileInfo dokanFileInfo) {
		return NTStatus.STATUS_SUCCESS;
		//LOG?
		//return findFilesWithPattern(directory, MATCH_ALL_PATTERN, fillFindDataFunction, dokanFileInfo);
	}

	@Override
	public int findFilesWithPattern(WString path, WString searchPattern, DokanOperations.FillWin32FindData FillFindData, DokanFileInfo dokanFileInfo) {
		Path actualPath = getRootedPath(path);
		assert actualPath.isAbsolute();
		LOG.info("({}) findFilesWithPattern() is called for {} with search pattern {}.", dokanFileInfo.context, actualPath, searchPattern.toString());
		if (dokanFileInfo.context == 0) {
			//LOG.debug("findFilesWithPattern(): Invalid handle to {}.", actualPath);
			return NTStatus.INVALID_HANDLE;
		}

		final DirectoryStream.Filter<Path> filter;
		if (searchPattern.equals(MATCH_ALL_PATTERN)) {
			filter = (Path p) -> true;  // match all
		} else {
			//TODO:try to circumvent the native call
			filter = (Path p) -> DokanAPI.DokanIsNameInExpression(searchPattern, new WString(p.getFileName().toString()), true);
		}
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(actualPath, filter)) {
			Spliterator<Path> spliterator = Spliterators.spliteratorUnknownSize(ds.iterator(), Spliterator.DISTINCT);
			StreamSupport.stream(spliterator, true).map(p -> {
				assert p.isAbsolute();
				try {
					return createWin32FindData(p);
				} catch (IOException e) {
					//LOG.warn("({}) findFilesWithPattern(): IO error accessing {}. Will be ignored in file listing. Reported Exception:", dokanyFileInfo.Context, p, e);
					return null;
				}
			}).filter(Objects::nonNull).forEach(file -> {
				assert file != null;
				//LOG.trace("({}) findFilesWithPattern(): found file {}", dokanyFileInfo.Context, file.getFileName());
				FillFindData.invoke(file, dokanFileInfo);
			});
			//LOG.trace("({}) Successful searched content in {}.", dokanyFileInfo.Context, actualPath);
			return NTStatus.STATUS_SUCCESS;
		} catch (IOException e) {
			//LOG.warn("({}) findFilesWithPattern(): Unable to list content of directory {}.", dokanyFileInfo.Context, actualPath, e);
			return NTStatus.IO_DEVICE_ERROR;
		}
	}

	private WinBase.WIN32_FIND_DATA createWin32FindData(Path p) throws IOException {
		var attr = Files.readAttributes(p, DosFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
		if (attr.isSymbolicLink()) {
			throw new IOException("Unsupported file type in listing");
		}

		var size = attr.size();

		char[] tmp = p.getFileName().toString().toCharArray();
		char[] cFileName = Arrays.copyOf(p.getFileName().toString().toCharArray(), WinBase.MAX_PATH);
		if (tmp.length < WinBase.MAX_PATH) {
			cFileName[tmp.length] = '\0';
		} else {
			cFileName[cFileName.length - 1] = '\0';
		}

		return new WinBase.WIN32_FIND_DATA(dosAttributesToInt(attr), new WinBase.FILETIME(new Date(attr.creationTime().toMillis())), new WinBase.FILETIME(new Date(attr.lastAccessTime().toMillis())), new WinBase.FILETIME(new Date(attr.lastModifiedTime().toMillis())), (int) ((size >> 32) & 0xFFFFFFFFFL), (int) (size & 0xFFFFFFFFFL), 0, 0, cFileName, EMPTY_ALT_FILE_NAME);
	}

	@Override
	public int getDiskFreeSpace(LongByReference freeBytesAvailable, LongByReference totalNumberOfBytes, LongByReference totalNumberOfFreeBytes, DokanFileInfo dokanFileInfo) {
		//LOG.trace("getFreeDiskSpace() is called.");
		try {
			totalNumberOfBytes.setValue(fileStore.getTotalSpace());
			freeBytesAvailable.setValue(fileStore.getUsableSpace());
			totalNumberOfFreeBytes.setValue(fileStore.getUnallocatedSpace());
			return NTStatus.STATUS_SUCCESS;
		} catch (IOException e) {
			//LOG.warn("({}) getFreeDiskSpace(): Unable to detect disk space status.\n{}", dokanyFileInfo.Context, e);
			return NTStatus.IO_DEVICE_ERROR;
		}
	}

	@Override
	public int getVolumeInformation(@Out Pointer volumeNameBuffer, @Unsigned int volumeNameBufferSize, @Out @Unsigned IntByReference volumeSerialNumber, @Out @Unsigned IntByReference maximumComponentLength, @Out @EnumSet IntByReference fileSystemFlags, @Out Pointer filesystemNameBuffer, @Unsigned int filesystemNameBufferSize, DokanFileInfo dokanFileInfo) {
		LOG.info("({}) getVolumeInformation called",dokanFileInfo.context);
		volumeNameBuffer.setWideString(0,"TEST");
		volumeSerialNumber.setValue(root.hashCode());
		maximumComponentLength.setValue(255);
		fileSystemFlags.setValue(WinNT.FILE_CASE_PRESERVED_NAMES | WinNT.FILE_READ_ONLY_VOLUME | WinNT.FILE_UNICODE_ON_DISK);
		filesystemNameBuffer.setWideString(0, "DOKAN_JAVA");
		return NTStatus.STATUS_SUCCESS;
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
