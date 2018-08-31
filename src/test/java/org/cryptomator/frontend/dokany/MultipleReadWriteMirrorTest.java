package org.cryptomator.frontend.dokany;

import org.slf4j.impl.SimpleLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MultipleReadWriteMirrorTest {

	static {
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "trace");
		System.setProperty(SimpleLogger.LOG_FILE_KEY, "System.out");
		System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
		System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "HH:mm:ss:SSS");
	}

	public static void main(String[] args) throws IOException, MountFailedException {
		if (!MountFactory.isApplicable()) {
			System.err.println("Dokany not installed.");
			return;
		}

		int numOfVolumes = 5;
		Path root = Paths.get("D:\\Arbeit\\Skymatic\\tmp");

		createDirectories(root, numOfVolumes);


		Set<Character> driveLetters;
		try (IntStream stream = IntStream.rangeClosed('F', 'X')) {
			driveLetters = stream.mapToObj(i -> (char) i).collect(Collectors.toSet());
		}


		Mount[] mounts = new Mount[numOfVolumes];
		Iterator<Character> itDriveLetters = driveLetters.iterator();
		try {
			for (int volumeIndex = 0; volumeIndex < numOfVolumes; volumeIndex++) {
				Path path = root.resolve(Integer.toString(volumeIndex));
				MountFactory mountFactory = new MountFactory(Executors.newCachedThreadPool());
				mounts[volumeIndex] = mountFactory.mount(path, Paths.get(itDriveLetters.next() + ":\\"), "Test" + volumeIndex, "Mirror FS");
			}
			System.in.read();
		} finally {
			for (int volumeIndex = 0; volumeIndex < numOfVolumes; volumeIndex++) {
				mounts[volumeIndex].close();
			}
		}

		removeDirectories(root, numOfVolumes);
	}

	private static void createDirectories(Path root, int numOfVolumes) throws IOException {
		for (int i = 0; i < numOfVolumes; i++) {
			Files.createDirectory(root.resolve(Integer.toString(i)));
		}
	}

	private static void removeDirectories(Path root, int numOfVolumes) throws IOException {
		for (int i = 0; i < numOfVolumes; i++) {
			Files.deleteIfExists(root.resolve(Integer.toString(i)));
		}
	}
}
