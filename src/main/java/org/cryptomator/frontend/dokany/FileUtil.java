package org.cryptomator.frontend.dokany;

import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.EnumIntegerSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Set;
import java.util.stream.IntStream;

public class FileUtil {

	private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
	private static final Set<Integer> globOperatorsToEscapeCodePoints;

	static {
		char[] globOperatorsToEscape = new char[]{'[', ']', '{', '}'};
		globOperatorsToEscapeCodePoints = Sets.newHashSet();
		for (int i = 0; i < globOperatorsToEscape.length; i++) {
			globOperatorsToEscapeCodePoints.add(Character.codePointAt(globOperatorsToEscape, i));
		}
	}

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

	/**
	 * Method for preprocessing a string containing glob patterns for a {@link java.nio.file.PathMatcher}. These characters must be escaped to not cause a different matching expression.
	 * This method escapes the characters defined in {@link FileUtil#globOperatorsToEscapeCodePoints}.
	 *
	 * @param rawPattern a string possibly containing unwanted glob operators
	 * @return a String where some glob operators are escaped
	 */
	public static String addEscapeSequencesForPathPattern(String rawPattern) {
		String tmp = rawPattern.codePoints().flatMap(c -> {
			if (Character.isBmpCodePoint(c) && globOperatorsToEscapeCodePoints.contains(c)) {
				return IntStream.of((int) '\\', c);
			} else {
				return IntStream.of(c);
			}
		}).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
		LOG.trace("Escaped sequence is now {}.", tmp);
		return tmp;
	}

}
