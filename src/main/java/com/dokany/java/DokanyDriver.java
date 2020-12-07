package com.dokany.java;

import com.dokany.java.constants.MountError;
import com.dokany.java.structure.DeviceOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Main class to start and stop Dokany file system.
 */
public final class DokanyDriver {

	private static final Logger LOG = LoggerFactory.getLogger(DokanyDriver.class);
	private final DeviceOptions deviceOptions;
	private final DokanyFileSystem fileSystem;

	private volatile boolean isMounted;
	private volatile CompletableFuture mountFuture;

	public DokanyDriver(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem) {
		this.deviceOptions = deviceOptions;
		this.fileSystem = fileSystem;
		this.mountFuture = CompletableFuture.failedFuture(new IllegalStateException("Not mounted."));
		this.isMounted = false;

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
	public synchronized void start() throws DokanyException {
		if (!isMounted) {
			try {
				Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

				//real mount op
				mountFuture = CompletableFuture
						.supplyAsync(() -> NativeMethods.DokanMain(deviceOptions, new DokanyOperationsProxy(fileSystem)))
						.handle((returnVal, exception) -> {
							setIsMounted(false);
							if (returnVal != null && returnVal.equals(0)) {
								return 0;
							}

							if (returnVal == null) {
								throw new DokanyException(exception);
							} else {
								throw new DokanyException("Return Code " + returnVal + ": " + MountError.fromInt(returnVal).getDescription());
							}
						});

				//check return value
				try {
					mountFuture.get(1000, TimeUnit.MILLISECONDS);
				} catch (TimeoutException e) {
					// up and running
				} catch (ExecutionException e) {
					LOG.error("Error while mounting", e);
					if (e.getCause() instanceof DokanyException) {
						throw (DokanyException) e.getCause();
					} else {
						throw new DokanyException(e);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

				setIsMounted(true);
			} catch (UnsatisfiedLinkError err) {
				LOG.error("Unable to load dokan driver.", err);
				throw new LibraryNotFoundException(err.getMessage());
			}
		} else {
			LOG.debug("Dokan Device already mounted on {}.", deviceOptions.MountPoint);
		}
	}

	public synchronized void setIsMounted(boolean newValue) {
		isMounted = newValue;
	}


	/**
	 * Unmounts the Dokan Device from the mount point given in the mount options.
	 */
	public synchronized void shutdown() {
		LOG.info("Unmounting Dokan device at {}", deviceOptions.MountPoint);
		if (isMounted) {
			boolean unmounted = NativeMethods.DokanRemoveMountPoint(deviceOptions.MountPoint);
			if (unmounted) {
				setIsMounted(false);
			} else {
				LOG.error("Unable to unmount Dokan device at {}.", deviceOptions.MountPoint);
			}
		} else {
			LOG.debug("Dokan Device not mounted at {}.", deviceOptions.MountPoint);
		}
	}

}
