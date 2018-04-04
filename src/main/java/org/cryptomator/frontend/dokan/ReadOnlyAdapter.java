package org.cryptomator.frontend.dokan;

import com.dokany.java.DokanyFileSystem;
import com.dokany.java.Win32FindStreamData;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.*;
import com.sun.jna.platform.win32.WinBase;
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
import java.util.Set;

/**
 * TODO: research must be taken wether we need all the path checking or not (because before every operation a zwCreate is called)!
 */
public class ReadOnlyAdapter extends DokanyFileSystem {
	public static final Logger LOG = LoggerFactory.getLogger(ReadOnlyAdapter.class);

	public ReadOnlyAdapter(DeviceOptions deviceOptions, VolumeInformation volumeInfo, FreeSpace freeSpace, Date rootCreationDate, String rootPath) {
		super(deviceOptions, volumeInfo, freeSpace, rootCreationDate, rootPath);
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

	public Set<WinBase.WIN32_FIND_DATA> findFilesWithPattern(String s, DokanyFileInfo dokanyFileInfo, String s1) throws IOException {
		return null;
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
	public FileData read(String path, int offset, int readLength) throws IOException {
		//stubby
		return null;
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

	public void cleanup(String path, DokanyFileInfo dokanyFileInfo) throws IOException {
		if(dokanyFileInfo.deleteOnClose()){
			Files.delete(FileUtil.resolveToAbsoluteAndNormalizedPath(path));
		}
	}

	public void close(String s, DokanyFileInfo dokanyFileInfo) throws IOException {
		//stubby
	}

	public int getSecurity(String s, int i, byte[] bytes) throws IOException {
		//stubby
		return 0;
	}

	public void setSecurity(String s, int i, byte[] bytes) throws IOException {
		//stubby
	}

	public long truncate(String s) throws IOException {
		return 0;
	}

	public void setAllocationSize(String path, int size) throws IOException {
		Path p = FileUtil.resolveToAbsoluteAndNormalizedPath(path);
		if(Files.exists(p)){
			try (FileChannel fc = FileChannel.open(p, StandardOpenOption.WRITE) ){
				fc.truncate(size);
			}
		}
		else {
			throw new FileNotFoundException();
		}
	}

	public void setEndOfFile(String s, int i) throws IOException {

	}

	/**
	 * Sets the supported file attributes of a file given by its path
	 * TODO: WRITE TEST
	 * @param path String encoding the path of the desired file
	 * @param attrs Set of file attributes
	 * @throws IOException
	 */
	public void setAttributes(String path, EnumIntegerSet<FileAttribute> attrs) throws IOException {
		Path p = FileUtil.resolveToAbsoluteAndNormalizedPath(path);
		if(Files.exists(p)){
			for(FileAttribute attr : attrs){
				if(FileUtil.isFileAttributeSupported(attr)){
					FileUtil.setAttribute(p,attr);
				}
				else{
					LOG.debug("Windows file attribute {} is currently not supported and thus will be ignored", attr.name());
				}
			}
		}
		else{
			throw new FileNotFoundException();
		}
	}

	/**
	 * Returns information of a file given by path
	 * TODO: support more windows file attributes!
	 * @param path String encoding the path of the desired file
	 * @return FullFileInfo
	 * @throws IOException
	 */
	public FullFileInfo getInfo(String path) throws IOException {
		Path p = FileUtil.resolveToAbsoluteAndNormalizedPath(path);
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
	 * @param path String encoding the path of the desired file
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
