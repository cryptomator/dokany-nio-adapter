package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
	private final char driveLetter;
	private final Future<?> driverJob;
	private final ProcessBuilder revealCommand;

	public Mount(ExecutorService executorService, char driveLetter, DokanyDriver driver) {
		this.driver = driver;
		this.driveLetter = driveLetter;
		this.driverJob = executorService.submit(driver::start);
		this.revealCommand = new ProcessBuilder("explorer", "/root,", driveLetter + ":\\");
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
			LOG.debug("Unmounting drive {}: ...", driveLetter);
			driver.shutdown();
			driverJob.get(UNMOUNT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			LOG.debug("Unmounted drive {}: successfully.", driveLetter);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			LOG.error("Dokany driver terminated abnormally.", e);
		} catch (TimeoutException e) {
			LOG.warn("Dokany driver will be canceled now...");
		} finally {
			if (driverJob.cancel(true)) {
				LOG.warn("Dokany driver for drive {}: canceled.", driveLetter);
			}
		}
	}
}
