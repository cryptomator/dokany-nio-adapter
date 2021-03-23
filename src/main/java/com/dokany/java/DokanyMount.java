package com.dokany.java;

import com.dokany.java.constants.MountError;
import com.dokany.java.structure.DeviceOptions;
import org.cryptomator.frontend.dokany.Mount;
import org.cryptomator.frontend.dokany.Revealer;
import org.cryptomator.frontend.dokany.SafeUnmountCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Main class to start and stop Dokany file system.
 */
public final class DokanyMount implements Mount {

	private static final Logger LOG = LoggerFactory.getLogger(DokanyMount.class);

	private final DeviceOptions deviceOptions;
	private final DokanyFileSystem fileSystem;
	private final SafeUnmountCheck unmountCheck;

	private volatile boolean isMounted;
	private volatile CompletableFuture mountFuture;

	public DokanyMount(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem) {
		this(deviceOptions, fileSystem, () -> true);
	}

	public DokanyMount(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem, SafeUnmountCheck unmountCheck) {
		this.deviceOptions = deviceOptions;
		this.fileSystem = fileSystem;
		this.mountFuture = CompletableFuture.failedFuture(new IllegalStateException("Not mounted."));
		this.isMounted = false;
		this.unmountCheck = unmountCheck;
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
	 * Calls {@link NativeMethods#DokanMain(DeviceOptions, DokanyOperations)} in a thread of the Common ForkJoin Pool.
	 * Adds to the JVM a {@link java.lang.Runtime#addShutdownHook(Thread)} which calls {@link #close()}
	 */
	public synchronized void mount() throws DokanyException {
		this.mount(ForkJoinPool.commonPool());
	}

	/**
	 * Calls {@link NativeMethods#DokanMain(DeviceOptions, DokanyOperations)} on the given executor.
	 * Adds to the JVM a {@link java.lang.Runtime#addShutdownHook(Thread)} which calls {@link #close()}
	 *
	 * @Param Executor executor
	 */
	public synchronized void mount(Executor executor) throws DokanyException {
		if (!isMounted) {
			try {
				Runtime.getRuntime().addShutdownHook(new Thread(this::close));

				LOG.info("Dokany API/driver version: {} / {}", getVersion(), getDriverVersion());
				//real mount op
				mountFuture = CompletableFuture
						.supplyAsync(() -> NativeMethods.DokanMain(deviceOptions, new DokanyOperationsProxy(fileSystem)), executor)
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
				throw new DokanyException(err);
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
	public synchronized void close() {
		if (isMounted) {
			LOG.info("Unmounting Dokan device at {}", deviceOptions.MountPoint);
			boolean unmounted = NativeMethods.DokanRemoveMountPoint(deviceOptions.MountPoint);
			if (unmounted) {
				setIsMounted(false);
			} else {
				LOG.error("Unable to unmount Dokan device at {}. Use dokanctl.exe to unmount", deviceOptions.MountPoint);
			}
		}
	}


	/**
	 * Unmounts the filessystem, if it is safe.
	 *
	 * @throws IllegalStateException if it is currently not possible to unmount the filesytsem.
	 */
	@Override
	public void unmount() throws IllegalStateException {
		if (unmountCheck.safeUnmountPossible()) {
			close();
		} else {
			throw new IllegalStateException("There are handles to files or directories open.");
		}
	}

	/**
	 * Unmounts the filesystem, no matter what.
	 */
	@Override
	public void unmountForced() {
		close();
	}


	@Override
	public void reveal(Revealer revealer) throws Exception {
		revealer.reveal(Path.of(deviceOptions.MountPoint.toString()));
	}

}
