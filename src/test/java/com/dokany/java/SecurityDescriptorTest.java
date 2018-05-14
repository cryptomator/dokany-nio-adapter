package com.dokany.java;

import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.filesecurity.*;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SecurityDescriptorTest {

	@Test
	public void testControlField() {
		EnumIntegerSet<SecurityDescriptorControlFlag> control = new EnumIntegerSet<>(SecurityDescriptorControlFlag.class);
		control.add(SecurityDescriptorControlFlag.SR, SecurityDescriptorControlFlag.GD, SecurityDescriptorControlFlag.OD);
		ByteBuffer buf = ByteBuffer.allocate(2);

		Assert.assertEquals(29360128, Integer.reverseBytes(control.toInt()));
		Assert.assertEquals(448, Short.reverseBytes((short) control.toInt()));
		Assert.assertArrayEquals(new byte[]{0x01, -64}, buf.putShort(Short.reverseBytes((short) control.toInt())).array());
	}

	@Test
	public void testSidWithoutSubAuthorities() {
		SecurityIdentifier sid = new SecurityIdentifier(SidIdentifierAuthority.WORLD_SID_AUTHORITY, null);
		Assert.assertArrayEquals(new byte[]{0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00}, sid.toByteArray());
	}

	@Test
	public void testAccessAllowedACE() {
		//set the flag
		EnumIntegerSet<AccessControlEntryFlag> flags = new EnumIntegerSet<AccessControlEntryFlag>(AccessControlEntryFlag.class);
		flags.add(AccessControlEntryFlag.CONTAINER_INHERIT_ACE, AccessControlEntryFlag.OBJECT_INHERIT_ACE);
		//set the mask
		EnumIntegerSet<AccessMask> mask = new EnumIntegerSet<>(AccessMask.class);
		mask.add(AccessMask.GA);
		//set the sid to world sid resp. everyone
		SecurityIdentifier sid = new SecurityIdentifier(SidIdentifierAuthority.WORLD_SID_AUTHORITY, null);
		//create ace
		AccessAllowedACE allowedACE = new AccessAllowedACE(flags, sid, mask);

		//encoding of byte array: 1.ace type, 2. ace flags, 3.-4.ace size, 5.-8. access mask, 8.-14. sid (everything little endian!
		Assert.assertArrayEquals(new byte[]{0x00, 0x03, 0x10, 0x00, 0x00, 0x00, 0x00, 0x10, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00}, allowedACE.toByteArray());
	}

	@Test
	public void testACL() {
		//test empty DACL rev2
		AccessControlList emptyDaclRev2 = AccessControlList.createDaclRevision2(new ArrayList<>(0));
		Assert.assertArrayEquals(new byte[]{0x02, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00}, emptyDaclRev2.toByteArray());
		//test empty DACL rev4
		AccessControlList emptyDaclRev4 = AccessControlList.createDaclRevision4(new ArrayList<>(0));
		Assert.assertArrayEquals(new byte[]{0x04, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00}, emptyDaclRev4.toByteArray());
		//test empty SACL rev2
		AccessControlList emptySaclRev2 = AccessControlList.createSaclRevision2(new ArrayList<>(0));
		Assert.assertArrayEquals(new byte[]{0x02, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00}, emptySaclRev2.toByteArray());
		//test empty SACL rev4
		AccessControlList emptySaclRev4 = AccessControlList.createSaclRevision4(new ArrayList<>(0));
		Assert.assertArrayEquals(new byte[]{0x04, 0x00, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00}, emptySaclRev4.toByteArray());

		//test DACL rev2 with accessAllowACE
		EnumIntegerSet<AccessControlEntryFlag> flags = new EnumIntegerSet<AccessControlEntryFlag>(AccessControlEntryFlag.class);
		flags.add(AccessControlEntryFlag.CONTAINER_INHERIT_ACE, AccessControlEntryFlag.OBJECT_INHERIT_ACE);
		//set the mask
		EnumIntegerSet<AccessMask> mask = new EnumIntegerSet<>(AccessMask.class);
		mask.add(AccessMask.GA);
		//set the sid to world sid resp. everyone
		SecurityIdentifier sid = new SecurityIdentifier(SidIdentifierAuthority.WORLD_SID_AUTHORITY, null);
		//create ace
		AccessControlList daclRev2WithAccessAllow = AccessControlList.createDaclRevision2(Collections.singletonList(new AccessAllowedACE(flags, sid, mask)));

		Assert.assertArrayEquals(new byte[]{0x02, 0x00, 0x18, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x03, 0x10, 0x00, 0x00, 0x00, 0x00, 0x10, 0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00}, daclRev2WithAccessAllow.toByteArray());
	}

	@Test
	public void testEmptySecurityDescriptor() {

	}

	@Test
	public void testOwnerAndGroupSD() {

	}

	@Test
	public void testSaclAndDaclSD() {

	}

	@Test
	public void testCompleteSD() {

	}

	public static byte[] getEmptySecurityDescriptor() {
		return new byte[]{
				0x01, //revision
				0x00, //sbz1
				0x01,// first half of control flag indicating a self relative sec. desc.
				-64, //second half indicating owner and group default
				0x00,
				0x00,
				0x00,
				0x00, //owner offset
				0x00,
				0x00,
				0x00,
				0x00, //group offset
				0x00,
				0x00,
				0x00,
				0x00, //sacl offset
				0x00,
				0x00,
				0x00,
				0x00, //dacl offset
		};

	}

}
