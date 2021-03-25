package com.dokany.java;

public final class DokanyException extends RuntimeException {

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