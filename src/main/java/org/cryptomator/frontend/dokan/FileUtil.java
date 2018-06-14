package org.cryptomator.frontend.dokan;

import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.EnumIntegerSet;
import com.sun.jna.platform.win32.WinBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	/**
	 * Indicates whether if the given windows file attribute is supported
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
			case NORMAL:
			case DIRECTORY:
				//TODO: log message
				break;
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * currently all acl permissions are given the user
	 *
	 * @param user
	 * @return
	 */
	public static AclAttribute getStandardAclPermissions(UserPrincipal user) {
		List<AclEntry> aclEntryList = new ArrayList<AclEntry>();
		AclEntry entry = AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(user).setPermissions(AclEntryPermission.values()).build();
		aclEntryList.add(entry);
		return new AclAttribute(aclEntryList);
	}

}
