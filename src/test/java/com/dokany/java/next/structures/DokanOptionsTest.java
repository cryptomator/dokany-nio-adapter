package com.dokany.java.next.structures;

import com.google.common.base.Strings;
import com.sun.jna.platform.win32.WinNT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;

public class DokanOptionsTest {

	@Nested
	public class BuilderTest {

		//-- Mocks --
		Path mountPoint;

		DokanOptions.Builder builder;

		@BeforeEach
		public void init() {
			mountPoint = Mockito.mock(Path.class);
			Mockito.when(mountPoint.toString()).thenReturn("/foo/bar");
			Mockito.when(mountPoint.toAbsolutePath()).thenReturn(mountPoint);
			builder = new DokanOptions.Builder(mountPoint);
		}

		@Test
		@DisplayName("Setting the volume security descriptor array sets length and byte array correct")
		public void testSettingSecDescriptorSetsArrayAndLength() {
			var securityDescriptorContent = "This is a security descriptor".getBytes(StandardCharsets.UTF_8);

			DokanOptions result = builder.withSecurityDescriptor(new WinNT.SECURITY_DESCRIPTOR(securityDescriptorContent)).build();

			Assertions.assertEquals(securityDescriptorContent.length,result.VolumeSecurityDescriptorLength);
			var resultDescriptor = Arrays.copyOf(result.VolumeSecurityDescriptor,result.VolumeSecurityDescriptorLength);
			Assertions.assertArrayEquals(securityDescriptorContent,resultDescriptor);
		}

	}

}
