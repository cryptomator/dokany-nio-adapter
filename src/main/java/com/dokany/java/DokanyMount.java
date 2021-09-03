package com.dokany.java;

import com.dokany.java.constants.MountError;
import com.dokany.java.structure.DeviceOptions;
import org.cryptomator.frontend.dokany.Mount;
import org.cryptomator.frontend.dokany.Revealer;
import org.cryptomator.frontend.dokany.SafeUnmountCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Main class to start and stop Dokany file system.
 */
public final class DokanyMount implements Mount {

	private static final Logger LOG = LoggerFactory.getLogger(DokanyMount.class);
	private static final int MOUNT_TIMEOUT_MILLIS = Integer.getInteger("org.cryptomator.frontend.dokany.mountTimeOut",22500);
	private static final AtomicInteger MOUNT_COUNTER = new AtomicInteger(1);

	private final DeviceOptions deviceOptions;
	private final DokanyFileSystem fileSystem;
	private final SafeUnmountCheck unmountCheck;

	private final AtomicBoolean isMounted;

	public DokanyMount(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem) {
		this(deviceOptions, fileSystem, () -> true);
	}

	public DokanyMount(final DeviceOptions deviceOptions, final DokanyFileSystem fileSystem, SafeUnmountCheck unmountCheck) {
		this.deviceOptions = deviceOptions;
		this.fileSystem = fileSystem;
		this.isMounted = new AtomicBoolean(false);
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
	 * Calls {@link NativeMethods#DokanMain(DeviceOptions, DokanyOperations)} in a new thread.
	 * No-op if this object is already mounted.
	 * <p>
	 * Additionally a shutdown hook invoking {@link #close()} is registered to the JVM.
	 */
	public void mount() throws DokanyException {
		this.mount(ignored -> {});
	}

	/**
	 * Calls {@link NativeMethods#DokanMain(DeviceOptions, DokanyOperations)} in a new thread and after DokanMain exit runs the specified action with an throwable as parameter if DokanMain terminiated irregularly.
	 * No-op if this object is already mounted.
	 * <p>
	 * Additionally a shutdown hook invoking {@link #close()} is registered to the JVM.
	 *
	 * @param onDokanExit object with a run() method which is executed after the Dokan process exited
	 */
	public synchronized void mount(Consumer<Throwable> onDokanExit) throws DokanyException {
		if (!isMounted.getAndSet(true)) {
			int mountId = MOUNT_COUNTER.getAndIncrement();
			try {
				Runtime.getRuntime().addShutdownHook(new Thread(this::close));

				LOG.info("Dokany API/driver version: {} / {}", getVersion(), getDriverVersion());

				//real mount op
				CountDownLatch mountSuccessSignal = new CountDownLatch(1);
				AtomicReference<Throwable> exception = new AtomicReference<>();
				var mountThread = new Thread(() -> {
					try {
						int r = NativeMethods.DokanMain(deviceOptions, new DokanyOperationsProxy(mountSuccessSignal, fileSystem, "dokanMount-" + mountId + "-callback-"));
						if (r != 0) {
							throw new DokanyException("DokanMain returned error code" + r + ": " + MountError.fromInt(r).getDescription());
						}
					} catch (Exception e) {
						exception.set(e);
					} finally {
						isMounted.set(false);
						onDokanExit.accept(exception.get());
					}
				});
				mountThread.setName("dokanMount-" + mountId + "-main");
				mountThread.setDaemon(true);
				mountThread.start();

				// wait for mounted() is called, unlocking the barrier
				if (!mountSuccessSignal.await(MOUNT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
					NativeMethods.DokanRemoveMountPoint(deviceOptions.MountPoint);
					if (exception.get() != null) {
						if( exception.get() instanceof DokanyException) {
							throw (DokanyException) exception.get();
						} else {
							throw new DokanyException(exception.get());
						}
					}
					throw new DokanyException("Mount timed out");
				}

			} catch (UnsatisfiedLinkError err) {
				throw new DokanyException(err);
			} catch (InterruptedException e) {
				NativeMethods.DokanRemoveMountPoint(deviceOptions.MountPoint);
				Thread.currentThread().interrupt();
				throw new DokanyException("Mount interrupted.");
			}
		} else {
			LOG.debug("Dokan Device already mounted on {}.", deviceOptions.MountPoint);
		}
	}

	/**
	 * Unmounts the Dokan device from its mount point. No-op if the device is not mounted.
	 */
	public synchronized void close() {
		if (isMounted.get()) {
			LOG.info("Unmounting dokan device at {}", deviceOptions.MountPoint);
			var unmounted = NativeMethods.DokanRemoveMountPoint(deviceOptions.MountPoint);
			if (unmounted) {
				isMounted.set(false);
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
		if (isMounted.get()) {
			revealer.reveal(Path.of(deviceOptions.MountPoint.toString()));
		} else {
			throw new IllegalStateException("Filesystem not mounted.");
		}
	}

}
