package org.cryptomator.frontend.dokany;

import com.dokany.java.next.DokanAPI;
import com.dokany.java.next.DokanMount;
import com.dokany.java.next.constants.MountOptions;
import org.cryptomator.frontend.dokany.locks.LockManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class ReadOnlyMirrorTest {

	public static void main(String[] args) throws IOException {
		Path root = Path.of("T:\\txt");

		LockManager lm = new LockManager();
		var fs = new MinimalReadOnlyAdapter(root, lm);

		var mount = DokanMount.create(fs);

		try {
			mount.mount(Path.of("X:\\"), MountOptions.MOUNT_MANAGER | MountOptions.STDERR | MountOptions.DISPATCH_DRIVER_LOGS | MountOptions.DEBUG, 10000);
		} catch (InterruptedException e) {
			mount.unmount();
			e.printStackTrace();
			return;
		}

		var reader = new BufferedReader(new InputStreamReader(System.in));
		char exit = ' ';
		while (!Character.isAlphabetic(exit)) {
			try {
				exit = (char) reader.read();
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}

		mount.unmount();

		DokanAPI.DokanShutdown();
	}

}
