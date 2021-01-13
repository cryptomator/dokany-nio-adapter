package org.cryptomator.frontend.dokany;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface Mount extends AutoCloseable {

	@Override
	void close();

	void unmount();

	void unmountForced();

	void reveal(Consumer<Path> revealer);

}
