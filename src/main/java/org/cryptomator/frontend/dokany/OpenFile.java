package org.cryptomator.frontend.dokany;

import com.google.common.base.MoreObjects;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

public class OpenFile extends OpenHandle {

	private static final Logger LOG = LoggerFactory.getLogger(OpenFile.class);
	private static final int BUFFER_SIZE = 4096;

	private final FileChannel channel;


	public OpenFile(Path path, FileChannel channel) {
		super(path);
		this.channel = channel;
	}

	/**
	 * Reads up to {@code num} bytes beginning at {@code offset} into {@code buf}
	 *
	 * @param buf Buffer
	 * @param num Number of bytes to read
	 * @param offset Position of first byte to read
	 * @return Actual number of bytes read (can be less than {@code num} if reached EOF).
	 * @throws IOException If an exception occurs during read.
	 */
	public synchronized int read(Pointer buf, int num, long offset) throws IOException {
		if (offset >= channel.size()) {
			return 0;
		} else {
			ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
			int pos = 0;
			channel.position(offset);
			do {
				int remaining = num - pos;
				int read = readNext(bb, remaining);
				if (read == -1) {
					return pos; // reached EOF
				} else {
					LOG.trace("Reading {}-{} ({}-{})", offset + pos, offset + pos + read, offset, offset + num);
					buf.write(pos, bb.array(), 0, read);
					pos += read;
				}
			} while (pos < num);
			return pos;
		}
	}

	/**
	 * Writes up to {@code num} bytes from {@code buf} into the current file beginning at {@code offset}
	 *
	 * @param buf Buffer
	 * @param num Number of bytes to write
	 * @param offset Position of first byte to write at
	 * @return Actual number of bytes written
	 * TODO: only the bytes which contains information or also some filling zeros?
	 * @throws IOException If an exception occurs during write.
	 */
	public synchronized int write(Pointer buf, int num, long offset) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
		int written = 0;
		channel.position(offset);
		do {
			int remaining = num - written;
			bb.clear();
			int len = (int) Math.min(remaining, bb.capacity());
			buf.read(written, bb.array(), 0, len);
			bb.limit(len);
			channel.write(bb); // TODO check return value
			written += len;
		} while (written < num);
		return written;
	}

	public int append(Pointer buf, int num) throws IOException {
		return write(buf, num, channel.size());
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

	/**
	 * Test if we can delete this file.
	 *
	 * @return <code>true</code> if no concurrent routine holds a lock on this file or the file is opened as read-only
	 * @implNote Attempts to acquire an exclusive lock (and immediately releases it upon success)
	 */
	public boolean canBeDeleted() {
		try (FileLock lock = channel.tryLock()) {
			return lock != null; // we could acquire the exclusive lock, i.e. nobody else is accessing this channel
		} catch (NonWritableChannelException e) {
			return true; //channel only opened for reading, so we can't say anything
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(OpenFile.class) //
				.add("path", path) //
				.add("channel", channel) //
				.toString();
	}

	public static OpenFile open(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
		FileChannel channel = FileChannel.open(path, options, attrs);
		return new OpenFile(path, channel);
	}

}
