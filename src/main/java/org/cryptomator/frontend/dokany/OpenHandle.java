package org.cryptomator.frontend.dokany;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

public abstract class OpenHandle implements Closeable {

	protected final Path path;

	public OpenHandle(Path path) {
		this.path = path;
	}

	public abstract void close() throws IOException;

}
