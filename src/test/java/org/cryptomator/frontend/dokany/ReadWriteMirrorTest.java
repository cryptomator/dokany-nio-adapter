package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyDriver;
import com.dokany.java.DokanyFileSystem;
import com.dokany.java.constants.FileSystemFeature;
import com.dokany.java.constants.MountOption;
import com.dokany.java.structure.DeviceOptions;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.FreeSpace;
import com.dokany.java.structure.VolumeInformation;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ReadWriteMirrorTest {

	private static final long TIMEOUT = 1000;

	public static void main(String[] args) throws IOException {
		System.out.println("Starting Dokany MirrorFS");

		String mountPoint = "K:\\";
		final short threadCount = 2;
		EnumIntegerSet mountOptions = new EnumIntegerSet<>(MountOption.class);
		mountOptions.add(MountOption.DEBUG_MODE, MountOption.STD_ERR_OUTPUT, MountOption.REMOVABLE_DRIVE, MountOption.CURRENT_SESSION);
		String uncName = "";
		int timeout = 10000;
		int allocationUnitSize = 4096;
		int sectorSize = 4096;

		DeviceOptions deviceOptions = new DeviceOptions(mountPoint, threadCount, mountOptions, uncName, timeout, allocationUnitSize, sectorSize);

		EnumIntegerSet fsFeatures = new EnumIntegerSet<>(FileSystemFeature.class);
		fsFeatures.add(FileSystemFeature.CASE_PRESERVED_NAMES, FileSystemFeature.CASE_SENSITIVE_SEARCH,
				FileSystemFeature.PERSISTENT_ACLS, FileSystemFeature.SUPPORTS_REMOTE_STORAGE, FileSystemFeature.UNICODE_ON_DISK);

		VolumeInformation volumeInfo = new VolumeInformation(VolumeInformation.DEFAULT_MAX_COMPONENT_LENGTH, "Mirror", 0x98765432, "Dokany MirrorFS", fsFeatures);
		FreeSpace freeSpace = new FreeSpace(200000, 200);

		DokanyFileSystem myFs = new ReadWriteAdapter(Paths.get("Y:\\test"), volumeInfo, freeSpace);
		DokanyDriver dokanyDriver = new DokanyDriver(deviceOptions, myFs);

		int res;
		try {
			res = CompletableFuture
					.supplyAsync(() -> execMount(dokanyDriver))
					.get(TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		System.in.read();
		dokanyDriver.shutdown();
	}

	private static int execMount(DokanyDriver dd) {
		dd.start();
		return 0;
	}

}
