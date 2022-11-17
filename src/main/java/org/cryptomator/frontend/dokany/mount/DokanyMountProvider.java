package org.cryptomator.frontend.dokany.mount;

import org.cryptomator.frontend.dokany.internal.Dokany;
import org.cryptomator.frontend.dokany.internal.DokanyException;
import org.cryptomator.frontend.dokany.internal.DokanyFileSystem;
import org.cryptomator.frontend.dokany.internal.DokanyMount;
import org.cryptomator.frontend.dokany.internal.constants.DokanOption;
import org.cryptomator.frontend.dokany.internal.constants.FileSystemFeature;
import org.cryptomator.frontend.dokany.internal.structure.DeviceOptions;
import org.cryptomator.frontend.dokany.internal.structure.EnumIntegerSet;
import org.cryptomator.frontend.dokany.internal.VolumeInformation;
import com.sun.jna.platform.win32.WinNT;
import org.apache.commons.cli.ParseException;
import org.cryptomator.frontend.dokany.adapter.ReadWriteAdapter;
import org.cryptomator.frontend.dokany.locks.LockManager;
import org.cryptomator.integrations.mount.Mount;
import org.cryptomator.integrations.mount.MountBuilder;
import org.cryptomator.integrations.mount.MountCapability;
import org.cryptomator.integrations.mount.MountFailedException;
import org.cryptomator.integrations.mount.MountService;
import org.cryptomator.integrations.mount.Mountpoint;
import org.cryptomator.integrations.mount.UnmountFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.cryptomator.frontend.dokany.internal.constants.FileSystemFeature.CASE_PRESERVED_NAMES;
import static org.cryptomator.frontend.dokany.internal.constants.FileSystemFeature.CASE_SENSITIVE_SEARCH;
import static org.cryptomator.frontend.dokany.internal.constants.FileSystemFeature.UNICODE_ON_DISK;

/**
 * Dokany implementation of the {@link MountService} interface.
 */
public class DokanyMountProvider implements MountService {

	private static final Logger LOG = LoggerFactory.getLogger(DokanyMountProvider.class);

	private static final int SUPPORTED_DRIVER_VERSION = 400;

	private static final EnumIntegerSet<FileSystemFeature> DEFAULT_FS_FEATURES = new EnumIntegerSet(CASE_PRESERVED_NAMES, CASE_SENSITIVE_SEARCH, UNICODE_ON_DISK);

	@Override
	public String displayName() {
		return "Dokany (1.5)";
	}

	@Override
	public boolean isSupported() {
		return Dokany.isInstalled() //
				&& Dokany.apiVersion() >= DeviceOptions.DOKANY_FEATURE_VERSION //
				&& Dokany.driverVersion() >= SUPPORTED_DRIVER_VERSION;
	}

	@Override
	public String getDefaultMountFlags() {
		return "--options CURRENT_SESSION,ENABLE_FCB_GARBAGE_COLLECTION"
				+ " --thread-count 5"
				+ " --timeout 10000";
	}

	@Override
	public Set<MountCapability> capabilities() {
		return Set.of(MountCapability.READ_ONLY, MountCapability.MOUNT_AS_DRIVE_LETTER, MountCapability.MOUNT_TO_EXISTING_DIR, MountCapability.MOUNT_FLAGS, MountCapability.VOLUME_NAME, MountCapability.FILE_SYSTEM_NAME);
	}

	@Override
	public MountBuilder forFileSystem(Path path) {
		return new DokanyMountBuilder(path);
	}

	static class DokanyMountBuilder implements MountBuilder {

		private final Path fsRoot;

		private String fsName = VolumeInformation.DEFAULT_FS_NAME;
		private MountOptionParser.MountOptions mountFlags;
		private Path mountPoint;
		private boolean readOnly = false;
		private String volumeLabel = VolumeInformation.DEFAULT_FS_NAME;

		private DokanyMountBuilder(Path fsRoot) {
			this.fsRoot = fsRoot;
		}

