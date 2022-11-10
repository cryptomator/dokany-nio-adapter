package org.cryptomator.frontend.dokany.internal;

public final class DokanyException extends Exception {

	public DokanyException(String msg) {
		super(msg);
	}

	public DokanyException(Throwable e) {
		super(e);
	}

	DokanyException(String msg, Throwable cause) {
		super(msg, cause);
	}
}