package com.dokany.java.next.structures;

import com.dokany.java.next.nativeannotations.Boolean;
import com.dokany.java.next.nativeannotations.EnumSet;
import com.dokany.java.next.nativeannotations.Unsigned;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinNT;

import java.nio.file.Path;

@Structure.FieldOrder({"Version", "SingleThread", "Options", "GlobalContext", "MountPoint", "UNCName", "Timeout", "AllocationUnitSize", "SectorSize", "VolumeSecurityDescriptorLength", "VolumeSecurityDescriptor"})
public class DokanOptions extends Structure implements Structure.ByReference {

	private static final int VOLUME_SECURITY_DESCRIPTOR_MAX_SIZE = 1024 * 16;
	/**
	 * Version of the Dokan features requested without dots (version "123" is equal to Dokan version 1.2.3).
	 */
	@Unsigned
	public volatile short Version;

	/**
	 * Only use a single thread to process events. This is highly not recommended as can easily create a bottleneck.
	 */
	@Boolean
	public volatile byte SingleThread;

	/**
	 * Features enabled for the mount. See \ref DOKAN_OPTION.
	 */
	@EnumSet
	public volatile int Options;

	/**
	 * FileSystem can store anything here.
	 */
	@Unsigned
	public volatile long GlobalContext;

	/**
	 * Mount point. It can be a driver letter like "M:\" or a folder path "C:\mount\dokan" on a NTFS partition.
	 */
	//LPCWSTR
	//TODO: should be volatile?
	public WString MountPoint;

	/**
	 * UNC Name for the Network Redirector
	 * \see <a href="https://msdn.microsoft.com/en-us/library/windows/hardware/ff556761(v=vs.85).aspx">Support for UNC Naming</a>
	 */
	//LPCWSTR
	public WString UNCName;

	/**
	 * Max timeout in milliseconds of each request before Dokan gives up to wait events to complete.
	 * A timeout request is a sign that the userland implementation is no longer able to properly manage requests in time.
	 * The driver will therefore unmount the device when a timeout trigger in order to keep the system stable.
	 * The default timeout value is 15 seconds.
	 */
	@Unsigned
	public volatile int Timeout;

	/**
	 * Allocation Unit Size of the volume. This will affect the file size.
	 */
	@Unsigned
	public volatile int AllocationUnitSize;

	/**
	 * Sector Size of the volume. This will affect the file size.
	 */
	@Unsigned
	public volatile int SectorSize;

	/**
	 * Length of the optional VolumeSecurityDescriptor provided. Set 0 will disable the option.
	 */
	@Unsigned
	public volatile int VolumeSecurityDescriptorLength;

	/**
	 * Optional Volume Security descriptor. See <a href="https://docs.microsoft.com/en-us/windows/win32/api/securitybaseapi/nf-securitybaseapi-initializesecuritydescriptor">InitializeSecurityDescriptor</a>
	 */
	public byte[] VolumeSecurityDescriptor = new byte[VOLUME_SECURITY_DESCRIPTOR_MAX_SIZE];

	public int getVersion() {
		return Short.toUnsignedInt(Version);
	}

	public boolean getSingleThread() {
		return SingleThread != 0;
	}

	public long getOptions() {
		return Integer.toUnsignedLong(Options);
	}

	public long getTimeout() {
		return Integer.toUnsignedLong(Timeout);
	}

	public long getAllocationUnitSize() {
		return Integer.toUnsignedLong(AllocationUnitSize);
	}

	public long getSectorSize() {
		return Integer.toUnsignedLong(SectorSize);
	}

	public long getVolumeSecurityDescriptorLength() {
		return Integer.toUnsignedLong(VolumeSecurityDescriptorLength);
	}


	public static class Builder {

		@Unsigned
		private short version = 200;
		@Boolean
		private byte singleThread = 0x00;
		@EnumSet
		private int options = 0;
		@Unsigned
		private long globalContext = 0;
		private String mountPoint;
		private String uncName = "";
		@Unsigned
		private int timeout = 5000;
		@Unsigned
		private int allocationUnitSize = 4096; //default ntfs is 4KiBi
		@Unsigned
		private int sectorSize = 4096;
		@Unsigned
		private int volumeSecurityDescriptorLength = 0;
		private byte[] volumeSecurityDescriptor = new byte[VOLUME_SECURITY_DESCRIPTOR_MAX_SIZE];

		public DokanOptions dokanOptions = new DokanOptions();

		public Builder(Path mountPoint) {
			this.mountPoint = mountPoint.toAbsolutePath().toString();
		}

		public Builder withSecurityDescriptor(WinNT.SECURITY_DESCRIPTOR descriptor) {
			if (descriptor.data.length > this.volumeSecurityDescriptor.length) {
				throw new IllegalArgumentException("Given security descriptor is too big");
			}

			for (int i = 0; i < descriptor.data.length; i++) {
				this.volumeSecurityDescriptor[i] = descriptor.data[i];
			}
			this.volumeSecurityDescriptorLength = descriptor.data.length;
			return this;
		}

		public Builder withSingleThreadEnabled(boolean isSingleThreaded) {
			if (isSingleThreaded) {
				this.singleThread = 0x01;
			}
			return this;
		}

		public Builder withGlobalContext(long context) {
			this.globalContext = context;
			return this;
		}

		public Builder withUNCName(String uncName) {
			this.uncName = uncName;
			return this;
		}

		public Builder withTimeout(@Unsigned int timeout) {
			this.timeout = timeout;
			return this;
		}

		public Builder withAllocationUnitSize(@Unsigned int clustersize) {
			this.allocationUnitSize = clustersize;
			return this;
		}

		public Builder withSectorSize(@Unsigned int sectorSize) {
			this.sectorSize = sectorSize;
			return this;
		}

		public Builder withOptions(@EnumSet int options) {
			this.options = options;
			return this;
		}

		public DokanOptions build() {
			dokanOptions.writeField("Version", version);
			dokanOptions.writeField("SingleThread", singleThread);
			dokanOptions.writeField("Options", options);
			dokanOptions.writeField("GlobalContext", globalContext);
			dokanOptions.MountPoint = new WString(mountPoint);
			dokanOptions.UNCName = new WString(uncName);
			dokanOptions.writeField("Timeout", timeout);
			dokanOptions.writeField("AllocationUnitSize", allocationUnitSize);
			dokanOptions.writeField("SectorSize", sectorSize);
			dokanOptions.writeField("VolumeSecurityDescriptorLength", volumeSecurityDescriptorLength);
			dokanOptions.writeField("VolumeSecurityDescriptor", volumeSecurityDescriptor);
			return dokanOptions;
		}
	}
}
