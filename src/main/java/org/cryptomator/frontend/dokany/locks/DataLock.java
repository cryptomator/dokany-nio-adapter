package org.cryptomator.frontend.dokany.locks;

public interface DataLock extends AutoCloseable {

	@Override
	void close();
}
