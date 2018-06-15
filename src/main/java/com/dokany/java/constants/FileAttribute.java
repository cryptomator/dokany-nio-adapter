package com.dokany.java.constants;

import com.dokany.java.DokanyUtils;
import com.dokany.java.structure.EnumIntegerSet;
import com.sun.jna.platform.win32.WinNT;

/**
 * https://msdn.microsoft.com/en-us/library/gg258117(v=vs.85).aspx
 */
public enum FileAttribute implements EnumInteger {
    ARCHIVE(WinNT.FILE_ATTRIBUTE_ARCHIVE),
    COMPRESSED(WinNT.FILE_ATTRIBUTE_COMPRESSED),
    DEVICE(WinNT.FILE_ATTRIBUTE_DEVICE),
    DIRECTORY(WinNT.FILE_ATTRIBUTE_DIRECTORY),
    ENCRYPTED(WinNT.FILE_ATTRIBUTE_ENCRYPTED),
    HIDDEN(WinNT.FILE_ATTRIBUTE_HIDDEN),
    INTEGRITY_STREAM(32768),
    NORMAL(WinNT.FILE_ATTRIBUTE_NORMAL),
    NOT_CONTENT_INDEXED(WinNT.FILE_ATTRIBUTE_NOT_CONTENT_INDEXED),
    NO_SCRUB_DATA(131072),
    OFFLINE(WinNT.FILE_ATTRIBUTE_OFFLINE),
    READONLY(WinNT.FILE_ATTRIBUTE_READONLY),
    REPARSE_POINT(WinNT.FILE_ATTRIBUTE_REPARSE_POINT),
    SPARSE_FILE(WinNT.FILE_ATTRIBUTE_SPARSE_FILE),
    SYSTEM(WinNT.FILE_ATTRIBUTE_SYSTEM),
    TEMPORARY(WinNT.FILE_ATTRIBUTE_TEMPORARY),
    VIRTUAL(WinNT.FILE_ATTRIBUTE_VIRTUAL);

    private final int mask;

    public static EnumIntegerSet<FileAttribute> fromInt(final int value) {
        return DokanyUtils.enumSetFromInt(value, values());
    }

    FileAttribute(final int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return this.mask;
    }
}
