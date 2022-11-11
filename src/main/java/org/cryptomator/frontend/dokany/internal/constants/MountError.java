package org.cryptomator.frontend.dokany.internal.constants;

import org.cryptomator.frontend.dokany.internal.DokanyUtils;

public enum MountError implements EnumInteger {

	SUCCESS(0, "Dokan mount succeeded"),
	DOKAN_ERROR(-1, "Dokan mount error"),
	DRIVE_LETTER_ERROR(-2, "Dokan mount failed - Bad drive letter."),
	DRIVER_INSTALL_ERROR(-3, "Dokan mount failed - Cannot install driver."),
	START_ERROR(-4, "Dokan mount failed - Driver answer that something is wrong."),
	MOUNT_ERROR(-5, "Dokan mount failed - Cannot assign a drive letter or mount point. Probably already used by another volume."),
	MOUNT_POINT_ERROR(-6, "Dokan mount failed - Mount point is invalid."),
	VERSION_ERROR(-7, "Dokan mount failed - Requested an incompatible version.");

	private final int mask;
	private final String description;

	public static MountError fromInt(final int value) {
		return DokanyUtils.enumFromInt(value, values());
	}

	@SuppressWarnings("all")
	private MountError(final int mask, final String description) {
		this.mask = mask;
		this.description = description;
	}

	@SuppressWarnings("all")
	public int getMask() {
		return this.mask;
	}

	@SuppressWarnings("all")
	public String getDescription() {
		return this.description;
	}
}
