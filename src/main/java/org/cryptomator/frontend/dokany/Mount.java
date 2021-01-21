package org.cryptomator.frontend.dokany;

public interface Mount extends AutoCloseable {

	@Override
	void close();

	void unmount();

	void unmountForced();

	void reveal(Revealer revealer) throws Exception;

}
