package com.dokany.java;

import com.sun.jna.CallbackThreadInitializer;

public class DokanCallbackThreadInitializer extends CallbackThreadInitializer {

	private static final String PREFIX = "DOKAN";

	public DokanCallbackThreadInitializer(int i) {
		super(true, false, PREFIX + i);
	}

}
