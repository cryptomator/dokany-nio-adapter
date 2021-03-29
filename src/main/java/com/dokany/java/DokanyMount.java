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

	public DokanyMount(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem) {
		this(deviceOptions, fileSystem, () -> true);
	}

	public DokanyMount(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem, SafeUnmountCheck unmountCheck) {
		this.deviceOptions = deviceOptions;
		this.fileSystem = fileSystem;
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
	 * No-op if this object is already mounted.
	 * <p>
	 * Additionally a shutdown hook invoking {@link #close()} is registered to the JVM.
	 */
	public void mount() throws DokanyException {
		this.mount(ForkJoinPool.commonPool(), () -> {});
	}


	/**
	 * Calls {@link NativeMethods#DokanMain(DeviceOptions, DokanyOperations)} on the given executor.
	 * No-op if this object is already mounted.
	 * <p>
	 * Additionally a shutdown hook invoking {@link #close()} is registered to the JVM.
	 *
	 * @param executor
	 * @throws DokanyException
	 */
	public void mount(Executor executor) throws DokanyException {
		this.mount(executor, () -> {});
	}


	/**
	 * Calls {@link NativeMethods#DokanMain(DeviceOptions, DokanyOperations)} on the given executor and runs after mount termination a specified action.
	 * No-op if this object is already mounted.
	 * <p>
	 * Additionally a shutdown hook invoking {@link #close()} is registered to the JVM.
	 *
	 * @param executor
	 * @param onDokanExit object with a run() method which is executed after the Dokan process exited
	 */
	public synchronized void mount(Executor executor, Runnable onDokanExit) throws DokanyException {
		if (!isMounted) {
			isMounted = true;
			try {
				Runtime.getRuntime().addShutdownHook(new Thread(this::close));

				LOG.info("Dokany API/driver version: {} / {}", getVersion(), getDriverVersion());
				//real mount op
				CompletableFuture.supplyAsync(() -> NativeMethods.DokanMain(deviceOptions, new DokanyOperationsProxy(fileSystem)), executor)
						.whenComplete((returnVal, throwable) -> {
							isMounted = false;
							onDokanExit.run();
							if (throwable != null) {
								throw new DokanyException(throwable);
							}
							if (returnVal != null && returnVal != 0) {
								throw new DokanyException("Non-Zero return code " + returnVal + ": " + MountError.fromInt(returnVal).getDescription());
							}
						})
						.get(1000, TimeUnit.MILLISECONDS);

				//if the execution reaches this point, device was directly unmounted.
				throw new DokanyException("Mount failed: Volume was instantly unmounted.");
			} catch (UnsatisfiedLinkError err) {
				throw new DokanyException(err);
			} catch (ExecutionException e) {
				if (e.getCause() instanceof DokanyException) {
					throw (DokanyException) e.getCause();
				} else {
					throw new DokanyException(e.getCause());
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (TimeoutException e) {
				//up and running
			}
		} else {
			LOG.debug("Dokan Device already mounted on {}.", deviceOptions.MountPoint);
		}
	}


	/**
	 * Unmounts the Dokan Device from the mount point given in the mount options.
	 */
	public synchronized void close() {
		if (isMounted) {
			LOG.info("Unmounting Dokan device at {}", deviceOptions.MountPoint);
			boolean unmounted = NativeMethods.DokanRemoveMountPoint(deviceOptions.MountPoint);
			if (unmounted) {
				isMounted = false;
			} else {
				LOG.error("Unable to unmount dokan device at {}.", deviceOptions.MountPoint);
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
		if (isMounted) {
			revealer.reveal(Path.of(deviceOptions.MountPoint.toString()));
		} else {
			throw new IllegalStateException("Filesystem not mounted.");
		}
	}

}
