package org.cryptomator.frontend.dokany;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A simple mount revealer which opens the windows explorer.
 */
public class WindowsExplorerRevealer implements Revealer {

	private static final int REVEAL_TIMEOUT_MS = 5000;

	@Override
	public void reveal(Path path) throws TimeoutException, IOException {
		var pb = new ProcessBuilder("explorer", "/root,", path.toString());
		try {
			Process proc = pb.start();
			if (!proc.waitFor(REVEAL_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
				proc.destroyForcibly();
				throw new TimeoutException("Failed to reveal drive: Timeout.");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
