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
	 * @param buffer Buffer to fill
	 * @param offset Position of first byte to read
	 * @return Actual number of bytes read (can be less than {@code num} if reached EOF).
	 * @throws IOException If an exception occurs during read.
	 */
	public synchronized int read(ByteBuffer buffer, long offset) throws IOException {
		return channel.read(buffer,offset);
	}

	/**
	 * Writes the given buffer into the channel at the offset position
	 * @param buffer buffer to write from
	 * @param offset position in the channel of the first byte to write
	 * @return
	 * @throws IOException
	 */
	public synchronized int write(ByteBuffer buffer, long offset) throws IOException {
		return channel.write(buffer, offset);
	}

	/**
	 * Append this buffer at the end of the channel
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	public int append(ByteBuffer buffer) throws IOException {
		return channel.write(buffer, channel.size());
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
