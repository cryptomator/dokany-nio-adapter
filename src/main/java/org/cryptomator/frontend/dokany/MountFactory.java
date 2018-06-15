package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyDriver;
import com.dokany.java.DokanyFileSystem;
import com.dokany.java.constants.FileSystemFeature;
import com.dokany.java.constants.MountOption;
import com.dokany.java.structure.DeviceOptions;
import com.dokany.java.structure.EnumIntegerSet;
import com.dokany.java.structure.VolumeInformation;

import java.nio.file.Path;

public class MountFactory {

    private static final short THREAD_COUNT = 2;
    private static final EnumIntegerSet<MountOption> MOUNT_OPTIONS = new EnumIntegerSet<>(MountOption.DEBUG_MODE, //
            MountOption.STD_ERR_OUTPUT, //
            MountOption.REMOVABLE_DRIVE, //
            MountOption.CURRENT_SESSION);
    private static final EnumIntegerSet<FileSystemFeature> FILE_SYSTEM_FEATURES = new EnumIntegerSet<>(FileSystemFeature.CASE_PRESERVED_NAMES, //
            FileSystemFeature.CASE_SENSITIVE_SEARCH, //
            FileSystemFeature.PERSISTENT_ACLS, //
            FileSystemFeature.SUPPORTS_REMOTE_STORAGE, //
            FileSystemFeature.UNICODE_ON_DISK);
    private static final String UNC_NAME = "";
    private static final int TIMEOUT = 10000;
    private static final int ALLOC_UNIT_SIZE = 4096;
    private static final int SECTOR_SIZE = 4096;

    public Mount mount(Path fileSystemRoot, char driveLetter, String volumeName, String fileSystemName) {
        String mountPoint = driveLetter + ":\\";
        DeviceOptions deviceOptions = new DeviceOptions(mountPoint, THREAD_COUNT, MOUNT_OPTIONS, UNC_NAME, TIMEOUT, ALLOC_UNIT_SIZE, SECTOR_SIZE);
        VolumeInformation volumeInfo = new VolumeInformation(VolumeInformation.DEFAULT_MAX_COMPONENT_LENGTH, volumeName, 0x98765432, fileSystemName, FILE_SYSTEM_FEATURES);
        DokanyFileSystem myFs = new ReadWriteAdapter(fileSystemRoot, volumeInfo);
        DokanyDriver dokanyDriver = new DokanyDriver(deviceOptions, myFs);
        return new Mount(dokanyDriver);
    }

}
