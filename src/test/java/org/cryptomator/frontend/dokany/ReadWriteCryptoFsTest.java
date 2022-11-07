package org.cryptomator.frontend.dokany;

import org.cryptomator.cryptofs.CryptoFileSystem;
import org.cryptomator.cryptofs.CryptoFileSystemProperties;
import org.cryptomator.cryptofs.CryptoFileSystemProvider;
import org.cryptomator.cryptolib.common.MasterkeyFileAccess;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Scanner;

public class ReadWriteCryptoFsTest {

	static {
		System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");
		System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
		System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "HH:mm:ss.SSS");
	}

	public static void main(String[] args) throws IOException, DokanyMountFailedException, NoSuchAlgorithmException {
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

		SecureRandom csprng = SecureRandom.getInstanceStrong();
		CryptoFileSystemProperties props = CryptoFileSystemProperties.cryptoFileSystemProperties()
				.withKeyLoader(uri -> new MasterkeyFileAccess(new byte[0], csprng).load(vaultPath.resolve("masterkey.cryptomator"), vaultPassword))
				.build();
		CryptoFileSystem cryptofs = CryptoFileSystemProvider.newFileSystem(vaultPath, props);
		Path path = cryptofs.getPath("/");
		try (Mount mount = MountFactory.mount(path, mountPoint, "MyVault", "CryptoFS")) {
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
