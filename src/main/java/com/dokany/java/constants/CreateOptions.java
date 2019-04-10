package com.dokany.java.constants;

import com.dokany.java.DokanyUtils;
import com.dokany.java.structure.EnumIntegerSet;

/**
 * https://docs.microsoft.com/en-us/windows-hardware/drivers/ddi/content/wdm/nf-wdm-zwcreatefile
 */
public enum CreateOptions implements EnumInteger {
    FILE_DIRECTORY_FILE(0x00000001),
    FILE_WRITE_THROUGH(0x00000002),
    FILE_SEQUENTIAL_ONLY(0x00000004),
    FILE_NO_INTERMEDIATE_BUFFERING(0x00000008),
    FILE_SYNCHRONOUS_IO_ALERT(0x00000010),
    FILE_SYNCHRONOUS_IO_NONALERT(0x00000020),
    FILE_NON_DIRECTORY_FILE(0x00000040),
    FILE_CREATE_TREE_CONNECTION(0x00000080),

    FILE_COMPLETE_IF_OPLOCKED(0x00000100),
    FILE_NO_EA_KNOWLEDGE(0x00000200),
    FILE_OPEN_REMOTE_INSTANCE(0x00000400),
    FILE_RANDOM_ACCESS(0x00000800),

    FILE_DELETE_ON_CLOSE(0x00001000),
    FILE_OPEN_BY_FILE_ID(0x00002000),
    FILE_OPEN_FOR_BACKUP_INTENT(0x00004000),
    FILE_NO_COMPRESSION(0x00008000);

    private final int mask;

    CreateOptions(final int i) {
        mask = i;
    }

    public static EnumIntegerSet<CreateOptions> fromInt(final int value) {
        return DokanyUtils.enumSetFromInt(value, values());
    }

    @Override
    public int getMask() {
        return mask;
    }

}
