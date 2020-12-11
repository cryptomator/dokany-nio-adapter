package org.cryptomator.frontend.dokany;

public interface Mount extends AutoCloseable {

	@Override
	void close();

	void unmount();

	void unmountForced();

	@Deprecated
	boolean reveal();

}
