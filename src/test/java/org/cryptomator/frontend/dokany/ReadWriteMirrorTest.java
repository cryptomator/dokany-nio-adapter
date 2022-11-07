package org.cryptomator.frontend.dokany;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ReadWriteMirrorTest {

	static {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "HH:mm:ss.SSS");
	}

	public static void main(String[] args) throws IOException, DokanyMountFailedException, InterruptedException {
		if (!MountFactory.isApplicable()) {
			System.err.println("Dokany not installed.");
			return;
		}

		final Path dirPath;
		final Path mountPoint;
		Optional<String> testDirProp = Optional.ofNullable(System.getProperty("TestDir"));
		Optional<String> testMountPoint = Optional.ofNullable(System.getProperty("TestMountPoint"));
		if (testDirProp.isPresent() && testMountPoint.isPresent()) {
			dirPath = Path.of(testDirProp.get());
			mountPoint = Path.of(testMountPoint.get());
		} else {
			try (Scanner scanner = new Scanner(System.in)) {
				System.out.println("Enter path to the directory you want to mirror:");
				dirPath = Path.of(scanner.nextLine());
				System.out.println("Enter path where the mirror is mounted:");
				mountPoint = Path.of(scanner.nextLine());
			}
		}

		CountDownLatch exitSignal = new CountDownLatch(1);
		Consumer<Throwable> onDokanExitAction = exception -> exitSignal.countDown();
		try (Mount mount = MountFactory.mount(dirPath, mountPoint, "Test", "DokanyNioFS", "--thread-count 5", onDokanExitAction)) {
			try {
				mount.reveal(new WindowsExplorerRevealer());
			} catch (Exception e) {
				System.out.println("Unable to reveal.");
				e.printStackTrace();
			}
			System.in.read();
			mount.unmountForced();

		} finally {
			if (exitSignal.await(3000, TimeUnit.MILLISECONDS)) {
				System.out.println("onExit action executed.");
			} else {
				System.out.println("onExit action NOT executed after 3s. Exiting...");
			}
		}

	}

}
