package org.cryptomator.frontend.dokany;

import com.dokany.java.next.DokanAPI;
import com.dokany.java.next.DokanException;
import com.dokany.java.next.DokanMount;
import com.dokany.java.next.constants.MountOptions;
import org.cryptomator.frontend.dokany.locks.LockManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class ReadOnlyMirrorTest {

	public static void main(String[] args) throws IOException, DokanException {
		Path root = Path.of("T:\\txt");

		LockManager lm = new LockManager();
		var fs = new MinimalReadOnlyAdapter(root, lm);

		var reader = new BufferedReader(new InputStreamReader(System.in));
		waitForUserInput(reader);

		var mount = DokanMount.create(fs)
				.withMountOptions(MountOptions.MOUNT_MANAGER | MountOptions.STDERR | MountOptions.DEBUG)
				.mount(Path.of("X:\\"));
		waitForUserInput(reader);
		mount.unmount();

		DokanAPI.DokanShutdown();
	}

	private static void waitForUserInput(BufferedReader reader) throws IOException {
		System.out.println("Please enter ONE Character to continue...");
		char exit = ' ';
		while (!Character.isAlphabetic(exit)) {
			try {
				exit = (char) reader.read();
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

}
