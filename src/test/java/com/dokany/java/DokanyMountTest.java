package com.dokany.java;

import com.dokany.java.structure.DeviceOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class DokanyMountTest {

	DokanyFileSystem fs;
	DeviceOptions options;
	DokanyMount mount;

	@BeforeEach
	public void beforeEach() {
		options = Mockito.mock(DeviceOptions.class);
		fs = Mockito.mock(DokanyFileSystem.class);
		mount = new DokanyMount(options, fs);
	}

	@Test
	public void testCheckToChoke() throws DokanyException {
		Assertions.assertAll( //
				() -> Assertions.assertThrows(DokanyException.class, () -> mount.checkToChoke(new Exception(), true)), //
				() -> Assertions.assertThrows(DokanyException.class, () -> mount.checkToChoke(null, false)), //
				() -> Assertions.assertThrows(DokanyException.class, () -> mount.checkToChoke(new Exception(), true)), //
				() -> Assertions.assertDoesNotThrow(() -> mount.checkToChoke(null, true)) //
		);
	}

}
