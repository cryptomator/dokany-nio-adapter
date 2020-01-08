package com.dokany.java;

import com.dokany.java.constants.MountError;
import com.dokany.java.structure.DeviceOptions;
import com.sun.jna.WString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class to start and stop Dokany file system.
 */
public final class DokanyDriver {

	private static final Logger LOG = LoggerFactory.getLogger(DokanyDriver.class);
	private final DeviceOptions deviceOptions;
	private final DokanyFileSystem fileSystem;

	public DokanyDriver(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem) {

		this.deviceOptions = deviceOptions;
		this.fileSystem = fileSystem;

		LOG.info("Dokany version: {}", getVersion());
		LOG.info("Dokany driver version: {}", getDriverVersion());
	}

	/**
	 * Get driver version.
	 *
	 * @return
	 * @see {@link NativeMethods#DokanDriverVersion()}
	 */
	public long getDriverVersion() {
		return NativeMethods.DokanDriverVersion();
	}

	/**
	 * Get Dokany version.
	 *
	 * @return
	 * @see {@link NativeMethods#DokanVersion()}
	 */
	public long getVersion() {
		return NativeMethods.DokanVersion();
	}

	/**
	 * Get file system.
	 *
	 * @return
	 */

	public DokanyFileSystem getFileSystem() {
		return fileSystem;
	}

	/**
	 * Calls {@link NativeMethods#DokanMain(DeviceOptions, DokanyOperations)}. Has {@link java.lang.Runtime#addShutdownHook(Thread)} which calls {@link #shutdown()}
	 */
	public void start() {
		try {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					shutdown();
				}
			});

			int mountStatus = NativeMethods.DokanMain(deviceOptions, new DokanyOperationsProxy(fileSystem));

			if (mountStatus < 0) {
				throw new IllegalStateException(MountError.fromInt(mountStatus).getDescription());
			}

		} catch (UnsatisfiedLinkError err) {
			LOG.error("Unable to find dokany driver.", err);
			throw new LibraryNotFoundException(err.getMessage());
		} catch (Throwable e) {
			LOG.warn("Error while mounting", e);
			throw e;
		}
	}

	/**
	 * Calls {@link #stop(String)}.
	 */
	public void shutdown() {
		stop(deviceOptions.MountPoint.toString());
	}

	/**
	 * Calls {@link NativeMethods#DokanUnmount(char)} and {@link NativeMethods#DokanRemoveMountPoint(WString)}
	 *
	 * @param mountPoint
	 */
	public static void stop(final String mountPoint) {
		LOG.info("Unmount and shutdown: {}", mountPoint);
		NativeMethods.DokanUnmount(mountPoint.charAt(0));
		NativeMethods.DokanRemoveMountPoint(new WString(mountPoint));
	}
}
