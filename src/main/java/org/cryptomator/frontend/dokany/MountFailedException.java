package org.cryptomator.frontend.dokany;

public class MountFailedException extends Exception{

	public MountFailedException(String message) {
		super(message);
	}

	public MountFailedException(Throwable cause) {
		super(cause);
	}
}
