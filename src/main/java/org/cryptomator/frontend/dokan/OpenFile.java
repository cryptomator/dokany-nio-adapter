package org.cryptomator.frontend.dokan;

import com.google.common.base.MoreObjects;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

public class OpenFile implements Closeable {

	private static final Logger LOG = LoggerFactory.getLogger(OpenFile.class);
	private static final int BUFFER_SIZE = 4096;

	private final Path path;
	private final FileChannel channel;

	public OpenFile(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
		this.path = path;
		this.channel = FileChannel.open(path, options, attrs);
	}

	/**
	 * Reads up to {@code num} bytes beginning at {@code offset} into {@code buf}
	 *
	 * @param buf Buffer
	 * @param num Number of bytes to read
	 * @param offset Position of first byte to read
	 * @return Actual number of bytes read (can be less than {@code size} if reached EOF).
	 * @throws IOException If an exception occurs during read.
	 */
	public synchronized int read(Pointer buf, long num, long offset) throws IOException {
		long size = channel.size();
		if (offset >= size) {
			return 0;
		} else {
			ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
			long pos = 0;
			channel.position(offset);
			do {
				long remaining = num - pos;
				int read = readNext(bb, remaining);
				if (read == -1) {
					return (int) pos; // reached EOF TODO: wtf cast
				} else {
					LOG.trace("Reading {}-{} ({}-{})", offset + pos, offset + pos + read, offset, offset + num);
					buf.write(pos, bb.array(), 0, read);
					pos += read;
				}
			} while (pos < num);
			return (int) pos; // TODO wtf cast
		}
	}

	/**
	 * Writes up to {@code num} bytes from {@code buf} from {@code offset} into the current file
	 *
	 * @param buf Buffer
	 * @param num Number of bytes to write
	 * @param offset Position of first byte to write at
	 * @return Actual number of bytes written
	 * TODO: only the bytes which contains information or also some filling zeros?
	 * @throws IOException If an exception occurs during write.
	 */
	public synchronized int write(Pointer buf, long num, long offset) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
		long written = 0;
		channel.position(offset);
		do {
			long remaining = num - written;
			bb.clear();
			int len = (int) Math.min(remaining, bb.capacity());
			buf.read(written, bb.array(), 0, len);
			bb.limit(len);
			channel.write(bb); // TODO check return value
			written += len;
		} while (written < num);
		return (int) written; // TODO wtf cast
	}

	private int readNext(ByteBuffer readBuf, long num) throws IOException {
		readBuf.clear();
		readBuf.limit((int) Math.min(readBuf.capacity(), num));
		return channel.read(readBuf);
	}

	@Override
	public void close() throws IOException {
		channel.close();
	}

	public void flush() throws IOException {
		channel.force(false);
	}

	public void truncate(long size) throws IOException {
		channel.truncate(size);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(OpenFile.class) //
				.add("path", path) //
				.add("channel", channel) //
				.toString();
	}

}
