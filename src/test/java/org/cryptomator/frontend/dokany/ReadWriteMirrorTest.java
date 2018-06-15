package org.cryptomator.frontend.dokany;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadWriteMirrorTest {

	public static void main(String[] args) throws IOException {
		System.out.println("Starting Dokany MirrorFS");
		Path path = Paths.get("C:\\Users\\Sebastian\\Desktop\\Dev\\TestVaultOrig");
		try (Mount mount = new MountFactory().mount(path, 'T', "1&1 Tresor", "Cryptomator FS")) {
			System.out.println("Mounted successfully");
			System.in.read();
		}
	}

}
