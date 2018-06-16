package com.dokany.java.constants;

import com.dokany.java.DokanyUtils;
import com.sun.jna.platform.win32.WinNT;

/**
 * Enum encoding the CreationDisposition-values for {@link com.dokany.java.DokanyFileSystem}
 * When the file ...
 * This argument:           |             Exists            Does not exist
 * -------------------------+------------------------------------------------------
 * CREATE_ALWAYS            |            Truncates             Creates
 * CREATE_NEW         +-----------+        Fails               Creates
 * OPEN_ALWAYS     ===| does this |===>    Opens               Creates
 * OPEN_EXISTING      +-----------+        Opens                Fails
 * TRUNCATE_EXISTING        |            Truncates              Fails
 * <p>
 * see also in the  FileManagement documentation <a href="https://msdn.microsoft.com/en-us/library/aa363858%28VS.85%29.aspx">CreateFile</a>)
 */
public enum CreationDisposition implements EnumInteger {
    CREATE_NEW(WinNT.CREATE_NEW, "Create New"),
    CREATE_ALWAYS(WinNT.CREATE_ALWAYS, "Create Always"),
    OPEN_EXISTING(WinNT.OPEN_EXISTING, "Open Existing"),
    OPEN_ALWAYS(WinNT.OPEN_ALWAYS, "Open Always"),
    TRUNCATE_EXISTING(WinNT.TRUNCATE_EXISTING, "Truncate Existing");

    private final int mask;
    private final String description;
    private final boolean isReadonly;

    CreationDisposition(final int i, final String desc) {
        mask = i;
        description = desc;
        // TODO: Is this logic correct?
        isReadonly = ((mask == 3) || (mask == 4));
    }

    public static CreationDisposition fromInt(final int value) {
        return DokanyUtils.enumFromInt(value, values());
    }

    @SuppressWarnings("all")
    public int getMask() {
        return this.mask;
    }

    @SuppressWarnings("all")
    public String getDescription() {
        return this.description;
    }

    @SuppressWarnings("all")
    public boolean isReadonly() {
        return this.isReadonly;
    }
}
