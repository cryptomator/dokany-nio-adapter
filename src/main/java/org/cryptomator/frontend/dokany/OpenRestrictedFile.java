package org.cryptomator.frontend.dokany;

import com.sun.jna.Pointer;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Path;

/**
 * A File where no FileChannel to the data is opened. Should be useed, if the file is e.g. invalid but should be deleteable.
 */
public class OpenRestrictedFile extends OpenFile {

	public OpenRestrictedFile(Path path) {
		super(path, null);
	}

	@Override
	public synchronized int read(Pointer buf, int num, long offset) throws IOException {
		throw new FileSystemException("File opened in restricted mode. Read is not supported.");
	}

	@Override
	public synchronized int write(Pointer buf, int num, long offset) throws IOException {
		throw new FileSystemException("File is opened in restricted mode. Write is not supported.");
	}

	@Override
	public void flush() throws IOException {
		throw new FileSystemException("File is opened in restricted mode. Write is not supported.");
	}

	@Override
	public void truncate(long size) throws IOException {
		throw new FileSystemException("File is opened in restricted mode. Write is not supported.");
	}

	@Override
	public boolean canBeDeleted() {
		return true;
	}

	@Override
	public void close() throws IOException {

	}

	public static OpenRestrictedFile open(Path path) {
		return new OpenRestrictedFile(path);
	}
}
