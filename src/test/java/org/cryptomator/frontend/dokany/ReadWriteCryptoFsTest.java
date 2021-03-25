package org.cryptomator.frontend.dokany;

import org.cryptomator.cryptofs.CryptoFileSystem;
import org.cryptomator.cryptofs.CryptoFileSystemProperties;
import org.cryptomator.cryptofs.CryptoFileSystemProvider;
import org.slf4j.impl.SimpleLogger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class ReadWriteCryptoFsTest {

	static {
		System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "trace");
		System.setProperty(SimpleLogger.LOG_FILE_KEY, "System.out");
		System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
		System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, "HH:mm:ss:SSS");
	}

	public static void main(String[] args) throws IOException, DokanyMountFailedException {
		if (!MountFactory.isApplicable()) {
			System.err.println("Dokany not installed.");
			return;
		}

		final Path vaultPath;
		final String vaultPassword;
		final Path mountPoint;
		Optional<String> testVaultPathProp = Optional.ofNullable(System.getProperty("TestVaultPath"));
		Optional<String> testVaultPasswordProp = Optional.ofNullable(System.getProperty("TestVaultPassword"));
		Optional<String> testMountPoint = Optional.ofNullable(System.getProperty("TestMountPoint"));
		if (testVaultPathProp.isPresent() && testVaultPasswordProp.isPresent() && testMountPoint.isPresent()) {
			vaultPath = Path.of(testVaultPathProp.get());
			vaultPassword = testVaultPasswordProp.get();
			mountPoint = Path.of(testMountPoint.get());
		} else {
			System.out.println("At least one of the properties \"TestVaultPath\" or \"TestVaultPassword\" is not set. Switching to manual mode.");
			try (Scanner scanner = new Scanner(System.in)) {
				System.out.println("Enter path to the vault you want to access:");
				vaultPath = Path.of(scanner.nextLine());
				System.out.println("Enter the password for the vault:");
				vaultPassword = scanner.nextLine();
				System.out.println("Enter path where vault is mounted:");
				mountPoint = Path.of(scanner.nextLine());
			}
		}

		CryptoFileSystemProperties props = CryptoFileSystemProperties.withPassphrase(vaultPassword)
				.withMasterkeyFilename("masterkey.cryptomator")
				.build();
		CryptoFileSystem cryptofs = CryptoFileSystemProvider.newFileSystem(vaultPath, props);
		Path path = cryptofs.getPath("/");
		MountFactory mountFactory = new MountFactory(Executors.newCachedThreadPool());
		try (Mount mount = mountFactory.mount(path, mountPoint, "MyVault", "CryptoFS")) {
			try {
				mount.reveal(new WindowsExplorerRevealer());
			} catch (Exception e) {
				System.out.println("Unable to reveal.");
				e.printStackTrace();
			}
			System.in.read();
		}
	}

}
