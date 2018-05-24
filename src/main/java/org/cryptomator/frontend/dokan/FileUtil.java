package org.cryptomator.frontend.dokan;

import com.dokany.java.constants.FileAccess;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.filesecurity.SecurityIdentifier;
import com.dokany.java.structure.filesecurity.SelfRelativeSecurityDescriptor;
import com.google.common.collect.Sets;
import com.sun.jna.platform.win32.WinBase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.UserPrincipal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
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

	public static Set<OpenOption> accesRightsToOpenOptions(EnumIntegerSet<FileAccess> accessRights) {
		Set<OpenOption> openOptions = Sets.newHashSet();
		for (FileAccess acc : accessRights) {
			switch (acc) {
				case DELETE:
					openOptions.add(StandardOpenOption.DELETE_ON_CLOSE);
					break;
				case READ_DATA:
				case GENERIC_READ:
					openOptions.add(StandardOpenOption.READ);
					break;
				case WRITE_DATA:
				case GENERIC_WRITE:
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

	/**
	 * Creates the standard securityDescriptor with generic read, write and execute access
	 *
	 * @return
	 */
	public static byte[] getStandardSecurityDescriptor() {
		String stringSid = null;
		try {
			stringSid = getSidOfCurrentUser();
		} catch (IOException e) {
			stringSid = "S-1-1-0";
		}
		SecurityIdentifier sid = SecurityIdentifier.fromString(stringSid);
		ByteBuffer buf = ByteBuffer.allocate(20 + sid.sizeOfByteArray() + 12 + 16 + sid.sizeOfByteArray()); //header+ my sid+ everyone sid + header acl+ace +my sid
		buf.put(new byte[]{0x01, //revision
				0x00, //sbz1
				36,// second half of control flag
				-128});
		buf.putInt(Integer.reverseBytes(20));
		buf.putInt(Integer.reverseBytes(20 + sid.sizeOfByteArray()));
		buf.putInt(0);
		buf.putInt(Integer.reverseBytes(20 + sid.sizeOfByteArray() + 12));
		buf.put(sid.toByteArray()); //me
		buf.put(new byte[]{0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00}); //group
		buf.put(new byte[]{0x02, 0x00}); //first two bytes of acl header
		buf.putShort(Short.reverseBytes((short) (16 + sid.sizeOfByteArray()))); //size
		buf.put(new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x03}); //ace count, sbz2, acetype, aceflags
		buf.putShort(Short.reverseBytes((short) (8 + sid.sizeOfByteArray())));
		buf.put(new byte[]{0x00, 0x00, 0x00, 0x10});
		buf.put(sid.toByteArray());
		return buf.array();
	}

	public static String getSidOfCurrentUser() throws IOException {
		ProcessBuilder pb = new ProcessBuilder("whoami", "/user", "/NH");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(pb.start().getInputStream()))) {
			String s = reader.readLine();
			return s.substring(s.indexOf(" S-") + 1);
		}
	}
}
