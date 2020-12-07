package org.cryptomator.frontend.dokany;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class MultipleMirrorsTest {

	@TempDir
	static Path SANDBOX; // = Path.of("T:\\sandbox");

	private static int numberOfMounts = 10;
	private static String testDirPrefix = "testDir";

	private ConcurrentLinkedQueue<Mount> mounts;
	private MountFactory mountFactory;
	private ExecutorService mountThreads;

	@BeforeAll
	public static void beforeAll() {
		IntStream.range(0, numberOfMounts)
				.forEach(num -> {
							Path testDir = SANDBOX.resolve(testDirPrefix + num);
							Path testFile = testDir.resolve("yolo" + num + ".txt");
							Path mountPoint = SANDBOX.resolve("mnt" + num);
							try {
								Files.createDirectories(testDir);
								Files.createDirectory(mountPoint);
								Files.writeString(testFile, "Carpe Diem!", StandardOpenOption.CREATE_NEW);
							} catch (IOException e) {
								throw new UncheckedIOException(e);
							}
						}
				);
	}

	@BeforeEach
	public void init() throws IOException {
		this.mounts = new ConcurrentLinkedQueue<>();
		this.mountThreads = Executors.newCachedThreadPool();
		this.mountFactory = new MountFactory(mountThreads);
	}


	@Test
	@EnabledIfDokanyInstalled
	public void testMultipleConcurrentMirrorsFolderMounts() {
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
			for (int i = 0; i < numberOfMounts; i++) {
				var testDir = SANDBOX.resolve(testDirPrefix + i);
				var mountPoint = SANDBOX.resolve("mnt" + i);
				try {
					MountFactory mountFactory = new MountFactory(mountThreads);
					Mount m = mountFactory.mount(testDir, mountPoint, "mnt" + i, "Mirror FS");
					mounts.add(m);
				} catch (MountFailedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Test
	@Disabled
	public void testMultipleConcurrentMirrorsDriveLetterMounts() {
		Assertions.assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
			for (int i = 0; i < numberOfMounts; i++) {
				var testDir = SANDBOX.resolve(testDirPrefix + i);
				char driveLetter = Character.toChars(0x4a + i)[0];
				var mountPoint = Path.of(driveLetter + ":\\");
				try {
					MountFactory mountFactory = new MountFactory(mountThreads);
					Mount m = mountFactory.mount(testDir, mountPoint, "mnt" + i, "Mirror FS");
					mounts.add(m);
				} catch (MountFailedException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@AfterEach
	public void tearDown() throws IOException {
		mounts.forEach(Mount::unmountForced);
	}

	@AfterAll
	public static void afterAll() throws IOException {
		//deleteDir(SANDBOX);
	}

	private static void deleteDir(Path p) throws IOException {
		if (Files.isDirectory(p)) {
			//recurse!
			Files.list(p).forEach(child -> {
				try {
					deleteDir(child);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
		Files.delete(p);
	}


}
