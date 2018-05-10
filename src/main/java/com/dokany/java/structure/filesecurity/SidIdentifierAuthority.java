package com.dokany.java.structure.filesecurity;

import java.util.Arrays;

/**
 * Enumeration of Well known SidIdentifierAuthoritys
 */
public enum SidIdentifierAuthority implements Byteable {

	/**
	 * Specifies the NULL SID authority. It defines only the NULL well-known-SID: S-1-0-0.
	 */
	NULL_SID_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00}),

	/**
	 * Specifies the World SID authority. It only defines the Everyone well-known-SID: S-1-1-0.
	 */
	WORLD_SID_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x01}),

	/**
	 * Specifies the Local SID authority. It defines only the Local well-known-SID: S-1-2-0.
	 */
	LOCAL_SID_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x02}),

	/**
	 * Specifies the Creator SID authority. It defines the Creator Owner, Creator Group, and Creator Owner Server well-known-SIDs: S-1-3-0, S-1-3-1, and S-1-3-2. These SIDs are used as placeholders in an access control list (ACL) and are replaced by the user, group, and machine SIDs of the security principal.
	 */
	CREATOR_SID_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x03}),

	/**
	 * Not used.
	 */
	NON_UNIQUE_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x04}),

	/**
	 * Specifies the Windows NT operating system security subsystem SID authority. It defines all other SIDs in the forest.
	 */
	SECURITY_NT_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x05}),

	/**
	 * Specifies the application package authority. It defines application capability SIDs.
	 */
	SECURITY_APP_PACKAGE_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x0F}),

	/**
	 * Specifies the Mandatory label authority. It defines the integrity level SIDs.
	 */
	SECURITY_MANDATORY_LABEL_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x10}),

	/**
	 * Specifies the Scoped Policy Authority. It defines all other scoped policy SIDs in the forest.
	 */
	SECURITY_SCOPED_POLICY_ID_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x11}),

	/**
	 * Specifies the authentication authority asserting the client’s identity. It defines only the following well-known SIDs: S-1-18-1, and S-1-18-2.
	 */
	SECURITY_AUTHENTICATION_AUTHORITY(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x12});

	private byte[] mask;

	SidIdentifierAuthority(byte[] mask) {
		if (mask.length != 6) {
			//TODO: what should be? padding or truncating?
		} else {
			this.mask = Arrays.copyOf(mask, mask.length);
		}
	}

	@Override
	public byte[] toByteArray() {
		return mask;
	}

	@Override
	public int sizeOfByteArray() {
		return 6;
	}
}
