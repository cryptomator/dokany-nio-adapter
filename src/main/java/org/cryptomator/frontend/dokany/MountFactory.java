package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyDriver;
import com.dokany.java.DokanyFileSystem;
import com.dokany.java.constants.FileSystemFeature;
import com.dokany.java.constants.MountOption;
import com.dokany.java.structure.DeviceOptions;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.VolumeInformation;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MountFactory {

	private static final Logger LOG = LoggerFactory.getLogger(MountFactory.class);
	private static final int MOUNT_TIMEOUT_MS = 5000;
	private static final short THREAD_COUNT = 1;
	private static final EnumIntegerSet<MountOption> MOUNT_OPTIONS = new EnumIntegerSet<>( //
			// MountOption.DEBUG_MODE, //
			// MountOption.STD_ERR_OUTPUT, //
			// MountOption.REMOVABLE_DRIVE, //
			MountOption.CURRENT_SESSION);
	private static final EnumIntegerSet<FileSystemFeature> FILE_SYSTEM_FEATURES = new EnumIntegerSet<>( //
			FileSystemFeature.CASE_PRESERVED_NAMES, //
			FileSystemFeature.CASE_SENSITIVE_SEARCH, //
			// FileSystemFeature.PERSISTENT_ACLS, //
			// FileSystemFeature.SUPPORTS_REMOTE_STORAGE, //
			FileSystemFeature.UNICODE_ON_DISK);
	private static final String UNC_NAME = "";
	private static final int TIMEOUT = 10000;
	private static final int ALLOC_UNIT_SIZE = 4096;
	private static final int SECTOR_SIZE = 4096;

	private final ExecutorService executorService;

	public MountFactory(ExecutorService executorService) {
		this.executorService = executorService;
	}

	/**
	 * Mounts a drive with the given drive letter containing contents of the given path.
	 * This method blocks until the mount succeeds or times out.
	 *
	 * @param fileSystemRoot Path to the directory which will be the content root of the mounted drive.
	 * @param driveLetter The drive letter of the mounted drive.
	 * @param volumeName The name of the drive as shown to the user.
	 * @param fileSystemName The technical file system name shown in the drive properties window.
	 * @return The mount object, regardless if mount succeeded.
	 */
	public Mount mount(Path fileSystemRoot, char driveLetter, String volumeName, String fileSystemName) throws MountFailedException {
		Preconditions.checkArgument(CharMatcher.inRange('B', 'Z').matches(driveLetter), "Invalid drive letter, expecting B-Z.");

		String mountPoint = driveLetter + ":\\";
		DeviceOptions deviceOptions = new DeviceOptions(mountPoint, THREAD_COUNT, MOUNT_OPTIONS, UNC_NAME, TIMEOUT, ALLOC_UNIT_SIZE, SECTOR_SIZE);
		VolumeInformation volumeInfo = new VolumeInformation(VolumeInformation.DEFAULT_MAX_COMPONENT_LENGTH, volumeName, 0x98765432, fileSystemName, FILE_SYSTEM_FEATURES);
		CompletableFuture<Void> mountDidSucceed = new CompletableFuture<>();
		DokanyFileSystem dokanyFs = new ReadWriteAdapter(fileSystemRoot, volumeInfo, mountDidSucceed);
		DokanyDriver dokanyDriver = new DokanyDriver(deviceOptions, dokanyFs);
		LOG.debug("Mounting on drive {}: ...", driveLetter);
		Mount mount = new Mount(driveLetter, dokanyDriver);
		try {
			mount.mount(executorService);
			mountDidSucceed.get(MOUNT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			LOG.debug("Mounted drive {}: successfully.", driveLetter);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			LOG.error("Mounting failed.", e);
			throw new MountFailedException(e);
		} catch (TimeoutException e) {
			LOG.warn("Mounting timed out.");
		}
		return mount;
	}

	public static boolean isApplicable() {
		return System.getProperty("os.name").toLowerCase().contains("windows")
				&& Files.exists(Paths.get("C:\\Windows\\System32\\drivers\\dokan1.sys")); // https://github.com/dokan-dev/dokany/wiki/How-to-package-your-application-with-Dokan#check-for-previous-dokan-installations
	}

}
