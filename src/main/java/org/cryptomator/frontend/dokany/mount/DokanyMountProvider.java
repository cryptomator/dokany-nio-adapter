package org.cryptomator.frontend.dokany.mount;

import com.dokany.java.Dokany;
import org.cryptomator.integrations.mount.Mount;
import org.cryptomator.integrations.mount.MountBuilder;
import org.cryptomator.integrations.mount.MountCapability;
import org.cryptomator.integrations.mount.MountFailedException;
import org.cryptomator.integrations.mount.MountService;

import java.nio.file.Path;
import java.util.Set;

/**
 * Dokany implementation of the {@link MountService} interface.
 */
public class DokanyMountProvider implements MountService {

	private static final int SUPPORTED_API_VERSION = 150;
	private static final int SUPPORTED_DRIVER_VERSION = 150;

	@Override
	public String displayName() {
		return "Dokany (1.5)";
	}

	@Override
	public boolean isSupported() {
		return Dokany.isInstalled() //
				&& Dokany.apiVersion() >= SUPPORTED_API_VERSION //
				&& Dokany.driverVersion() >= SUPPORTED_DRIVER_VERSION;
	}

	@Override
	public String getDefaultMountFlags(String mountName) {
		return " --options CURRENT_SESSION"
				+ " --thread-count 5"
				+ " --timeout 10000"
				+ " --allocation-unit-size 4096"
				+ " --sector-size 4096";
	}

	@Override
	public Set<MountCapability> capabilities() {
		return Set.of(MountCapability.READ_ONLY, MountCapability.MOUNT_AS_DRIVE_LETTER, MountCapability.MOUNT_TO_EXISTING_DIR, MountCapability.MOUNT_FLAGS);
	}

	@Override
	public MountBuilder forFileSystem(Path path) {
		return new DokanyMountBuilder(path);
	}

	static class DokanyMountBuilder implements MountBuilder {

		private final Path fsRoot;
		private Path mountPoint;
		private String mountFlags;
		private boolean readOnly;

		private DokanyMountBuilder(Path fsRoot) {
			this.fsRoot = fsRoot;
		}

		@Override
		public MountBuilder setMountpoint(Path mountPoint) {
			this.mountPoint = mountPoint;
			return this;
		}

		@Override
		public MountBuilder setMountFlags(String mountFlags) {
			this.mountFlags = mountFlags;
			return this;
		}

		@Override
		public MountBuilder setReadOnly(boolean mountReadOnly) {
			this.readOnly = mountReadOnly;
			return this;
		}

		@Override
		public Mount mount() throws MountFailedException {
			//TODO
			return null;
		}
	}
}
