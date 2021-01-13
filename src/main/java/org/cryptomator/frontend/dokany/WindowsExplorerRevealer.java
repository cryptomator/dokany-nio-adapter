package org.cryptomator.frontend.dokany;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * A simple mount revealer which opens the windows explorer.
 * <p>
 * TODO: maybe throw an exception on error.
 */
public class WindowsExplorerRevealer implements Consumer<Path> {

	private static final int REVEAL_TIMEOUT_MS = 5000;

	@Override
	public void accept(Path path) {
		var pb = new ProcessBuilder("explorer", "/root,", path.toString());
		try {
			Process proc = pb.start();
			if (!proc.waitFor(REVEAL_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
				proc.destroyForcibly();
				throw new TimeoutException("Failed to reveal drive: Timeout.");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}
}
