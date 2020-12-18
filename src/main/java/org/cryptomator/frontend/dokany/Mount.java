package org.cryptomator.frontend.dokany;

import java.nio.file.Path;

public interface Mount extends AutoCloseable {

	@Override
	void close();

	void unmount();

	void unmountForced();

	Path getMountPoint();

}
