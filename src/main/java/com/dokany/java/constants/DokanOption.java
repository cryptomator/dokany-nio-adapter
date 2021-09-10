package com.dokany.java.constants;

import com.dokany.java.DokanyUtils;
import com.dokany.java.structure.EnumIntegerSet;

public enum DokanOption implements EnumInteger {
	DEBUG_MODE(1, "Enable ouput debug message"),
	STD_ERR_OUTPUT(2, "Enable ouput debug message to stderr"),
	ALT_STREAM(4, "Use alternate stream"),
	WRITE_PROTECTION(8, "Enable mount drive as write-protected"),
	NETWORK_DRIVE(16, "Use network drive - Dokan network provider need to be installed"),
	REMOVABLE_DRIVE(32, "Use removable drive"),
	MOUNT_MANAGER(64, "Use mount manager"),
	CURRENT_SESSION(128, "Mount the drive on current session only"),
	FILELOCK_USER_MODE(256, "Enable Lockfile/Unlockfile operations. Otherwise Dokan will take care of it"),
	ENABLE_NOTIFICATION_API(512,"Enable DokanNotify-API"),
	ENABLE_FCB_GARBAGE_COLLECTION(2048,"Prevents exponentially slow down certain operations caused by filter drivers."),
	CASE_SENSITIVE(4096,"Enables case sensitivity"),
	ENABLE_UNMOUNT_NETWORK_DRIVE(8192,"Allows unmounting network drives via explorer"),
	DISPATCH_DRIVER_LOGS(16384,"Forward driver and volume logs to userland");

	private final int mask;
	private final String description;

	DokanOption(final int i, final String desc) {
		mask = i;
		description = desc;
	}

	public static EnumIntegerSet<DokanOption> fromInt(final int value) {
		return DokanyUtils.enumSetFromInt(value, values());
	}

	public int getMask() {
		return this.mask;
	}

	public String getDescription() {
		return this.description;
	}

}
