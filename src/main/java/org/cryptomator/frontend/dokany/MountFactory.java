package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyDriver;
import com.dokany.java.DokanyFileSystem;
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

public class MountFactory {

	private static final Logger LOG = LoggerFactory.getLogger(MountFactory.class);
	private static final int MOUNT_TIMEOUT_MS = 5000;
	private static final short THREAD_COUNT = 5;
	private static final EnumIntegerSet<DokanOption> DOKAN_OPTIONS = new EnumIntegerSet<>( //
			// DokanOption.DEBUG_MODE, //
			// DokanOption.STD_ERR_OUTPUT, //
			// DokanOption.REMOVABLE_DRIVE, //
			DokanOption.CURRENT_SESSION);
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
	 * Mounts a virtual drive at the given mount point containing contents of the given path.
	 * This method blocks until the mount succeeds or times out.
	 *
	 * @param fileSystemRoot Path to the directory which will be the content root of the mounted drive.
	 * @param mountPoint The mount point of the mounted drive. Can be an empty directory or a drive letter.
	 * @param volumeName The name of the drive as shown to the user.
	 * @param fileSystemName The technical file system name shown in the drive properties window.
	 * @return The mount object.
	 * @throws MountFailedException if the mount process is aborted due to errors
	 */
	public Mount mount(Path fileSystemRoot, Path mountPoint, String volumeName, String fileSystemName) throws MountFailedException {
		Path absMountPoint = mountPoint.toAbsolutePath();
		DeviceOptions deviceOptions = new DeviceOptions(absMountPoint.toString(), THREAD_COUNT, DOKAN_OPTIONS, UNC_NAME, TIMEOUT, ALLOC_UNIT_SIZE, SECTOR_SIZE);
		VolumeInformation volumeInfo = new VolumeInformation(VolumeInformation.DEFAULT_MAX_COMPONENT_LENGTH, volumeName, 0x98765432, fileSystemName, FILE_SYSTEM_FEATURES);
		CompletableFuture<Void> mountDidSucceed = new CompletableFuture<>();
		LockManager lockManager = new LockManager();
		DokanyFileSystem dokanyFs = new ReadWriteAdapter(fileSystemRoot, lockManager, volumeInfo, mountDidSucceed);
		DokanyDriver dokanyDriver = new DokanyDriver(deviceOptions, dokanyFs);
		LOG.debug("Mounting on {}: ...", absMountPoint);
		Mount mount = new Mount(absMountPoint, dokanyDriver);
		try {
			mount.mount(executorService);
			mountDidSucceed.get(MOUNT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			LOG.debug("Mounted directory at {} successfully.", absMountPoint.toString());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			LOG.error("Mounting failed.", e);
			throw new MountFailedException(e.getCause());
		} catch (TimeoutException e) {
			LOG.warn("Mounting timed out.");
		}
		return mount;
	}

	/**
	 * Mounts a virtual drive at the given mount point containing contents of the given path with the specified additional mount options.
	 * If an additional mount option is not specified the default value is used.
	 * This method blocks until the mount succeeds or times out.
	 *
	 * @param fileSystemRoot Path to the directory which will be the content root of the mounted drive.
	 * @param mountPoint The mount point of the mounted drive. Can be an empty directory or a drive letter.
	 * @param volumeName The name of the drive as shown to the user.
	 * @param fileSystemName The technical file system name shown in the drive properties window.
	 * @param additionalOptions String of additional options to overwrite default values. See {@link MountUtil} for details.
	 * @return The mount object.
	 * @throws MountFailedException if the mount process is aborted due to errors
	 */
	public Mount mount(Path fileSystemRoot, Path mountPoint, String volumeName, String fileSystemName, String additionalOptions) throws MountFailedException {
		Path absMountPoint = mountPoint.toAbsolutePath();
		MountUtil.MountOptions options = parseMountOptions(additionalOptions);
		DeviceOptions deviceOptions = new DeviceOptions(absMountPoint.toString(),
				options.getThreadCount().orElse(THREAD_COUNT),
				options.getDokanOptions(),
				UNC_NAME,
				options.getTimeout().orElse(TIMEOUT),
				options.getAllocationUnitSize().orElse(ALLOC_UNIT_SIZE),
				options.getSectorSize().orElse(SECTOR_SIZE));
		VolumeInformation volumeInfo = new VolumeInformation(VolumeInformation.DEFAULT_MAX_COMPONENT_LENGTH, volumeName, 0x98765432, fileSystemName, FILE_SYSTEM_FEATURES);
		CompletableFuture<Void> mountDidSucceed = new CompletableFuture<>();
		LockManager lockManager = new LockManager();
		DokanyFileSystem dokanyFs = new ReadWriteAdapter(fileSystemRoot, lockManager, volumeInfo, mountDidSucceed);
		DokanyDriver dokanyDriver = new DokanyDriver(deviceOptions, dokanyFs);

		LOG.debug("Mounting on {}: ...", absMountPoint);
		Mount mount = new Mount(absMountPoint, dokanyDriver);
		try {
			mount.mount(executorService);
			mountDidSucceed.get(MOUNT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			LOG.debug("Mounted directory at {} successfully.", absMountPoint.toString());
		} catch (
				InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (
				ExecutionException e) {
			LOG.error("Mounting failed.", e);
			throw new MountFailedException(e.getCause());
		} catch (
				TimeoutException e) {
			LOG.warn("Mounting timed out.");
		}
		return mount;
	}

	private MountUtil.MountOptions parseMountOptions(String options) throws MountFailedException {
		try {
			return MountUtil.parse(options);
		} catch (IllegalArgumentException | ParseException e) {
			throw new MountFailedException(e);
		}
	}

	public static boolean isApplicable() {
		return System.getProperty("os.name").toLowerCase().contains("windows")
				&& Files.exists(Paths.get("C:\\Windows\\System32\\drivers\\dokan1.sys")); // https://github.com/dokan-dev/dokany/wiki/How-to-package-your-application-with-Dokan#check-for-previous-dokan-installations
	}

}
