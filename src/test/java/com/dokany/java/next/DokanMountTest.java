package com.dokany.java.next;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

//TODO: Disable, if Dokany not installed
//TODO: use annotation which checks that both properties exists
public class DokanMountTest {

	private static final Path TEST_DIR;
	private static final Path TEST_DRIVE_LETTER;
	private DokanFileSystem fs = new MinimalDokanAdapter();
	private DokanMount mount;

	static {
		TEST_DIR = Path.of(System.getProperty("dokan.java.testMountpoint"));
		TEST_DRIVE_LETTER = Path.of(System.getProperty("dokan.java.testDriveLetter").trim()); //Workaround due to not parsable if prop ends with \"
	}

	@BeforeAll
	public static void init() {
		Assertions.assertTrue(Files.notExists(TEST_DRIVE_LETTER, LinkOption.NOFOLLOW_LINKS), "Tests cannot run due to test driveletter" + TEST_DRIVE_LETTER + " is occupied");
		Assertions.assertTrue(Files.exists(TEST_DIR, LinkOption.NOFOLLOW_LINKS), "Tests cannot run due to test mountpoint " + TEST_DIR + " does no exist");
	}


	@Test
	public void testMountToDirSuccessful() throws InterruptedException, DokanException {
		var fsSpy = Mockito.spy(fs);
		this.mount = DokanMount.create(fsSpy)
				.mount(TEST_DIR);
		mount.unmount();

		Mockito.verify(fsSpy, Mockito.times(1)).mounted(Mockito.any(), Mockito.any());
		Mockito.verify(fsSpy, Mockito.times(1)).unmounted(Mockito.any());
	}

	@Test
	public void testMountToDriveSuccessful() throws DokanException {
		var fsSpy = Mockito.spy(fs);
		this.mount = DokanMount.create(fsSpy)
				.mount(TEST_DRIVE_LETTER);
		mount.unmount();

		Mockito.verify(fsSpy, Mockito.times(1)).mounted(Mockito.any(), Mockito.any());
		Mockito.verify(fsSpy, Mockito.times(1)).unmounted(Mockito.any());
	}

}
