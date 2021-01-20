package com.dokany.java;

import com.dokany.java.constants.EnumInteger;
import com.dokany.java.structure.EnumIntegerSet;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Utilities to do various operations.
 */
public class DokanyUtils {

	private static final Logger LOG = LoggerFactory.getLogger(DokanyUtils.class);

	private DokanyUtils() {

	}

	public static String trimStrToSize(final String str, final int len) {
		return str.substring(0, Math.min(str.length(), len));
	}

	public static FILETIME getTime(final Date date) {
		return new FILETIME(date);
	}

	public static FILETIME getTime(final long time) {
		return getTime(new Date(time));
	}

	public static FILETIME getCurrentTime() {
		return getTime(new Date());
	}

	/**
	 * Will return an
	 * TODO: can be refactored to the EnumIntegerSet Class
	 *
	 * @param value
	 * @param allEnumValues
	 * @return
	 */
	public static <T extends Enum<T> & EnumInteger> EnumIntegerSet<T> enumSetFromInt(final int value, final T[] allEnumValues) {
		EnumIntegerSet<T> elements = new EnumIntegerSet<>(allEnumValues[0].getDeclaringClass());
		int remainingValues = value;
		for (T current : allEnumValues) {
			int mask = current.getMask();

			if ((remainingValues & mask) == mask) {
				elements.add(current);
				remainingValues -= mask;
			}
		}
		return elements;
	}

	/**
	 * TODO: can be refactored to the EnumIntegerSet Class
	 *
	 * @param value
	 * @param enumValues
	 * @param <T>
	 * @return
	 */
	public static <T extends EnumInteger> T enumFromInt(final int value, final T[] enumValues) {
		for (final T current : enumValues) {
			if (value == current.getMask()) {
				return current;
			}
		}
		throw new IllegalArgumentException("Invalid int value: " + value);
	}

}
