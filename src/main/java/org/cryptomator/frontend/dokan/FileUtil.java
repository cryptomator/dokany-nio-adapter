package org.cryptomator.frontend.dokan;

import com.dokany.java.constants.FileAccess;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.EnumIntegerSet;
import com.google.common.collect.Sets;
import com.sun.jna.platform.win32.WinBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.Set;

public class FileUtil {

	/**
	 * TODO: support for other attributes ?
	 *
	 * @param attr the DOS file attributes of the java standard API
	 * @return
	 */
	public static EnumIntegerSet<FileAttribute> dosAttributesToEnumIntegerSet(DosFileAttributes attr) {
		EnumIntegerSet<FileAttribute> set = new EnumIntegerSet<>(FileAttribute.class);
		if (attr.isArchive()) {
			set.add(FileAttribute.ARCHIVE);
		}
		if (attr.isHidden()) {
			set.add(FileAttribute.HIDDEN);
		}
		if (attr.isReadOnly()) {
			set.add(FileAttribute.READONLY);
		}
		if (attr.isSystem()) {
			set.add(FileAttribute.SYSTEM);
		}
		if (attr.isDirectory()) {
			set.add(FileAttribute.DIRECTORY);
		}
		if (attr.isRegularFile()) {
			set.add(FileAttribute.NORMAL);
		}
		if (attr.isSymbolicLink()) {
			set.add(FileAttribute.REPARSE_POINT);
		}
		return set;
	}

	public static WinBase.FILETIME javaFileTimeToWindowsFileTime(FileTime t) {
		return new WinBase.FILETIME(new Date(t.toMillis()));
	}

	public static Path resolveToAbsoluteAndNormalizedPath(String s) {
		return Paths.get(s).normalize().toAbsolutePath();
	}

	/**
	 * Indicates wether if the given windows file attribute is supported
	 *
	 * @param attr
	 * @return
	 */
	public static boolean isFileAttributeSupported(FileAttribute attr) {
		switch (attr) {
			case ARCHIVE:
			case HIDDEN:
			case READONLY:
			case SYSTEM:
			case DIRECTORY:
			case NORMAL:
			case REPARSE_POINT:
				return true;
			default:
				return false;
		}
	}

	/**
	 * @param p
	 * @param attr
	 * @throws IOException
	 */
	public static void setAttribute(Path p, FileAttribute attr) throws IOException {
		switch (attr) {
			case ARCHIVE:
			case HIDDEN:
			case READONLY:
			case SYSTEM:
				Files.setAttribute(p, "dos:" + attr.name().toLowerCase(), true);
				break;
			case REPARSE_POINT:
				Files.setAttribute(p, "basic:symboliclink", true);
				break;
			case NORMAL:
				Files.setAttribute(p, "basic:regularfile", true);
			case DIRECTORY:
				Files.setAttribute(p, "basic:directory", true);
			default:
				throw new IllegalArgumentException();
		}
	}

	public static Set<OpenOption> accesRightsToOpenOptions(EnumIntegerSet<FileAccess> accessRights){
		Set<OpenOption> openOptions = Sets.newHashSet();
		for(FileAccess acc: accessRights){
			switch (acc){
				case DELETE:
					openOptions.add(StandardOpenOption.DELETE_ON_CLOSE);
					break;
				case READ_DATA: case GENERIC_READ:
					openOptions.add(StandardOpenOption.READ);
					break;
				case WRITE_DATA: case GENERIC_WRITE:
					openOptions.add(StandardOpenOption.WRITE);
					break;
				case APPEND_DATA:
					openOptions.add(StandardOpenOption.APPEND);
					break;
				case SYNCHRONIZE:
					openOptions.add(StandardOpenOption.SYNC);
					break;
				default:
					//TODO: LOG the unsupported attribute somewhere
			}
		}
		return openOptions;
	}
}
