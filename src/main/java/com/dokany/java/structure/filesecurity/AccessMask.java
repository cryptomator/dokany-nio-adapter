package com.dokany.java.structure.filesecurity;

import com.dokany.java.constants.EnumInteger;

/**
 * Enumeration of the possible AccesMask options.
 * For more info see the <a href="https://msdn.microsoft.com/en-us/library/cc230294.aspx"> Microsoft Documentation</a>.
 */
public enum AccessMask implements EnumInteger {
	/**
	 * GENERIC_READ
	 * When used in an Access Request operation: When read access to an object is requested, this bit is translated to a combination of bits. These are most often set in the lower 16 bits of the ACCESS_MASK. (Individual protocol specifications MAY specify a different configuration.) The bits that are set are implementation dependent. During this translation, the GR bit is cleared. The resulting ACCESS_MASK bits are the actual permissions that are checked against the ACE structures in the security descriptor that attached to the object.
	 * <p>
	 * When used to set the Security Descriptor on an object: When the GR bit is set in an ACE that is to be attached to an object, it is translated into a combination of bits, which are usually set in the lower 16 bits of the ACCESS_MASK. (Individual protocol specifications MAY specify a different configuration.) The bits that are set are implementation dependent. During this translation, the GR bit is cleared. The resulting ACCESS_MASK bits are the actual permissions that are granted by this ACE.
	 */
	GR(0x80000000L),


	/**
	 * GENERIC_WRITE
	 * When used in an Access Request operation: When write access to an object is requested, this bit is translated to a combination of bits, which are usually set in the lower 16 bits of the ACCESS_MASK. (Individual protocol specifications MAY specify a different configuration.) The bits that are set are implementation dependent. During this translation, the GW bit is cleared. The resulting ACCESS_MASK bits are the actual permissions that are checked against the ACE structures in the security descriptor that attached to the object.
	 * <p>
	 * When used to set the Security Descriptor on an object: When the GW bit is set in an ACE that is to be attached to an object, it is translated into a combination of bits, which are usually set in the lower 16 bits of the ACCESS_MASK. (Individual protocol specifications MAY specify a different configuration.) The bits that are set are implementation dependent. During this translation, the GW bit is cleared. The resulting ACCESS_MASK bits are the actual permissions that are granted by this ACE.
	 */
	GW(0x4000000L),


	/**
	 * GENERIC_EXECUTE
	 * <p>
	 * When used in an Access Request operation: When execute access to an object is requested, this bit is translated to a combination of bits, which are usually set in the lower 16 bits of the ACCESS_MASK. (Individual protocol specifications MAY specify a different configuration.) The bits that are set are implementation dependent. During this translation, the GX bit is cleared. The resulting ACCESS_MASK bits are the actual permissions that are checked against the ACE structures in the security descriptor that attached to the object.
	 * <p>
	 * When used to set the Security Descriptor on an object: When the GX bit is set in an ACE that is to be attached to an object, it is translated into a combination of bits, which are usually set in the lower 16 bits of the ACCESS_MASK. (Individual protocol specifications MAY specify a different configuration.) The bits that are set are implementation dependent. During this translation, the GX bit is cleared. The resulting ACCESS_MASK bits are the actual permissions that are granted by this ACE.
	 */
	GX(0x20000000L),


	/**
	 * GENERIC_ALL
	 * When used in an Access Request operation: When all access permissions to an object are requested, this bit is translated to a combination of bits, which are usually set in the lower 16 bits of the ACCESS_MASK. (Individual protocol specifications MAY specify a different configuration.) Objects are free to include bits from the upper 16 bits in that translation as required by the objects semantics. The bits that are set are implementation dependent. During this translation, the GA bit is cleared. The resulting ACCESS_MASK bits are the actual permissions that are checked against the ACE structures in the security descriptor that attached to the object.
	 * <p>
	 * When used to set the Security Descriptor on an object: When the GA bit is set in an ACE that is to be attached to an object, it is translated into a combination of bits, which are usually set in the lower 16 bits of the ACCESS_MASK. (Individual protocol specifications MAY specify a different configuration.) Objects are free to include bits from the upper 16 bits in that translation, if required by the objects semantics. The bits that are set are implementation dependent. During this translation, the GA bit is cleared. The resulting ACCESS_MASK bits are the actual permissions that are granted by this ACE.
	 */
	GA(0x10000000L),


	/**
	 * MAXIMUM_ALLOWED
	 * <p>
	 * When used in an Access Request operation: When requested, this bit grants the requestor the maximum permissions allowed to the object through the Access Check Algorithm. This bit can only be requested; it cannot be set in an ACE.
	 * <p>
	 * When used to set the Security Descriptor on an object: Specifying the Maximum Allowed bit in the SECURITY_DESCRIPTOR has no meaning. The MA bit SHOULD NOT be set and SHOULD be ignored when part of a SECURITY_DESCRIPTOR structure.
	 */
	MA(0x02000000L),


	/**
	 * ACCESS_SYSTEM_SECURITY
	 * When used in an Access Request operation: When requested, this bit grants the requestor the right to change the SACL of an object. This bit MUST NOT be set in an ACE that is part of a DACL. When set in an ACE that is part of a SACL, this bit controls auditing of accesses to the SACL itself.
	 */
	AS(0x01000000L),


	/**
	 * SYNCHRONIZE
	 * <p>
	 * Specifies access to the object sufficient to synchronize or wait on the object.
	 */
	SY(0x00100000L),


	/**
	 * WRITE_OWNER
	 * Specifies access to change the owner of the object as listed in the security descriptor.
	 */
	WO(0x00080000L),


	/**
	 * WRITE_DACL
	 * Specifies access to change the discretionary access control list of the security descriptor of an object.
	 */
	WD(0x00040000L),


	/**
	 * READ_CONTROL
	 * Specifies access to read the security descriptor of an object.
	 */
	RC(0x00020000L),


	/**
	 * DELETE
	 * Specifies access to delete an object.
	 */
	DE(0x00010000L);

	private int mask;

	AccessMask(long mask) {
		this.mask = (int) mask;
	}


	@Override
	public int getMask() {
		return mask;
	}
}