		@Override
		public MountBuilder setFileSystemName(String fileSystemName) {
			//see parameter nFileSystemNameSize of  https://learn.microsoft.com/de-de/windows/win32/api/fileapi/nf-fileapi-getvolumeinformationw#parameters
			if (fsName.length() > WinNT.MAX_PATH) {
				throw new IllegalArgumentException("File system name must be at most 260 characters long");
			}
			this.fsName = fileSystemName;
			return this;
		}

		@Override
		public MountBuilder setMountpoint(Path mountPoint) {
			this.mountPoint = mountPoint;
			return this;
		}

		@Override
		public MountBuilder setMountFlags(String mountFlags) {
			try {
				this.mountFlags = MountOptionParser.parse(mountFlags);
			} catch (ParseException e) {
				throw new IllegalArgumentException("Error parsing mountFlags", e);
			}
			return this;
		}

		@Override
		public MountBuilder setReadOnly(boolean mountReadOnly) {
			this.readOnly = mountReadOnly;
			return this;
		}

		@Override
		public MountBuilder setVolumeName(String volumeName) {
			//see parameter nVolumeNameSize of  https://learn.microsoft.com/de-de/windows/win32/api/fileapi/nf-fileapi-getvolumeinformationw#parameters
			if (volumeName.length() > WinNT.MAX_PATH) {
				throw new IllegalArgumentException("Volume label must be at most 260 characters long");
			}
			this.volumeLabel = volumeName;
			return this;
		}

		@Override
		public Mount mount() throws MountFailedException {
			EnumIntegerSet<DokanOption> adjustedDokanOptions = mountFlags.dokanOptions();
			EnumIntegerSet<FileSystemFeature> adjustedFSFeatures = DEFAULT_FS_FEATURES;

			if (readOnly) {
				adjustedDokanOptions.add(DokanOption.WRITE_PROTECTION);
				adjustedFSFeatures.add(FileSystemFeature.READ_ONLY_VOLUME);
			}

			var deviceOptions = new DeviceOptions(mountPoint.toAbsolutePath().toString(),//
					mountFlags.threadCount(),
					adjustedDokanOptions,
					"",
					mountFlags.timeout(),
					mountFlags.allocationUnitSize(),
					mountFlags.sectorSize());
			var volumeInfo = new VolumeInformation(VolumeInformation.DEFAULT_MAX_COMPONENT_LENGTH, volumeLabel, 0x98765432, fsName, adjustedFSFeatures);
			AtomicReference<SafeUnmountCheck> safeUnmountCheck = new AtomicReference<>(null);
			DokanyFileSystem dokanyFs = new ReadWriteAdapter(fsRoot, new LockManager(), volumeInfo, safeUnmountCheck);


			DokanyMount mount = new DokanyMount(deviceOptions, dokanyFs, safeUnmountCheck.get());
			LOG.debug("Mounting on {}: ...", deviceOptions.MountPoint);
			try {
				//@formatter:off
				mount.mount(exception -> {});
				//@formatter:on
				LOG.debug("Mounted directory at {} successfully.", deviceOptions.MountPoint);
				return new DokanyMountHandle(mount, mountPoint);
			} catch (DokanyException e) {
				LOG.error("Mounting failed.", e);
				throw new MountFailedException(e);
			}
		}
	}

	static class DokanyMountHandle implements Mount {

		private final DokanyMount mount;
		private final Path mountPoint;

		DokanyMountHandle(DokanyMount mount, Path mountPoint) {
			this.mount = mount;
			this.mountPoint = mountPoint;
		}

		@Override
		public Mountpoint getMountpoint() {
			return Mountpoint.forPath(mountPoint);
		}

		@Override
		public void unmount() throws UnmountFailedException {
			try {
				mount.unmount();
			} catch (IllegalStateException e) {
				throw new UnmountFailedException("The file system is still in use.");
			}
		}

		@Override
		public void unmountForced() throws UnmountFailedException {
			mount.unmountForced();
		}

		@Override
		public void close() throws UnmountFailedException, IOException {
			mount.close();
		}
	}
}
