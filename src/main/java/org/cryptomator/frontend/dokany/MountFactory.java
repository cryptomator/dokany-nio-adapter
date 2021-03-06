package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyException;
import com.dokany.java.DokanyFileSystem;
import com.dokany.java.DokanyMount;
import com.dokany.java.constants.DokanOption;
import com.dokany.java.constants.FileSystemFeature;
import com.dokany.java.structure.DeviceOptions;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.VolumeInformation;
import org.apache.commons.cli.ParseException;
import org.cryptomator.frontend.dokany.locks.LockManager;
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
import java.util.function.Consumer;

public class MountFactory {

	private static final Logger LOG = LoggerFactory.getLogger(MountFactory.class);
	private static final short THREAD_COUNT = 5;
	private static final String UNC_NAME = "";
	private static final int TIMEOUT = 10000;
	private static final int ALLOC_UNIT_SIZE = 4096;
	private static final int SECTOR_SIZE = 4096;
	private static final EnumIntegerSet<FileSystemFeature> FILE_SYSTEM_FEATURES = new EnumIntegerSet<>( //
			FileSystemFeature.CASE_PRESERVED_NAMES, //
			FileSystemFeature.CASE_SENSITIVE_SEARCH, //
			// FileSystemFeature.PERSISTENT_ACLS, //
			// FileSystemFeature.SUPPORTS_REMOTE_STORAGE, //
			FileSystemFeature.UNICODE_ON_DISK);

	private MountFactory() {}

	/**
	 * Mounts the root of a filesystem at the given mount point.
	 * This method blocks until the mount succeeds or times out.
	 *
	 * @param fileSystemRoot Path to the directory which will be the content root of the mounted drive.
	 * @param mountPoint The mount point of the mounted drive. Can be an empty directory or a drive letter.
	 * @param volumeName The name of the drive as shown to the user.
	 * @param fileSystemName The technical file system name shown in the drive properties window.
	 * @return The mount object.
	 * @throws DokanyMountFailedException if the mount process is aborted due to errors
	 */
	public static Mount mount(Path fileSystemRoot, Path mountPoint, String volumeName, String fileSystemName) throws DokanyMountFailedException {
		var absMountPoint = mountPoint.toAbsolutePath();
		DeviceOptions deviceOptions = new DeviceOptions(absMountPoint.toString(),
				THREAD_COUNT,
				new EnumIntegerSet<>(DokanOption.class),
				UNC_NAME,
				TIMEOUT,
				ALLOC_UNIT_SIZE,
				SECTOR_SIZE);
		return mount(fileSystemRoot, volumeName, fileSystemName, deviceOptions, ignored -> {});
	}

	/**
	 * Mounts the root of a filesystem at the given mount point with the specified additional mount options.
	 * If an additional mount option is not specified the default value is used.
	 * This method blocks until the mount succeeds or times out.
	 *
	 * @param fileSystemRoot Path to the directory which will be the content root of the mounted drive.
	 * @param mountPoint The mount point of the mounted drive. Can be an empty directory or a drive letter.
	 * @param volumeName The name of the drive as shown to the user.
	 * @param fileSystemName The technical file system name shown in the drive properties window.
	 * @param additionalOptions Additional options for the mount. For any unset option a default value is used. See {@link MountUtil} for details.
	 * @return The mount object.
	 * @throws DokanyMountFailedException if the mount process is aborted due to errors
	 */
	public static Mount mount(Path fileSystemRoot, Path mountPoint, String volumeName, String fileSystemName, String additionalOptions) throws DokanyMountFailedException {
		var absMountPoint = mountPoint.toAbsolutePath();
		var mountOptions = parseMountOptions(additionalOptions);
		DeviceOptions deviceOptions = new DeviceOptions(absMountPoint.toString(),
				mountOptions.getThreadCount().orElse(THREAD_COUNT),
				mountOptions.getDokanOptions(),
				UNC_NAME,
				mountOptions.getTimeout().orElse(TIMEOUT),
				mountOptions.getAllocationUnitSize().orElse(ALLOC_UNIT_SIZE),
				mountOptions.getSectorSize().orElse(SECTOR_SIZE));
		return mount(fileSystemRoot, volumeName, fileSystemName, deviceOptions, ignored -> {});
	}

