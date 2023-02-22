package org.cryptomator.frontend.dokany.internal.structure.filesecurity;

import org.cryptomator.frontend.dokany.internal.structure.EnumIntegerSet;

public abstract class AccessControlEntry implements Byteable {

	protected final AccessControlEntryType type;

	protected final EnumIntegerSet<AccessControlEntryFlag> flags;

	protected AccessControlEntry(AccessControlEntryType type, EnumIntegerSet<AccessControlEntryFlag> flags) {
		this.type = type;
		this.flags = flags;
	}

	@Override
	public abstract byte[] toByteArray();

	@Override
	public abstract int sizeOfByteArray();

}
