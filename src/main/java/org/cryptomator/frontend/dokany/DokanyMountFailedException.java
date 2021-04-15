package org.cryptomator.frontend.dokany;

public class DokanyMountFailedException extends Exception {

	public DokanyMountFailedException(String message) {
		super(message);
	}

	public DokanyMountFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public DokanyMountFailedException(Throwable cause) {
		super(cause);
	}
}
