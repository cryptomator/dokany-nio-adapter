package org.cryptomator.frontend.dokany;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OpenDirectory extends OpenHandle {

	public OpenDirectory(Path path) throws IOException {
		super(path);
		Files.readAttributes(path, "*"); // throws an exception if they can't be read
	}

	@Override
	public void close() {
	}

}
