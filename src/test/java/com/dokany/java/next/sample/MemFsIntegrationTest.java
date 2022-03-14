package com.dokany.java.next.sample;

import com.dokany.java.next.DokanException;
import com.dokany.java.next.DokanMount;
import com.dokany.java.next.constants.MountOptions;
import com.sun.jna.WString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MemFsIntegrationTest {

	ResourceManager resourceManager;
	MemoryFs memfsSpy;
	Path mountPoint = Path.of("X:\\");
	DokanMount.Mounter mounter;

	@BeforeEach
	public void init() {
		this.resourceManager = new ResourceManager();
		var memfs = new MemoryFs(resourceManager);
		this.memfsSpy = Mockito.spy(memfs);
		this.mounter = DokanMount.create(memfsSpy)
				.withMountOptions(MountOptions.MOUNT_MANAGER | MountOptions.STDERR)
				.withTimeout(3000)
				.withSingleThreaded(true);
	}

	@AfterEach
	public void cleanup() {
		resourceManager.clear();
	}

	@Test
	@DisplayName("After successful file creation, file exists")
	public void testCreatedFileInFileListing() throws DokanException {
		var file = mountPoint.resolve("test.file");
		var interalFilePath = MemoryPath.of("\\test.file");

		try (var mount = mounter.mount(mountPoint)) {
			Assertions.assertDoesNotThrow(() -> Files.createFile(file));
		}

		//We could actually mock the resourceManager here
		Assertions.assertTrue(resourceManager.exists(interalFilePath),"File creation failed");
	}

	@Test
	@DisplayName("After successful dir creation, dir exists")
	public void testCreatedDirInFileListing() throws DokanException {
		var dir = mountPoint.resolve("testDir");
		var interalDirPath = MemoryPath.of("\\testDir");

		try (var mount = mounter.mount(mountPoint)) {
			Assertions.assertDoesNotThrow(() -> Files.createDirectory(dir));
		}

		Assertions.assertTrue(resourceManager.exists(interalDirPath),"Dir creation failed.");
	}

	@Test
	@DisplayName("Non existing file is correctly determined")
	public void testNonExistingFileIsCorrectlyIndicated() throws DokanException {
		var resource = mountPoint.resolve("test.resource");

		final boolean result;
		try (var mount = mounter.mount(mountPoint)) {
			result = Files.notExists(resource);
		}
		Assertions.assertTrue(result, "Filesystem reports existence of non-existing resource");
	}

	@Test
	@DisplayName("Existing file is correctly indicated")
	public void testExistenceOfExistingFile() throws DokanException {
		var file = mountPoint.resolve("test.file");
		var internalFilePath = MemoryPath.ROOT.resolve("test.file");
		resourceManager.put(internalFilePath, new File("test.file", 0));

		final boolean result;
		try (var mount = mounter.mount(mountPoint)) {
			result = Files.exists(file);
		}
		Assertions.assertTrue(result, "MemFs indicates existing file as non-existing");
	}

	@Test
	@DisplayName("Existing dir is correctly indicated")
	public void testExistenceOfExistingDir() throws DokanException {
		var dir = mountPoint.resolve("testDir");
		var internalFilePath = MemoryPath.ROOT.resolve("testDir");
		resourceManager.put(internalFilePath, new Directory("testDir", 0));

		final boolean result;
		try (var mount = mounter.mount(mountPoint)) {
			result = Files.exists(dir);
		}
		Assertions.assertTrue(result, "MemFs indicates existing dir as non-existing");
	}

	@Test
	@DisplayName("Empty root folder throws exception")
	public void testEmptyRootDirListing() throws DokanException, IOException {
		final List<Path> result;
		//see https://github.com/dokan-dev/dokany/issues/1077
		try (var mount = mounter.mount(mountPoint)) {
			Assertions.assertThrows(NoSuchFileException.class, () -> Files.list(mountPoint).collect(Collectors.toList()));
		}
	}

	@Test
	@DisplayName("Empty non-root dir returns empty listing")
	public void testEmptyNonRootDirListing() throws DokanException, IOException {
		resourceManager.put(MemoryPath.of("\\bar"),new Directory("bar",0x00));
		var path = mountPoint.resolve("bar");
		final List<Path> result;
		try (var mount = mounter.mount(mountPoint)) {
			result = Files.list(path).collect(Collectors.toList());
		}

		Assertions.assertTrue(result.isEmpty(),"Listing of empty non-root dir contains unexpected elements");
	}

	@Test
	@DisplayName("Existing resources appear in dir listing")
	public void testDirListing() throws DokanException, IOException {
		resourceManager.put(MemoryPath.of("\\foo"),new File("foo",0x00));
		resourceManager.put(MemoryPath.of("\\bar"),new Directory("bar",0x00));

		final List<Path> result;
		try (var mount = mounter.mount(mountPoint)) {
			result = Files.list(mountPoint).collect(Collectors.toList());
		}

		Assertions.assertEquals(2,result.size());
		Assertions.assertTrue(result.stream().anyMatch(p -> p.endsWith("foo")));
		Assertions.assertTrue(result.stream().anyMatch(p -> p.endsWith("bar")));
	}

}
