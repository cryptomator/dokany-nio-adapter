package org.cryptomator.frontend.dokan;

import com.dokany.java.structure.FileData;
import com.google.common.base.MoreObjects;
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
	 * @param offset Position of first byte to read
	 * @param num Number of bytes to read
	 * @throws IOException If an exception occurs during read.
	 */
	public synchronized FileData read(long offset, int num) throws IOException {
		long size = channel.size();
		byte[] data = new byte[num];
		if (offset >= size) {
			return new FileData(new byte[]{},0);
		} else {
			ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
			int pos = 0;
			channel.position(offset);
			do {
				long remaining = num - pos;
				int read = readNext(bb, remaining);
				if (read == -1) {
					//reached EOF
					return new FileData(data,pos); // reached EOF TODO: wtf cast
				} else {
					LOG.trace("Reading {}-{} ({}-{})", offset + pos, offset + pos + read, offset, offset + num);
					byte [] readings = bb.array();
					for(int i=0;i<read;i++){
						data[pos+i] = readings[i];
					}
					pos += read;
				}
			} while (pos < num);
			return new FileData(data, pos); // TODO wtf cast
		}
	}

	/**
	 * Writes up to {@code num} bytes from {@code buf} from {@code offset} into the current file
	 *
	 * @param offset Position of first byte to write at
	 * @param num Number of bytes to write
	 * @return Actual number of bytes written
	 *         TODO: only the bytes which contains information or also some filling zeros?
	 * @throws IOException If an exception occurs during write.
	 */
	public synchronized int write(long offset, int num) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
		long written = 0;
		channel.position(offset);
		do {
			long remaining = num - written;
			bb.clear();
			int len = (int) Math.min(remaining, bb.capacity());
			//buf.get(written, bb.array(), 0, len);
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
