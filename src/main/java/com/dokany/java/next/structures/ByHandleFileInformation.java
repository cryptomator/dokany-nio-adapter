package com.dokany.java.next.structures;

import com.dokany.java.next.nativeannotations.EnumSet;
import com.dokany.java.next.nativeannotations.Unsigned;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase.FILETIME;

@Structure.FieldOrder({"dwFileAttributes", "ftCreationTime", "ftLastAccessTime", "ftLastWriteTime", "dwVolumeSerialNumber", "nFileSizeHigh", "nFileSizeLow", "nNumberOfLinks", "nFileIndexHigh", "nFileIndexLow"})
public class ByHandleFileInformation extends Structure {

	@EnumSet
	public int dwFileAttributes;
	public FILETIME ftCreationTime;
	public FILETIME ftLastAccessTime;
	public FILETIME ftLastWriteTime;
	@Unsigned
	public int dwVolumeSerialNumber;
	@Unsigned
	public int nFileSizeHigh;
	@Unsigned
	public int nFileSizeLow;
	@Unsigned
	public int nNumberOfLinks;
	@Unsigned
	public int nFileIndexHigh;
	@Unsigned
	public int nFileIndexLow;

	public long getDwFileAttributes() {
		return Integer.toUnsignedLong(dwFileAttributes);
	}

	public long getDwVolumeSerialNumber() {
		return Integer.toUnsignedLong(dwVolumeSerialNumber);
	}

	public long getnFileSizeHigh() {
		return Integer.toUnsignedLong(nFileSizeHigh);
	}

	public long getnFileSizeLow() {
		return Integer.toUnsignedLong(nFileSizeLow);
	}

	public long getnNumberOfLinks() {
		return Integer.toUnsignedLong(nNumberOfLinks);
	}

	public long getnFileIndexHigh() {
		return Integer.toUnsignedLong(nFileIndexHigh);
	}

	public long getnFileIndexLow() {
		return Integer.toUnsignedLong(nFileIndexLow);
	}
}
