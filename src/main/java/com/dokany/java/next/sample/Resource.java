package com.dokany.java.next.sample;

import com.dokany.java.next.structures.ByHandleFileInformation;
import com.sun.jna.platform.win32.WinBase;

import java.sql.Date;
import java.time.Instant;

public sealed abstract class Resource permits File,Directory {

	private final static char [] EMTPY_ALT_NAME = new char[] {'\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0','\0'};

	protected String name;
	protected int attributes;
	protected Instant creationTime;
	protected Instant lastAccessTime;
	protected Instant lastModifiedTime;
	protected long size;

	Resource(String name, int attributes, long size, Instant creationTime, Instant lastAccessTime, Instant lastModifiedTime) {
		this.name = name;
		this.attributes = attributes;
		this.size = size;
		this.creationTime = creationTime;
		this.lastAccessTime = lastAccessTime;
		this.lastModifiedTime = lastModifiedTime;
	}

	abstract Type getType();

	//TODO: which constructor is more efficient of Win32FindData?
	public WinBase.WIN32_FIND_DATA toFIND_DATAStruct() {
		var fileSize = toFIND_DATAFileSize(size);
		return new WinBase.WIN32_FIND_DATA(attributes,
				toFiletime(creationTime),
				toFiletime(lastAccessTime),
				toFiletime(lastModifiedTime),
				fileSize.high,
				fileSize.low,
				0,0,
				toFIND_DATAFileName(name),
				EMTPY_ALT_NAME
				);
	};

	public void writeTo(ByHandleFileInformation fileInfoHandle) {
		fileInfoHandle.dwFileAttributes = attributes;
		fileInfoHandle.setFileSize(size);
		fileInfoHandle.ftCreationTime = toFiletime(creationTime);
		fileInfoHandle.ftLastWriteTime = toFiletime(lastModifiedTime);
		fileInfoHandle.ftLastAccessTime = toFiletime(lastAccessTime);
		fileInfoHandle.nNumberOfLinks = 1;
		fileInfoHandle.setFileIndex(System.identityHashCode(this));
		return;
	}

	//TODO: cache filetime objects (are they immutable?)
	private WinBase.FILETIME toFiletime(Instant instant) {
		return new WinBase.FILETIME(Date.from(instant));
	}

	//TODO: cache buffer objects (are they immutable?)
	private char[] toFIND_DATAFileName(String s) {
		char [] buffer =  new char [WinBase.MAX_PATH];
		s.getChars(0,s.length(),buffer,0);
		buffer[s.length()] = '\0';
		return buffer;
	}

	private HighLowFileSize toFIND_DATAFileSize(long size) {
		return new HighLowFileSize(size);
	}

	//-- Getter & Setter

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAttributes() {
		return attributes;
	}

	public void setAttributes(int attributes) {
		this.attributes = attributes;
	}

	public Instant getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Instant creationTime) {
		this.creationTime = creationTime;
	}

	public Instant getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(Instant lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public Instant getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(Instant lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}


	//-- Utility object templates --

	record HighLowFileSize(int high, int low) {

		public HighLowFileSize(long size) {
			 this((int) (size >>> 32),(int) size);
		}
	}

	enum Type {
		DIR,
		FILE;
	}

}
