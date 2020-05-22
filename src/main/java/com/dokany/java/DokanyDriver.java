package com.dokany.java;

import com.dokany.java.constants.MountError;
import com.dokany.java.structure.DeviceOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main class to start and stop Dokany file system.
 */
public final class DokanyDriver {

	private static final Logger LOG = LoggerFactory.getLogger(DokanyDriver.class);
	private final DeviceOptions deviceOptions;
	private final DokanyFileSystem fileSystem;
	private final AtomicBoolean isMounted;

	public DokanyDriver(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem) {

		this.deviceOptions = deviceOptions;
		this.fileSystem = fileSystem;
		this.isMounted = new AtomicBoolean(false);

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
	public synchronized void start() {
		if (!isMounted.get()) {
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
				isMounted.set(true);

			} catch (UnsatisfiedLinkError err) {
				LOG.error("Unable to load dokan driver.", err);
				throw new LibraryNotFoundException(err.getMessage());
			} catch (Throwable e) {
				LOG.warn("Error while mounting", e);
				throw e;
			}
		} else {
			LOG.info("Dokan Device already mounted on {}.", deviceOptions.MountPoint);
		}
	}

	/**
	 * Unmounts the Dokan Device from the mount point given in the mount options.
	 */
	public synchronized void shutdown() {
		LOG.info("Unmounting Dokan device at {}", deviceOptions.MountPoint);
		if (isMounted.get()) {
			isMounted.set(NativeMethods.DokanRemoveMountPoint(deviceOptions.MountPoint));
			if (isMounted.get()) {
				LOG.error("Unable to unmount Dokan device at {}.", deviceOptions.MountPoint);
			}
		} else {
			LOG.info("Dokan Device {} already unmounted.", deviceOptions.MountPoint);
		}
	}

}
