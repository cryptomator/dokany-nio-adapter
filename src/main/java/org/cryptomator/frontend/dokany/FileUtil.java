package org.cryptomator.frontend.dokany;

import com.dokany.java.constants.AccessMask;
import com.dokany.java.constants.CreateOptions;
import com.dokany.java.constants.CreationDisposition;
import com.dokany.java.constants.FileAccessMask;
import com.dokany.java.constants.FileAttribute;
import com.dokany.java.structure.EnumIntegerSet;
import com.google.common.collect.Sets;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static com.dokany.java.constants.CreationDisposition.*;

public class FileUtil {

	public static final Instant WINDOWS_EPOCH_START = Instant.parse("1601-01-01T00:00:00Z");
	// from wdm.h
	public static final int FILE_SUPERSEDE = 0x00000000;
	public static final int FILE_OPEN = 0x00000001;
	public static final int FILE_CREATE = 0x00000002;
	public static final int FILE_OPEN_IF = 0x00000003;
	public static final int FILE_OVERWRITE = 0x00000004;
	public static final int FILE_OVERWRITE_IF = 0x00000005;

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
	 * Converts search string expression from the windows kernel to valid glob search patterns
	 * <p>
	 * TODO: maybe both methods should be merged for speed up?
	 *
	 * @param rawWindowsPattern a String that may contain unescaped glob patterns or windows kernel specific search patterns
	 * @return the corresponding glob search pattern for a {@link java.nio.file.PathMatcher}
	 */
	public static String convertToGlobPattern(String rawWindowsPattern) {
		String javaPattern = convertToJavaPattern(rawWindowsPattern);
		return addEscapeSequencesForPathPattern(javaPattern);
	}

	/**
	 * Replaces specific characters that are windows kernel specific search patterns. It is no problem to replace them directly, since all three are invalid filename characters (see <a href="https://docs.microsoft.com/de-de/windows/desktop/FileIO/naming-a-file"> Microsoft documentation</a>)
	 * 1. '>' is replaced by '?'
	 * 2. '<' is replaced by '*'
	 * 3. '"' is replaced by '.'
	 *
	 * @param rawWindowsPattern a String that may contain windows kernel specific search patterns
	 * @return
	 */
	private static String convertToJavaPattern(String rawWindowsPattern) {
		String tmp1 = rawWindowsPattern.replace('>', '?');
		String tmp2 = tmp1.replace('<', '*');
		String tmp3 = tmp2.replace('"', '.');
		return tmp3;
	}

	/**
	 * Method for preprocessing a string containing glob patterns for a {@link java.nio.file.PathMatcher}. These characters must be escaped to not cause a different matching expression.
	 * This method escapes the characters defined in {@link FileUtil#globOperatorsToEscapeCodePoints}.
	 *
	 * @param rawPattern a string possibly containing unwanted glob operators
	 * @return a String where some glob operators are escaped
	 */
	private static String addEscapeSequencesForPathPattern(String rawPattern) {
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
		if (accessMasks.contains(AccessMask.GENERIC_WRITE) || fileAccessMasks.contains(FileAccessMask.WRITE_DATA) || fileAccessMasks.contains(FileAccessMask.APPEND_DATA)) {
			openOptions.add(StandardOpenOption.WRITE);
		}
		if (accessMasks.contains(AccessMask.GENERIC_READ) || fileAccessMasks.contains(FileAccessMask.READ_DATA)) {
			openOptions.add(StandardOpenOption.READ);
		}
		if (accessMasks.contains(AccessMask.MAXIMUM_ALLOWED) || accessMasks.contains(AccessMask.GENERIC_ALL)) {
			openOptions.add(StandardOpenOption.READ);
			openOptions.add(StandardOpenOption.WRITE);
		}
		if (createOptions.contains(CreateOptions.FILE_WRITE_THROUGH)) {
			openOptions.add(StandardOpenOption.SYNC);
		}
		if (append) {
			openOptions.add(StandardOpenOption.APPEND);
		}
		// From the Java Documentation of DELETE_ON_CLOSE:This option is not recommended for use when opening files that are open concurrently by other entities.
//		if (accessMasks.contains(AccessMask.DELETE) && createOptions.contains(CreateOptions.FILE_DELETE_ON_CLOSE)) {
//			//openOptions.add(StandardOpenOption.DELETE_ON_CLOSE);
//		}
		if (fileAttributes.contains(FileAttribute.SPARSE_FILE)) {
			openOptions.add(StandardOpenOption.SPARSE);
		}
		switch (creationDisposition) {
			case CREATE_NEW:
				openOptions.add(StandardOpenOption.CREATE_NEW);
				openOptions.add(StandardOpenOption.WRITE); //Necessary, otherwise an Exceptions is thrown during filechannel creation
				break;
			case CREATE_ALWAYS:
				if (fileExists) {
					openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
				} else {
					openOptions.add(StandardOpenOption.CREATE);
					openOptions.add(StandardOpenOption.WRITE);
				}
				break;
			case OPEN_EXISTING:
				openOptions.add(StandardOpenOption.READ);
				break;
			case OPEN_ALWAYS:
				openOptions.add(StandardOpenOption.CREATE);
				if (!fileExists) openOptions.add(StandardOpenOption.WRITE);
				break;
			case TRUNCATE_EXISTING:
				openOptions.add(StandardOpenOption.TRUNCATE_EXISTING);
				break;
			default:
				throw new IllegalStateException("Unknown createDispostion attribute: " + creationDisposition.name());

		}
		return openOptions;
	}

