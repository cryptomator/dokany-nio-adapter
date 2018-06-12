package org.cryptomator.frontend.dokan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public abstract class OpenHandle {

	private static final Logger LOG = LoggerFactory.getLogger(OpenFile.class);

	protected final Path path;

	public OpenHandle(Path path) {
		this.path = path;
	}

	public abstract void close() throws IOException;

	public abstract boolean isRegularFile();

}
