package com.dokany.java;

public class StreamInfo {

	public String name;
	public long length;

	public StreamInfo(String name, long length) {
		this.name = name;
		this.length = length;
	}

	/**
	 *
	 * @return Win32FindStreamData containing the length and file name
	 */
	Win32FindStreamData toStruct() {
		Win32FindStreamData out = new Win32FindStreamData();
		out.length(length);
		name.getChars(0, name.length(), out.cFileName(), 0);
		return out;
	}
}