	public static Optional<FileTime> toFileTime(WinBase.FILETIME windowsTime) {
		Instant instant = windowsTime.toDate().toInstant();
		if (instant.equals(WINDOWS_EPOCH_START)) {
			return Optional.empty();
		} else {
			return Optional.of(FileTime.from(instant));
		}
	}

	/**
	 * TODO
	 *
	 * @param fileAccess
	 * @return
	 */
	public static int mapFileGenericAccessToGenericAccess(int fileAccess) {

		boolean genericRead = false, genericWrite = false, genericExecute = false, genericAll = false;
		int outDesiredAccess = fileAccess;

		if ((outDesiredAccess & WinNT.FILE_GENERIC_READ) == WinNT.FILE_GENERIC_READ) {
			outDesiredAccess |= WinNT.GENERIC_READ;
			genericRead = true;
		}
		if ((outDesiredAccess & WinNT.FILE_GENERIC_WRITE) == WinNT.FILE_GENERIC_WRITE) {
			outDesiredAccess |= WinNT.GENERIC_WRITE;
			genericWrite = true;
		}
		if ((outDesiredAccess & WinNT.FILE_GENERIC_EXECUTE) == WinNT.FILE_GENERIC_EXECUTE) {
			outDesiredAccess |= WinNT.GENERIC_EXECUTE;
			genericExecute = true;
		}
		if ((outDesiredAccess & WinNT.FILE_ALL_ACCESS) == WinNT.FILE_ALL_ACCESS) {
			outDesiredAccess |= WinNT.GENERIC_ALL;
			genericAll = true;
		}

		if (genericRead)
			outDesiredAccess &= ~WinNT.FILE_GENERIC_READ;
		if (genericWrite)
			outDesiredAccess &= ~WinNT.FILE_GENERIC_WRITE;
		if (genericExecute)
			outDesiredAccess &= ~WinNT.FILE_GENERIC_EXECUTE;
		if (genericAll)
			outDesiredAccess &= ~WinNT.FILE_ALL_ACCESS;

		return outDesiredAccess;
	}

	/**
	 * Converts the kernel file creation flags to the win32 flags. Copied from dokan.c
	 *
	 * @param createDisposition
	 * @return integer corresponding to an enum in {@link CreationDisposition}
	 */
	public static int convertCreateDispositionToCreationDispostion(int createDisposition) {
		switch (createDisposition) {
			case FILE_CREATE:
				return CREATE_NEW.getMask();
			case FILE_OPEN:
				return OPEN_EXISTING.getMask();
			case FILE_OPEN_IF:
				return OPEN_ALWAYS.getMask();
			case FILE_OVERWRITE:
				return TRUNCATE_EXISTING.getMask();
			case FILE_SUPERSEDE:
				// The documentation isn't clear on the difference between replacing a file
				// and truncating it.
				// For now we just map it to create/truncate
			case FILE_OVERWRITE_IF:
				return CREATE_ALWAYS.getMask();
			default:
				//TODO: maybe throw an exception
				return 0;
		}
	}
}
