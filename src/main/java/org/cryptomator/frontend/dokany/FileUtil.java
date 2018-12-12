package org.cryptomator.frontend.dokany;

import com.dokany.java.constants.AccessMask;
import com.dokany.java.constants.CreateOptions;
import com.dokany.java.constants.CreationDisposition;
import com.dokany.java.constants.EnumInteger;
import com.dokany.java.constants.FileAccess;
import com.dokany.java.constants.FileAccessMask;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.EnumIntegerSet;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.util.Set;
import java.util.stream.IntStream;

public class FileUtil {

	static final FileAttribute[] supportedAttributeValuesToSet = new FileAttribute[]{FileAttribute.HIDDEN, FileAttribute.READONLY, FileAttribute.SYSTEM, FileAttribute.ARCHIVE};

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
		if (attr.isSymbolicLink()) {
			set.add(FileAttribute.REPARSE_POINT);
		}
		if (attr.isRegularFile() && set.isEmpty()) {
			set.add(FileAttribute.NORMAL);
		}
		return set;
	}

	public static void setAttribute(DosFileAttributeView attrView, FileAttribute attr, boolean value) throws IOException {
		switch (attr) {
			case ARCHIVE:
				attrView.setArchive(value);
				break;
			case HIDDEN:
				attrView.setHidden(value);
				break;
			case READONLY:
				attrView.setReadOnly(value);
				break;
			case SYSTEM:
				attrView.setSystem(value);
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
		return rawPattern.codePoints().flatMap(c -> {
			if (Character.isBmpCodePoint(c) && globOperatorsToEscapeCodePoints.contains(c)) {
				return IntStream.of((int) '\\', c);
			} else {
				return IntStream.of(c);
			}
		}).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
	}

	public static Set<OpenOption> buildOpenOptions(EnumIntegerSet<AccessMask> accessMasks, EnumIntegerSet<FileAccessMask> fileAccessMasks, EnumIntegerSet<FileAttribute> fileAttributes, EnumIntegerSet<CreateOptions> createOptions, CreationDisposition creationDisposition, boolean append, boolean fileExists) {
		Set<OpenOption> openOptions = Sets.newHashSet();
		if (accessMasks.contains(AccessMask.GENERIC_WRITE)) {
			openOptions.add(StandardOpenOption.WRITE);
		}
		if (accessMasks.contains(AccessMask.GENERIC_READ)) {
			openOptions.add(StandardOpenOption.READ);
			//openOptions.add(StandardOpenOption.SYNC); TODO: research to what flags GENERIC_READ, WRITE and ALL translates
		}
		if (accessMasks.contains(AccessMask.MAXIMUM_ALLOWED) || accessMasks.contains(AccessMask.GENERIC_ALL)) {
			openOptions.add(StandardOpenOption.READ);
			openOptions.add(StandardOpenOption.WRITE);
		}
		if (accessMasks.contains(AccessMask.SYNCHRONIZE)) {
			openOptions.add(StandardOpenOption.SYNC);
		}
		if (append) {
			openOptions.add(StandardOpenOption.APPEND);
		}
		if (accessMasks.contains(AccessMask.DELETE) && createOptions.contains(CreateOptions.FILE_DELETE_ON_CLOSE)) {
			openOptions.add(StandardOpenOption.DELETE_ON_CLOSE);
		}
		if (fileAttributes.contains(FileAttribute.SPARSE_FILE)) {
			openOptions.add(StandardOpenOption.SPARSE);
		}
		if (fileExists) {
			switch (creationDisposition) {
				case CREATE_NEW:
					//FAILS
					openOptions.add(StandardOpenOption.CREATE_NEW);
					break;
				case CREATE_ALWAYS:
					openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
					break;
				case OPEN_EXISTING:
					//SUCCESS
					break;
				case OPEN_ALWAYS:
					openOptions.add(StandardOpenOption.CREATE);
					break;
				case TRUNCATE_EXISTING:
					openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
					break;
				default:
					throw new IllegalStateException("Unknown createDispostion attribute: " + creationDisposition.name());
			}
		} else {
			switch (creationDisposition) {
				case CREATE_NEW:
					openOptions.add(StandardOpenOption.CREATE_NEW);
					break;
				case CREATE_ALWAYS:
					openOptions.add(StandardOpenOption.CREATE);
					break;
				case OPEN_EXISTING:
					//FAILS
					//return
					break;
				case OPEN_ALWAYS:
					openOptions.add(StandardOpenOption.CREATE);
					break;
				case TRUNCATE_EXISTING:
					openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
					break;
				default:
					throw new IllegalStateException("Unknown createDispostion attribute: " + creationDisposition.name());
			}
		}
		return openOptions;
	}

}
