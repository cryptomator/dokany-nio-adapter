package com.dokany.java.structure.filesecurity;

import com.dokany.java.structure.EnumIntegerSet;

import java.nio.ByteBuffer;
import java.util.Optional;

public class SecurityDescriptor implements Byteable {

	private static final byte[] EMPTY = new byte[0];
	private final byte revision = (byte) 0x01;

	private final byte sbz1 = (byte) 0x00;

	private EnumIntegerSet<SecurityDescriptorControlFlag> control;

	private Optional<SecurityIdentifier> ownerSid;

	private Optional<SecurityIdentifier> groupSid;

	private Optional<AccessControlList> sacl;

	private Optional<AccessControlList> dacl;

	private SecurityDescriptor() {
	}

	@Override
	public byte[] toByteArray() {
		//do some computations of the size
		ByteBuffer buf = ByteBuffer.allocate(sizeOfByteArray());
		buf.put(revision);
		buf.put(sbz1);
		buf.putShort((short) Integer.reverseBytes(control.toInt()));
		buf.putInt(Integer.reverseBytes(ownerSid.map(SecurityIdentifier::sizeOfByteArray).orElse(0)));
		buf.putInt(Integer.reverseBytes(groupSid.map(SecurityIdentifier::sizeOfByteArray).orElse(0)));
		buf.putInt(Integer.reverseBytes(sacl.map(AccessControlList::sizeOfByteArray).orElse(0)));
		buf.putInt(Integer.reverseBytes(dacl.map(AccessControlList::sizeOfByteArray).orElse(0)));
		buf.put(ownerSid.map(SecurityIdentifier::toByteArray).orElse(EMPTY));
		buf.put(groupSid.map(SecurityIdentifier::toByteArray).orElse(EMPTY));
		buf.put(sacl.map(AccessControlList::toByteArray).orElse(EMPTY));
		buf.put(dacl.map(AccessControlList::toByteArray).orElse(EMPTY));
		return buf.array();
	}

	@Override
	public int sizeOfByteArray() {
		return 2 // the first fixed bytes (revision and sbz1)
				+ 2 // the 16bit big  control mask
				+ 4 * 4 // the 4 32bit integer offset values indicating the offset to the following varaible length data fields
				+ ownerSid.map(SecurityIdentifier::sizeOfByteArray).orElse(0)
				+ groupSid.map(SecurityIdentifier::sizeOfByteArray).orElse(0)
				+ sacl.map(AccessControlList::sizeOfByteArray).orElse(0)
				+ dacl.map(AccessControlList::sizeOfByteArray).orElse(0);
	}
}
