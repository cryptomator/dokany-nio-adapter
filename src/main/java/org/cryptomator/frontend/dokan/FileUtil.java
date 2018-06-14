package org.cryptomator.frontend.dokan;

import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.EnumIntegerSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;

public class FileUtil {

	private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

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

	public static void setAttribute(DosFileAttributeView attrView, FileAttribute attr) throws IOException {
		switch (attr) {
			case ARCHIVE:
				attrView.setArchive(true);
				break;
			case HIDDEN:
				attrView.setHidden(true);
				break;
			case READONLY:
				attrView.setReadOnly(true);
				break;
			case SYSTEM:
				attrView.setSystem(true);
				break;
			default:
				LOG.debug("Windows file attribute {} is currently not supported and thus will be ignored", attr.name());
		}
	}

}
