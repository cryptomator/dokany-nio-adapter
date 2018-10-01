package org.cryptomator.frontend.dokany;

import org.slf4j.impl.SimpleLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

		Path path = Paths.get("Y:\\test\\");
		Path mountPoint = Paths.get("T:\\");
		MountFactory mountFactory = new MountFactory(Executors.newCachedThreadPool());
		try (Mount mount = mountFactory.mount(path, mountPoint, "Test", "Cryptomator FS")) {
			mount.reveal();
			System.in.read();
		}
	}

}
