package com.dokany.java.next.sample;

import java.nio.ByteBuffer;

import static com.sun.jna.platform.win32.WinNT.FILE_ATTRIBUTE_NORMAL;

public final class File extends Resource {

	private static final int CAPACITY = 1024; //1 kiBi
	private static final byte [] OVERWRITE = new byte[CAPACITY];

	private ByteBuffer content;

	public File (String name) {
		this(name, FILE_ATTRIBUTE_NORMAL);
	}

	public File (String name, int attributes) {
		super(name, attributes, CAPACITY);
		this.content = ByteBuffer.allocate(CAPACITY); //1 kiBi
	}

	@Override
	public Type getType() {
		return Type.FILE;
	}

	public void wipe() {
		content.clear();
		content.put(OVERWRITE);
		content.clear();
	}

	@Override
	public long getSize() {
		return content.limit();
	}

}
