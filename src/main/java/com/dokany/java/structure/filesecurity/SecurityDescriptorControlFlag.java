package com.dokany.java.structure.filesecurity;

import com.dokany.java.constants.EnumInteger;

public enum SecurityDescriptorControlFlag implements EnumInteger {

	/**
	 * Self-Relative
	 * Set when the security descriptor is in self-relative format. Cleared when the security descriptor is in absolute format.
	 */
	SR(1 << 0),

	/**
	 *
	 */
	RM(1 << 1),
	PS(1 << 2),
	PD(1 << 3),
	SI(1 << 4),
	DI(1 << 5),
	SC(1 << 6),
	DC(1 << 7),
	SS(1 << 8),
	DT(1 << 9),
	SD(1 << 10),
	SP(1 << 11),
	DD(1 << 12),
	DP(1 << 13),
	GD(1 << 14),
	OD(1 << 15);

	private final int mask;

	SecurityDescriptorControlFlag(int mask) {
		this.mask = mask;
	}

	@Override
	public int getMask() {
		return mask;
	}
}
