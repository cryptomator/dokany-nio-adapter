package org.cryptomator.frontend.dokan.legacy;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.Win32FindStreamData;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.constants.FileSystemFeature;
import com.dokany.java.structure.*;
import com.sun.jna.platform.win32.WinBase;
import org.apache.commons.io.IOCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: research must be taken wether we need all the path checking or not (because before every operation a zwCreate is called)!
 */
public class ReadOnlyAdapter_OLD extends DokanyFileSystem {

	private IOCase ioCase;
	private OpenFileFactory fac;

	public static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAdapter_OLD.class);

	public ReadOnlyAdapter_OLD(DeviceOptions deviceOptions, VolumeInformation volumeInfo, FreeSpace freeSpace, Date rootCreationDate, String rootPath) {
		super(deviceOptions, volumeInfo, freeSpace, rootCreationDate, rootPath);
		fac = new OpenFileFactory();
		if (volumeInfo.getFileSystemFeatures().contains(FileSystemFeature.CASE_PRESERVED_NAMES)
				&& volumeInfo.getFileSystemFeatures().contains(FileSystemFeature.CASE_PRESERVED_NAMES)) {
			ioCase = IOCase.SENSITIVE;
		} else {
			ioCase = IOCase.INSENSITIVE;
		}
	}

	private Path getRootedPath(String s) {
		return FileUtil.resolveToAbsoluteAndNormalizedPath(getRoot() + s);
	}

	public void mounted() throws IOException {
		//stubby
	}

	public void unmounted() throws IOException {
		//stubby
	}

	public boolean doesPathExist(String s) throws IOException {
		return Files.exists(Paths.get(s));
	}

	/**
	 * List all Files which contains the given pattern.
	 * WARNING: currently the used libary uses the null pattern to list ALL files in the path, thus this method should be null save
	 *
	 * @param path
	 * @param dokanyFileInfo
	 * @param pattern
	 * @return
	 * @throws IOException
	 */
	public Set<WinBase.WIN32_FIND_DATA> findFilesWithPattern(String path, DokanyFileInfo dokanyFileInfo, String pattern) throws IOException {
		Path p = getRootedPath(path);
		Set<WinBase.WIN32_FIND_DATA> findings;
		try (Stream<Path> stream = Files.list(p)) {
			//stream.filter(path1 -> path.toString().contains(pattern)).map(FileUtil::pathToFindData);
			Stream<Path> streamByPattern;
			if (pattern == null || pattern.equals("*")) {
				//we want to list all files
				streamByPattern = stream;
			} else {
				streamByPattern = stream.filter(path1 -> path1.toString().contains(pattern));
			}
			findings = streamByPattern.map(path2 -> {
				try {
					return getInfoByPath(path2).toWin32FindData();
				} catch (IOException e) {
					return new WinBase.WIN32_FIND_DATA();
				}
			}).collect(Collectors.toSet());
		}
		return findings;
	}

	public Set<Win32FindStreamData> findStreams(String s) throws IOException {
		return null;
	}

	public void unlock(String s, int i, int i1) throws IOException {
		//stubby
	}

	public void lock(String s, int i, int i1) throws IOException {
		//stubby
	}

	public void move(String s, String s1, boolean b) throws IOException {
		//stubby
	}

	public void deleteFile(String s, DokanyFileInfo dokanyFileInfo) throws IOException {
		//stubby
	}

	public void deleteDirectory(String s, DokanyFileInfo dokanyFileInfo) throws IOException {
		//stubby
	}

	@Override
	public FileData read(String path, long offset, int readLength) throws IOException {
		Path p = getRootedPath(path);
		Set openFlag = new HashSet();
		openFlag.add(StandardOpenOption.READ);
		long fileHandle = fac.open(p,openFlag);
		//TODO: where is the dokany FileInfo??
		return fac.get(fileHandle).read(offset,readLength);
	}

	public int write(String s, int i, byte[] bytes, int i1) throws IOException {
		//stubby
		return 0;
	}

	public void createEmptyFile(String s, long l, EnumIntegerSet<FileAttribute> enumIntegerSet) throws IOException {
		//stubby
	}

	public void createEmptyDirectory(String s, long l, EnumIntegerSet<FileAttribute> enumIntegerSet) throws IOException {
		//stubby
	}

	public void flushFileBuffers(String s) throws IOException {
		//stubby
	}



	public int getSecurity(String s, int i, byte[] bytes) throws IOException {
		//stubby
		return 0;
	}

	public void setSecurity(String s, int i, byte[] bytes) throws IOException {
		//stubby
	}

	/**
	 * Method nowhere used
	 *
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public long truncate(String s) throws IOException {
		return 0;
	}

	/**
	 * this method depends on physical file size, meaning the sectors it uses on a disk
	 *
	 * @param path
	 * @param size
	 * @throws IOException
	 */
	public void setEndOfFile(String path, int size) throws IOException {
	}


	/**
	 * Returns information of a file given by path
	 * TODO: support more windows file attributes!
	 *
	 * @param path String encoding the path of the desired file
	 * @return FullFileInfo
	 * @throws IOException
	 */
	public FullFileInfo getInfo(String path) throws IOException {
		return getInfoByPath(getRootedPath(path));

	}

}
