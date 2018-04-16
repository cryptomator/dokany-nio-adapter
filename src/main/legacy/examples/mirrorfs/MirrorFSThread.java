package com.dokany.java.examples.mirrorfs;

import com.dokany.java.DokanyDriver;
import com.dokany.java.constants.FileSystemFeature;
import com.dokany.java.constants.MountOption;
import com.dokany.java.structure.DeviceOptions;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.FreeSpace;
import com.dokany.java.structure.VolumeInformation;

import java.io.FileNotFoundException;
import java.util.Date;

public class MirrorFSThread implements Runnable{

	String mountPoint;
	String dirToMirror;
	com.dokany.java.examples.mirrorfs.MirrorFS mirFs;
	DeviceOptions devOps;
	DokanyDriver dokany;

	public MirrorFSThread(String dirToMirror, String mountPoint){
		this.mountPoint = mountPoint;
		this.dirToMirror = dirToMirror;
		this.mirFs =null;

		final short threadCount = 1;
		EnumIntegerSet<MountOption> mountOptions = new EnumIntegerSet<>(MountOption.class);
		mountOptions.add(MountOption.DEBUG_MODE, MountOption.STD_ERR_OUTPUT, MountOption.MOUNT_MANAGER);
		String uncName = "";
		int timeout = 10000;
		int allocationUnitSize = 4096;
		int sectorSize = 4096;

		devOps = new DeviceOptions(mountPoint, threadCount, mountOptions, uncName, timeout, allocationUnitSize, sectorSize);

		EnumIntegerSet<FileSystemFeature> fsFeatures = new EnumIntegerSet<>(FileSystemFeature.class);
		fsFeatures.add(FileSystemFeature.CASE_PRESERVED_NAMES, FileSystemFeature.CASE_SENSITIVE_SEARCH,
				FileSystemFeature.PERSISTENT_ACLS, FileSystemFeature.SUPPORTS_REMOTE_STORAGE, FileSystemFeature.UNICODE_ON_DISK);

		VolumeInformation volumeInfo = new VolumeInformation(VolumeInformation.DEFAULT_MAX_COMPONENT_LENGTH, "Mirror", 0x98765432, "Dokany MirrorFS", fsFeatures);
		FreeSpace freeSpace = new FreeSpace(200000, 200);

		try {
			mirFs = new MirrorFS(devOps, volumeInfo, freeSpace, new Date(), dirToMirror);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("Hello!");
		dokany= new DokanyDriver(devOps, mirFs);
	}

	@Override
	public void run() {
		dokany.start();
	}

	public DokanyDriver getDriver(){
		return dokany;
	}
}
