package com.dokany.java;

import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.filesecurity.*;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * TODO: ändere bei sid dass die kennung nicht umgedreht wird!
 */
public class SelfRelativeSecurityDescriptorTest {

	@Test
	public void testControlField() {
		EnumIntegerSet<SecurityDescriptorControlFlag> control = new EnumIntegerSet<>(SecurityDescriptorControlFlag.class);
		control.add(SecurityDescriptorControlFlag.GD, SecurityDescriptorControlFlag.OD, SecurityDescriptorControlFlag.DD, SecurityDescriptorControlFlag.SD);
		ByteBuffer buf = ByteBuffer.allocate(2);

		Assert.assertEquals((43 << 8 + 0) << 16, Integer.reverseBytes(control.toInt()));
		Assert.assertEquals((43 << 8 + 0), Short.reverseBytes((short) control.toInt()));
		Assert.assertArrayEquals(new byte[]{43, 0}, buf.putShort(Short.reverseBytes((short) control.toInt())).array());
	}

	@Test
	public void testSidWithoutSubAuthorities() {
		SecurityIdentifier sid = new SecurityIdentifier(SidIdentifierAuthority.WORLD_SID_AUTHORITY, null);
		Assert.assertArrayEquals(new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01}, sid.toByteArray());
	}

	@Test
	public void testValidEveryoneSid() {
		SecurityIdentifier sid = new SecurityIdentifier(SidIdentifierAuthority.WORLD_SID_AUTHORITY, Collections.singletonList(0));
		Assert.assertArrayEquals(new byte[]{0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00}, sid.toByteArray());
	}

	@Test
	public void testValidBuiltinAdminitstratorsSid() {
		ArrayList<Integer> subAuths = new ArrayList<>(2);
		subAuths.add(32);
		subAuths.add(544);
		SecurityIdentifier sid = new SecurityIdentifier(SidIdentifierAuthority.SECURITY_NT_AUTHORITY, subAuths);
		Assert.assertArrayEquals(new byte[]{0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x20, 0x00, 0x00, 0x00, 0x20, 0x02, 0x00, 0x00}, sid.toByteArray());
	}

	@Test
	public void testSidFromString() {
		SecurityIdentifier sid = SecurityIdentifier.fromString("S-1-5-32-544");
		Assert.assertArrayEquals(new byte[]{0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x20, 0x00, 0x00, 0x00, 0x20, 0x02, 0x00, 0x00}, sid.toByteArray());
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
		SecurityIdentifier sid = SecurityIdentifier.fromString("S-1-1-0");// everyone sid
		//create ace
		AccessAllowedACE allowedACE = new AccessAllowedACE(flags, sid, mask);

		//encoding of byte array: 1.ace type, 2. ace flags, 3.-4.ace size, 5.-8. access mask, 8.-14. sid (everything little endian!
		Assert.assertArrayEquals(getAllowedAccessACE(), allowedACE.toByteArray());
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

		Assert.assertArrayEquals(getAclWithAAAce(), daclRev2WithAccessAllow.toByteArray());
	}

	@Test
	public void testEmptySecurityDescriptor() {
		EnumIntegerSet<SecurityDescriptorControlFlag> flags = new EnumIntegerSet<>(SecurityDescriptorControlFlag.class);
		flags.add(SecurityDescriptorControlFlag.GD, SecurityDescriptorControlFlag.OD, SecurityDescriptorControlFlag.DD, SecurityDescriptorControlFlag.SD);
		Assert.assertArrayEquals(getEmptySecurityDescriptor(), SelfRelativeSecurityDescriptor.createEmptySD(flags).toByteArray());
	}

	@Test
	public void testOwnerAndGroupSD() {
		//control
		EnumIntegerSet<SecurityDescriptorControlFlag> control = new EnumIntegerSet<>(SecurityDescriptorControlFlag.class);
		control.add(SecurityDescriptorControlFlag.SD, SecurityDescriptorControlFlag.DP);
		//owner
		SecurityIdentifier oSid = new SecurityIdentifier(SidIdentifierAuthority.WORLD_SID_AUTHORITY, null);
		//group
		SecurityIdentifier gSid = new SecurityIdentifier(SidIdentifierAuthority.WORLD_SID_AUTHORITY, null);

		EnumIntegerSet<AccessControlEntryFlag> flags = new EnumIntegerSet<AccessControlEntryFlag>(AccessControlEntryFlag.class);
		flags.add(AccessControlEntryFlag.CONTAINER_INHERIT_ACE, AccessControlEntryFlag.OBJECT_INHERIT_ACE);
		//set the mask
		EnumIntegerSet<AccessMask> mask = new EnumIntegerSet<>(AccessMask.class);
		mask.add(AccessMask.GA);
		//create ace
		AccessControlList daclRev2WithAccessAllow = AccessControlList.createDaclRevision2(Collections.singletonList(new AccessAllowedACE(flags, oSid, mask)));

		SelfRelativeSecurityDescriptor sd = SelfRelativeSecurityDescriptor.createSD(control, oSid, gSid, null, daclRev2WithAccessAllow);

		Assert.assertArrayEquals(getSDWithDaclWithAAAce(), sd.toByteArray());
	}

	@Test
	public void testSaclAndDaclSD() {

	}

	@Test
	public void testCompleteSD() {

	}

	private static byte[] getEmptySecurityDescriptor() {
		return new byte[]{
				0x01, //revision
				0x00, //sbz1
				43,// first half of control flag indicating  owner and group and dacl and sacl default
				-128, //first half indicating a self relative sec. desc.
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

	private static byte[] getSidWithoutSubAuth() {
		return new byte[]{0x01, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00};
	}

	private static byte[] getAllowedAccessACE() {
		return new byte[]{
				0x00, // ace Type
				0x03, //ace flags
				0x14, 0x00, //ace size
				0x00, 0x00, 0x00, 0x10, //access mask
				0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00};
	}

	private static byte[] getAclWithAAAce() {
		return concat(new byte[]{0x02, 0x00, 0x18, 0x00, 0x01, 0x00, 0x00, 0x00}, getAllowedAccessACE());
	}

	private static byte[] getSDWithDaclWithAAAce() {
		byte[] tmp = concat(new byte[]{
				0x01, //revision
				0x00, //sbz1
				0x01,// first half of control flag indicating a self relative sec. desc.
				36, //second half indicating sacl defaulted and dacl present
				0x10,
				0x00,
				0x00,
				0x00, //owner offset (this header = 16 Byte)
				0x18,
				0x00,
				0x00,
				0x00, //group offset (header + owner = 16 + 8)
				0x00,
				0x00,
				0x00,
				0x00, //sacl offset (zero)
				0x20,
				0x00,
				0x00,
				0x00, //dacl offset (header +owner +group = 16+8+8)
		}, getSidWithoutSubAuth());
		tmp = concat(tmp, getSidWithoutSubAuth());
		return concat(tmp, getAclWithAAAce());
	}

	private static byte[] concat(byte[] arr1, byte[] arr2) {
		byte[] arr3 = Arrays.copyOf(arr1, arr1.length + arr2.length);
		for (int i = 0; i < arr2.length; i++) {
			arr3[i + arr1.length] = arr2[i];
		}
		return arr3;
	}

}
