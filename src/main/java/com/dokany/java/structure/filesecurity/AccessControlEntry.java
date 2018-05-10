package com.dokany.java.structure.filesecurity;

import com.dokany.java.structure.EnumIntegerSet;

public abstract class AccessControlEntry implements Byteable {

	private final AccessControlEntryType type;

	private final EnumIntegerSet<AccessControlEntryFlag> flags;

	protected AccessControlEntry(AccessControlEntryType type, EnumIntegerSet<AccessControlEntryFlag> flags) {
		this.type = type;
		this.flags = flags;
	}

	@Override
	public abstract byte[] toByteArray();

	@Override
	public abstract int sizeOfByteArray();
}