	/**
	 * Mounts the root of a filesystem at the given mount point with the specified additional mount options and an action which is executed after unmount.
	 * If an additional mount option is not specified the default value is used.
	 * This method blocks until the mount succeeds or times out.
	 *
	 * @param fileSystemRoot Path to the directory which will be the content root of the mounted drive.
	 * @param mountPoint The mount point of the mounted drive. Can be an empty directory or a drive letter.
	 * @param volumeName The name of the drive as shown to the user.
	 * @param fileSystemName The technical file system name shown in the drive properties window.
	 * @param additionalOptions Additional options for the mount. For any unset option a default value is used. See {@link MountUtil} for details.
	 * @param onDokanExit consumer which runs after the dokan mount exited. If the exit was irregularly, the Throwable parameter is not null.
	 * @return The mount object.
	 * @throws DokanyMountFailedException if the mount process is aborted due to errors
	 */
	public static Mount mount(Path fileSystemRoot, Path mountPoint, String volumeName, String fileSystemName, String additionalOptions, Consumer<Throwable> onDokanExit) throws DokanyMountFailedException {
		var absMountPoint = mountPoint.toAbsolutePath();
		var mountOptions = parseMountOptions(additionalOptions);
		DeviceOptions deviceOptions = new DeviceOptions(absMountPoint.toString(),
				mountOptions.getThreadCount().orElse(THREAD_COUNT),
				mountOptions.getDokanOptions(),
				UNC_NAME,
				mountOptions.getTimeout().orElse(TIMEOUT),
				mountOptions.getAllocationUnitSize().orElse(ALLOC_UNIT_SIZE),
				mountOptions.getSectorSize().orElse(SECTOR_SIZE));
		return mount(fileSystemRoot, volumeName, fileSystemName, deviceOptions, onDokanExit);
	}

	private static Mount mount(Path fileSystemRoot, String volumeName, String fileSystemName, DeviceOptions deviceOptions, Consumer<Throwable> onDokanExit) throws DokanyMountFailedException {
		VolumeInformation volumeInfo = new VolumeInformation(VolumeInformation.DEFAULT_MAX_COMPONENT_LENGTH, volumeName, 0x98765432, fileSystemName, FILE_SYSTEM_FEATURES);
		OpenHandleCheck.OpenHandleCheckBuilder handleCheckBuilder = OpenHandleCheck.getBuilder();
		DokanyFileSystem dokanyFs = new ReadWriteAdapter(fileSystemRoot, new LockManager(), volumeInfo, handleCheckBuilder);
		DokanyMount mount = new DokanyMount(deviceOptions, dokanyFs, handleCheckBuilder.build());
		LOG.debug("Mounting on {}: ...", deviceOptions.MountPoint);
		try {
			mount.mount(onDokanExit);
			LOG.debug("Mounted directory at {} successfully.", deviceOptions.MountPoint);
		} catch (DokanyException e) {
			LOG.error("Mounting failed.", e);
			throw new DokanyMountFailedException("Error while mounting.", e);
		}
		return mount;
	}


	private static MountUtil.MountOptions parseMountOptions(String options) throws DokanyMountFailedException {
		try {
			return MountUtil.parse(options);
		} catch (IllegalArgumentException | ParseException e) {
			throw new DokanyMountFailedException("Unable to parse mount options", e);
		}
	}

	public static boolean isApplicable() {
		return System.getProperty("os.name").toLowerCase().contains("windows")
				&& Files.exists(Paths.get("C:\\Windows\\System32\\drivers\\dokan1.sys")); // https://github.com/dokan-dev/dokany/wiki/How-to-package-your-application-with-Dokan#check-for-previous-dokan-installations
	}

}
