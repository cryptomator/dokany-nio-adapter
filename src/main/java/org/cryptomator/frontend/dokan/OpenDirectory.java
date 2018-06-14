package org.cryptomator.frontend.dokan;

import java.nio.file.Path;

public class OpenDirectory extends OpenHandle {

	public OpenDirectory(Path path) {
		super(path);
	}

	@Override
	public void close() {}

	@Override
	public boolean isRegularFile() {
		return false;
	}
}
