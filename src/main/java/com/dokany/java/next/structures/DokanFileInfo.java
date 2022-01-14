package com.dokany.java.next.structures;

import com.dokany.java.next.nativeannotations.Boolean;
import com.dokany.java.next.nativeannotations.Reserved;
import com.dokany.java.next.nativeannotations.Unsigned;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

@Structure.FieldOrder({"context", "dokanContext", "dokanOptions", "processingContext", "processId", "isDirectory", "deleteOnClose", "pagingIo", "synchronousIo", "noCache", "writeToEndOfFile"})
public class DokanFileInfo extends Structure implements Structure.ByReference {

	@Unsigned
	public long context;

	@Reserved
	public long dokanContext;

	public DokanOptions dokanOptions;

	public Pointer processingContext;

	@Unsigned
	public int processId;

	@Boolean
	public byte isDirectory;

	@Boolean
	public byte deleteOnClose;

	@Boolean
	public byte pagingIo;

	@Boolean
	public byte synchronousIo;

	@Boolean
	public byte noCache;

	@Boolean
	public byte writeToEndOfFile;

	public long getProcessId() {
		return Integer.toUnsignedLong(processId);
	}

	public boolean getIsDirectory() {
		return isDirectory !=0;
	}

	public boolean getDeleteOnClose() {
		return deleteOnClose != 0;
	}

	public boolean getPagingIo() {
		return pagingIo != 0;
	}

	public boolean getSynchronousIo() {
		return synchronousIo != 0;
	}

	public boolean getNoCache() {
		return noCache != 0;
	}

	public boolean getWriteToEndOfFile() {
		return writeToEndOfFile != 0;
	}

}
