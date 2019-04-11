package org.cryptomator.frontend.dokany;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class OpenHandleFactory implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(OpenHandleFactory.class);

	private final ConcurrentMap<Long, OpenHandle> openHandles = new ConcurrentHashMap<>();
	private final HandleGenerator handleGen = new HandleGenerator();

	/**
	 * @param path
	 * @return
	 */
	public long openDir(Path path) throws IOException {
		long dirHandle = handleGen.getAndIncrement();
		openHandles.putIfAbsent(dirHandle, new OpenDirectory(path));
		return dirHandle;
	}

	/**
	 * @param path path of the file to open
	 * @param options file open options
	 * @return file handle used to identify and close open files.
	 * @throws IOException
	 */
	public long openFile(Path path, OpenOption... options) throws IOException {
		return this.openFile(path, Sets.newHashSet(options));
	}

	/**
	 * Creates an OpenFile with the given path  and options &amp; attributes and assigns a fileHandle != 0 to it
	 *
	 * @param path path of the file to open
	 * @param options file open options
	 * @param attrs file attributes to set when opening
	 * @return file handle used to identify and close open files.
	 * @throws IOException
	 */
	public long openFile(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
		long fileHandle = handleGen.getAndIncrement();
		openHandles.put(fileHandle, OpenFile.open(path, options, attrs));
		return fileHandle;
	}

	/**
	 * Creates an OpenRestrictedFile with the given path and assigns a fileHandle != 0 to it.
	 *
	 * @param path path of the file to open
	 * @return file handle used to identify and close open files.
	 * @throws IOException
	 */
	public long openRestrictedFile(Path path) {
		long fileHandle = handleGen.getAndIncrement();
		openHandles.put(fileHandle, OpenRestrictedFile.open(path));
		return fileHandle;
	}

	public OpenHandle get(Long fileHandle) {
		return openHandles.get(fileHandle);
	}

	public boolean exists(Long handle) {
		return openHandles.containsKey(handle);
	}

	/**
	 * Closes the channel identified by the given fileHandle
	 *
	 * @param handle file handle used to identify
	 * @throws ClosedChannelException If no channel for the given fileHandle has been found.
	 * @throws IOException
	 */
	public void close(long handle) throws ClosedChannelException, IOException {
		OpenHandle object = openHandles.remove(handle);
		if (object != null) {
			object.close();
		} else {
			throw new ClosedChannelException();
		}
	}

	/**
	 * Closes all currently open files.
	 * Calling this method will not prevent the factory to open new files, i.e. this method can be called multiple times and is not idempotent.
	 *
	 * @throws IOException
	 */
	@Override
	public synchronized void close() throws IOException {
		IOException exception = new IOException("At least one open handle could not be closed.");
		for (Iterator<Map.Entry<Long, OpenHandle>> it = openHandles.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<Long, OpenHandle> entry = it.next();
			OpenHandle openHandle = entry.getValue();
			LOG.warn("Closing unclosed handle {}", openHandle);
			try {
				openHandle.close();
			} catch (IOException e) {
				exception.addSuppressed(e);
			}
			it.remove();
		}
		if (exception.getSuppressed().length > 0) {
			throw exception;
		}
	}

	/**
	 * Generates handle ids excluding the id 0 which is used as a special value.
	 */
	private class HandleGenerator {

		private final AtomicLong handleGen = new AtomicLong(1);

		public long getAndIncrement() {
			long handle = handleGen.getAndIncrement();
			if (handle == 0) {
				handle = handleGen.getAndIncrement();
			}
			return handle;
		}
	}
}
