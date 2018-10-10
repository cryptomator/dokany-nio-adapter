package org.cryptomator.frontend.dokany.locks;

public interface PathLockBuilder {

	PathLock forReading();

	PathLock forWriting();

}
