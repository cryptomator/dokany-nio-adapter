package org.cryptomator.frontend.dokan;

import java.io.IOException;
import java.nio.file.Path;

public class OpenDirectory extends OpenHandle {

	public OpenDirectory(Path path) {
		super(path);
	}

	@Override
	public void close() throws IOException {

	}

	@Override
	public boolean isRegularFile() {
		return false;
	}
}
