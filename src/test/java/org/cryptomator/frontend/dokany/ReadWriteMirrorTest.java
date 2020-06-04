package org.cryptomator.frontend.dokany;

import org.slf4j.impl.SimpleLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ReadWriteMirrorTest {

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

		final Path dirPath;
		final String vaultPassword;
		final Path mountPoint;
		Optional<String> testDirProp = Optional.ofNullable(System.getProperty("TestDir"));
		Optional<String> testMountPoint = Optional.ofNullable(System.getProperty("TestMountPoint"));
		if (testDirProp.isPresent() && testMountPoint.isPresent()) {
			dirPath = Path.of(testDirProp.get());
			mountPoint = Path.of(testMountPoint.get());
		} else {
			try (Scanner scanner = new Scanner(System.in)) {
				System.out.println("Enter path to the vault you want to access:");
				dirPath = Path.of(scanner.nextLine());
				System.out.println("Enter path where vault is mounted:");
				mountPoint = Path.of(scanner.nextLine());
			}
		}

		MountFactory mountFactory = new MountFactory(Executors.newCachedThreadPool());
		try (Mount mount = mountFactory.mount(dirPath, mountPoint, "Test", "Cryptomator FS")) {
			mount.reveal();
			System.in.read();
			mount.unmount();
		}
	}

}
