package com.dokany.java.structure.filesecurity;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

/**
 * Objectoriented implementation of the ACL-structure used in a {@link SecurityDescriptor}.
 * For more information, please read the <a href="https://msdn.microsoft.com/en-us/library/cc230297.aspx">official Microsoft documentation</a>.
 */
public class AccessControlList implements Byteable {

	/**
	 * AclRevision
	 * An unsigned 8-bit value that specifies the revision of the ACL. The only two legitimate forms of ACLs supported for on-the-wire management or manipulation are type 2 and type 4. No other form is valid for manipulation on the wire.
	 */
	private final byte aclRevision;

	/**
	 * Sbz1
	 * An unsigned 8-bit value. This field is reserved and MUST be set to zero.
	 */
	private final byte sbz1 = 0;

	/**
	 * Sbz2
	 * An unsigned 16-bit integer. This field is reserved and MUST be set to zero.
	 */
	private final short sbz2 = 0;

	/**
	 * List of AccessControlEntrys in this ACL
	 */
	private List<AccessControlEntry> aces;

	private AccessControlList(byte aclRevision, List<AccessControlEntry> aces) {
		this.aclRevision = aclRevision;
		this.aces = Collections.emptyList();
		for (AccessControlEntry ace : aces) {
			//check revision number!
		}

	}


	@Override
	public byte[] toByteArray() {
		ByteBuffer buf = ByteBuffer.allocate(sizeOfByteArray());
		buf.put(aclRevision);
		buf.put(sbz1);
		buf.putShort(Short.reverseBytes((short) sizeOfByteArray()));
		buf.putShort(Short.reverseBytes((short) aces.size()));
		buf.putShort(Short.reverseBytes(sbz2));
		for (AccessControlEntry ace : aces) {
			buf.put(ace.toByteArray());
		}
		return buf.array();
	}

	@Override
	public int sizeOfByteArray() {
		return 1 // aclRevision
				+ 1 //sbz1
				+ 2 // aclSize
				+ 2 //ace count
				+ 2 //sbz2
				+ aces.stream().reduce(0, (sum, ace) -> sum + ace.sizeOfByteArray(), (x, y) -> x + y);
	}

	//TODO: is it correct that the rev. number implicates a SACL or DACL?
	public static AccessControlList createSacl(List<AccessControlEntry> aces) {
		return new AccessControlList((byte) 0x04, aces);
	}

	public static AccessControlList createDacl(List<AccessControlEntry> aces) {
		return new AccessControlList((byte) 0x02, aces);
	}
}
