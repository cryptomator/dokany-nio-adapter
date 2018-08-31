package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Mount implements AutoCloseable {

	private static final Logger LOG = LoggerFactory.getLogger(Mount.class);
	private static final int REVEAL_TIMEOUT_MS = 5000;
	private static final int UNMOUNT_TIMEOUT_MS = 5000;

	private final DokanyDriver driver;
	private final Path mountPoint;
	private final ProcessBuilder revealCommand;

	private Future<?> driverJob;

	public Mount(Path mountPoint, DokanyDriver driver) {
		this.driver = driver;
		this.mountPoint = mountPoint;
		this.revealCommand = new ProcessBuilder("explorer", "/root,", mountPoint.toString());
	}

	public void mount(ExecutorService executorService) throws ExecutionException, InterruptedException {
		this.driverJob = executorService.submit(driver::start);
		try {
			driverJob.get(3000, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			LOG.trace("Mounting still in progress.");
		}
	}

	public boolean reveal() {
		try {
			Process proc = revealCommand.start();
			boolean finishedInTime = proc.waitFor(REVEAL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			if (finishedInTime) {
				// The check proc.exitValue() == 0 is always false since Windows explorer return every time an exit value of 1
				return true;
			} else {
				proc.destroyForcibly();
				return false;
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return false;
		} catch (IOException e) {
			LOG.error("Failed to reveal drive.", e);
			return false;
		}
	}

	@Override
	public void close() {
		try {
			LOG.debug("Unmounting drive {}: ...", mountPoint);
			driver.shutdown();
			driverJob.get(UNMOUNT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			LOG.debug("Unmounted drive {}: successfully.", mountPoint);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			LOG.error("Dokany driver terminated abnormally.", e);
		} catch (TimeoutException e) {
			LOG.warn("Dokany driver will be canceled now...");
		} finally {
			if (driverJob.cancel(true)) {
				LOG.warn("Dokany driver for drive {}: canceled.", mountPoint);
			}
		}
	}
}
