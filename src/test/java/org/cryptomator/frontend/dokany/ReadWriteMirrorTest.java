package org.cryptomator.frontend.dokany;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class ReadWriteMirrorTest {

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("C:\\Users\\Sebastian\\Desktop\\Dev\\TestVaultOrig");
		MountFactory mountFactory = new MountFactory(Executors.newCachedThreadPool());
		try (Mount mount = mountFactory.mount(path, 'T', "1&1 Tresor", "Cryptomator FS")) {
			mount.reveal();
			System.in.read();
		}
	}

}
