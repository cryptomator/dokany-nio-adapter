package org.cryptomator.frontend.dokany;

import com.dokany.java.next.DokanAPI;
import com.dokany.java.next.DokanMount;
import com.dokany.java.next.constants.MountOptions;
import org.cryptomator.frontend.dokany.locks.LockManager;

import java.io.IOException;
import java.nio.file.Path;

public class ReadOnlyMirrorTest {

	public static void main(String [] args) throws InterruptedException {
		Path root = Path.of("T:\\txt");

		LockManager lm = new LockManager();
		var fs =new ReadOnlyAdapter(root, lm);

		var mount = DokanMount.create(fs);

		mount.mount(Path.of("X:\\"), MountOptions.MOUNT_MANAGER | MountOptions.STDERR , 10000);

		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mount.unmount();

		DokanAPI.DokanShutdown();
	}

}
