package org.cryptomator.frontend.dokan;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.Win32FindStreamData;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.*;
import com.sun.jna.platform.win32.WinBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Set;

public class ReadOnlyAdapter extends DokanyFileSystem {

	public ReadOnlyAdapter(DeviceOptions deviceOptions, VolumeInformation volumeInfo, FreeSpace freeSpace, Date rootCreationDate, String rootPath) {
		super(deviceOptions, volumeInfo, freeSpace, rootCreationDate, rootPath);
	}

	public void mounted() throws IOException {

	}

	public void unmounted() throws IOException {

	}

	public boolean doesPathExist(String s) throws IOException {
		return Files.exists(Paths.get(s));
	}

	public Set<WinBase.WIN32_FIND_DATA> findFilesWithPattern(String s, DokanyFileInfo dokanyFileInfo, String s1) throws IOException {
		return null;
	}

	public Set<Win32FindStreamData> findStreams(String s) throws IOException {
		return null;
	}

	public void unlock(String s, int i, int i1) throws IOException {

	}

	public void lock(String s, int i, int i1) throws IOException {

	}

	public void move(String s, String s1, boolean b) throws IOException {

	}

	public void deleteFile(String s, DokanyFileInfo dokanyFileInfo) throws IOException {

	}

	public void deleteDirectory(String s, DokanyFileInfo dokanyFileInfo) throws IOException {

	}

	@Override
	public FileData read(String path, int offset, int readLength) throws IOException {
	}

	public int write(String s, int i, byte[] bytes, int i1) throws IOException {
		return 0;
	}

	public void createEmptyFile(String s, long l, EnumIntegerSet<FileAttribute> enumIntegerSet) throws IOException {

	}

	public void createEmptyDirectory(String s, long l, EnumIntegerSet<FileAttribute> enumIntegerSet) throws IOException {

	}

	public void flushFileBuffers(String s) throws IOException {

	}

	public void cleanup(String s, DokanyFileInfo dokanyFileInfo) throws IOException {

	}

	public void close(String s, DokanyFileInfo dokanyFileInfo) throws IOException {

	}

	public int getSecurity(String s, int i, byte[] bytes) throws IOException {
		return 0;
	}

	public void setSecurity(String s, int i, byte[] bytes) throws IOException {

	}

	public long truncate(String s) throws IOException {
		return 0;
	}

	public void setAllocationSize(String s, int i) throws IOException {

	}

	public void setEndOfFile(String s, int i) throws IOException {

	}

	public void setAttributes(String s, EnumIntegerSet<FileAttribute> enumIntegerSet) throws IOException {

	}

	/**
	 * Returns information of a file given by path
	 * TODO: support more windows file attributes!
	 * @param path A path pointing to the specific file
	 * @return FullFileInfo
	 * @throws IOException
	 */
	public FullFileInfo getInfo(String path) throws IOException {
		Path p = Paths.get(path).normalize().toAbsolutePath();
		if(Files.exists(p)){
			DosFileAttributes attr = Files.getFileAttributeView(p, DosFileAttributeView.class).readAttributes();
			long index = 0;
			if(attr.fileKey() != null){
				index = (long) attr.fileKey();
			}
			return new FullFileInfo(p.toString(),
					index,
					FileUtil.dosAttributesToEnumIntegerSet(attr),
					volumeInfo.getSerialNumber(),
					FileUtil.javaFileTimeToWindowsFileTime(attr.creationTime()),
					FileUtil.javaFileTimeToWindowsFileTime(attr.lastAccessTime()),
					FileUtil.javaFileTimeToWindowsFileTime(attr.lastModifiedTime()));
		}
		else{
			throw new FileNotFoundException();
		}
	}

	/**
	 * Sets the Creation, last access and last modified time stamps of a file given by path
	 * @param path String representign the path to the file
	 * @param creationTime new creation timestamp given in windows coding
	 * @param lastAccessTime new last access timestamp given in windows coding
	 * @param lastModificationTime new last modified timestamp given in windows coding
	 * @throws IOException
	 */
	public void setTime(String path, WinBase.FILETIME creationTime, WinBase.FILETIME lastAccessTime, WinBase.FILETIME lastModificationTime) throws IOException {
		Path p = Paths.get(path).normalize().toAbsolutePath();
		if(Files.exists(p)){
			Files.setAttribute(p, "basic:creationTime", FileTime.fromMillis(creationTime.toDate().getTime()));
			Files.setAttribute(p, "basic:lastAccessTime", FileTime.fromMillis(lastAccessTime.toDate().getTime()));
			Files.setAttribute(p, "basic:lastModificationTime", FileTime.fromMillis(lastModificationTime.toDate().getTime()));
		}
		else {
			throw new FileNotFoundException();
		}
	}
}
