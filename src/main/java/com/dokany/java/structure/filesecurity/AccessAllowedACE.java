package com.dokany.java.structure.filesecurity;

import com.dokany.java.structure.EnumIntegerSet;

import java.nio.ByteBuffer;

public class AccessAllowedACE extends AccessControlEntry {

	EnumIntegerSet<AccessMask> rights;

	SecurityIdentifier sid;

	protected AccessAllowedACE(EnumIntegerSet<AccessControlEntryFlag> flags) {
		super(AccessControlEntryType.ACCESS_ALLOWED_ACE_TYPE, flags);
	}

	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(sizeOfByteArray());
		buf.put(type.toByteArray());
		buf.putShort(Short.reverseBytes((short) flags.toInt()));
		buf.putShort(Short.reverseBytes((short) sizeOfByteArray()));
		buf.putInt(Integer.reverseBytes(rights.toInt()));
		buf.put(sid.toByteArray());
		return buf.array();
	}

	@Override
	public int sizeOfByteArray() {
		return 1 //type
				+ 1 //flags
				+ 2 //size
				+ 4 //mask
				+ sid.sizeOfByteArray(); //size of sid
	}
}
