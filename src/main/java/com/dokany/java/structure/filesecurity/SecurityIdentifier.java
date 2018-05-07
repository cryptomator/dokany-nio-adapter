package com.dokany.java.structure.filesecurity;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Object-oriented implementation of the SID structure.
 * See also <a href=https://msdn.microsoft.com/en-us/library/gg465313.aspx> the microsoft doc</a>.
 */
public class SecurityIdentifier implements Byteable {

	private final byte revision = 0x01;

	private SidIdentifierAuthority sidAuth;

	private List<Integer> subAuthoritys;

	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(sizeOfByteArray());
		buf.put(revision);
		buf.put((byte) subAuthoritys.size()); //TODO: does this have to be reversed?
		buf.put(sidAuth.toByteArray());
		for (Integer subAuth : subAuthoritys) {
			buf.putInt(Integer.reverseBytes(subAuth));
		}
		return buf.array();
	}

	@Override
	public int sizeOfByteArray() {
		return 1 //revision
				+ 1 //subauthority count
				+ 6 //6 bytes of of the authority
				+ 4 * subAuthoritys.size();//each subauthority consists of 4 bytes
	}
}
