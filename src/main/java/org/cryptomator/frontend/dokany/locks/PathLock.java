package org.cryptomator.frontend.dokany.locks;

public interface PathLock extends AutoCloseable {

	DataLock lockDataForReading();

	DataLock lockDataForWriting();

	@Override
	void close();

}
