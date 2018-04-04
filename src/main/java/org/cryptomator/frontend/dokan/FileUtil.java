package org.cryptomator.frontend.dokan;

import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.EnumIntegerSet;
import com.sun.jna.platform.win32.WinBase;

import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;

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
		//long x = WinBase.FILETIME.dateToFileTime();
		return new WinBase.FILETIME(new Date(t.toMillis()));
	}
}
