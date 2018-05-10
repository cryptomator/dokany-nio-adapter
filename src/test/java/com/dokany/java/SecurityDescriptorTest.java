package com.dokany.java;

import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.filesecurity.SecurityDescriptorControlFlag;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

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
	public void testACL() {

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
