package com.dokany.java.next;


import com.dokany.java.next.nativeannotations.EnumSet;
import com.dokany.java.next.nativeannotations.Unsigned;
import com.dokany.java.next.structures.DokanOperations;
import com.dokany.java.next.structures.DokanOptions;
import com.sun.jna.Callback;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * TODO: Add Description to this class
 */
public class DokanMount {

	private static final Logger LOG = LoggerFactory.getLogger(DokanMount.class);

	private static final int TIMEOUT = 3000;

	private final DokanOperations dokanOperations;
	private final CallbackThreadInitializer callbackThreadInitializer;

	private DokanOptions options;
	private Path mountPoint;

	private volatile Pointer memoryContainingHandle;


	DokanMount(DokanOperations dokanOperations, CallbackThreadInitializer callbackThreadInitializer) {
		this.dokanOperations = dokanOperations;
		this.callbackThreadInitializer = callbackThreadInitializer;
	}

	public static DokanMount create(DokanFileSystem fs) {
		var callbackThreadInitializer = new DokanCallbackThreadInitializer("dokan-");
		var operations = prepare(fs, callbackThreadInitializer);
		return new DokanMount(operations, callbackThreadInitializer);
	}

	private static DokanOperations prepare(DokanFileSystem fs, DokanCallbackThreadInitializer callbackThreadInitializer) {
		Set<String> notImplementedMethods = Arrays.stream(fs.getClass().getMethods()).filter(method -> method.getAnnotation(NotImplemented.class) != null).map(Method::getName).collect(Collectors.toSet());
		DokanOperations dokanOperations = new DokanOperations();

		if (!notImplementedMethods.contains("zwCreateFile")) {
			dokanOperations.setZwCreateFile(fs::zwCreateFile);
			Native.setCallbackThreadInitializer(dokanOperations.ZwCreateFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("cleanup")) {
			dokanOperations.setCleanup(fs::cleanup);
			Native.setCallbackThreadInitializer(dokanOperations.Cleanup, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("closeFile")) {
			dokanOperations.setCloseFile(fs::closeFile);
			Native.setCallbackThreadInitializer(dokanOperations.CloseFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("readFile")) {
			dokanOperations.setReadFile(fs::readFile);
			Native.setCallbackThreadInitializer(dokanOperations.ReadFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("writeFile")) {
			dokanOperations.setWriteFile(fs::writeFile);
			Native.setCallbackThreadInitializer(dokanOperations.WriteFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("flushFileBuffer")) {
			dokanOperations.setFlushFileBuffers(fs::flushFileBuffers);
			Native.setCallbackThreadInitializer(dokanOperations.FlushFileBuffers, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("getFileInformation")) {
			dokanOperations.setGetFileInformation(fs::getFileInformation);
			Native.setCallbackThreadInitializer(dokanOperations.GetFileInformation, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("findFiles")) {
			dokanOperations.setFindFiles(fs::findFiles);
			Native.setCallbackThreadInitializer(dokanOperations.FindFiles, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("findFilesWithPattern")) {
			dokanOperations.setFindFilesWithPattern(fs::findFilesWithPattern);
			Native.setCallbackThreadInitializer(dokanOperations.FindFilesWithPattern, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("setFileAttributes")) {
			dokanOperations.setSetFileAttributes(fs::setFileAttributes);
			Native.setCallbackThreadInitializer(dokanOperations.SetFileAttributes, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("setFileTime")) {
			dokanOperations.setSetFileTime(fs::setFileTime);
			Native.setCallbackThreadInitializer(dokanOperations.SetFileTime, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("deleteFile")) {
			dokanOperations.setDeleteFile(fs::deleteFile);
			Native.setCallbackThreadInitializer(dokanOperations.DeleteFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("deleteDirectory")) {
			dokanOperations.setDeleteDirectory(fs::deleteDirectory);
			Native.setCallbackThreadInitializer(dokanOperations.DeleteDirectory, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("moveFile")) {
			dokanOperations.setMoveFile(fs::moveFile);
			Native.setCallbackThreadInitializer(dokanOperations.MoveFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("setEndOfFile")) {
			dokanOperations.setSetEndOfFile(fs::setEndOfFile);
			Native.setCallbackThreadInitializer(dokanOperations.SetEndOfFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("setAllocationSize")) {
			dokanOperations.setSetAllocationSize(fs::setAllocationSize);
			Native.setCallbackThreadInitializer(dokanOperations.SetAllocationSize, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("lockFile")) {
			dokanOperations.setLockFile(fs::lockFile);
			Native.setCallbackThreadInitializer(dokanOperations.LockFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("unlockFile")) {
			dokanOperations.setUnlockFile(fs::unlockFile);
			Native.setCallbackThreadInitializer(dokanOperations.UnlockFile, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("getDiskFreeSpace")) {
			dokanOperations.setGetDiskFreeSpace(fs::getDiskFreeSpace);
			Native.setCallbackThreadInitializer(dokanOperations.GetDiskFreeSpace, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("getVolumeInformation")) {
			dokanOperations.setGetVolumeInformation(fs::getVolumeInformation);
			Native.setCallbackThreadInitializer(dokanOperations.GetVolumeInformation, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("mounted")) {
			dokanOperations.setMounted(fs::mounted);
			Native.setCallbackThreadInitializer(dokanOperations.Mounted, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("unmounted")) {
			dokanOperations.setUnmounted(fs::unmounted);
			Native.setCallbackThreadInitializer(dokanOperations.Unmounted, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("getFileSecurity")) {
			dokanOperations.setGetFileSecurity(fs::getFileSecurity);
			Native.setCallbackThreadInitializer(dokanOperations.GetFileSecurity, callbackThreadInitializer);
		}
		if (!notImplementedMethods.contains("setFileSecurity")) {
			dokanOperations.setSetFileSecurity(fs::setFileSecurity);
			Native.setCallbackThreadInitializer(dokanOperations.SetFileSecurity, callbackThreadInitializer);
		} if (!notImplementedMethods.contains("findStreams")) {
			dokanOperations.setFindStreams(fs::findStreams);
			Native.setCallbackThreadInitializer(dokanOperations.FindStreams, callbackThreadInitializer);
		}
		return dokanOperations;
	}

	//TODO: rewrite!
	public synchronized void mount(Path mountPoint,  @EnumSet int options, @Unsigned int timeout) {
		DokanAPI.DokanInit();
		var dokanOptions = new DokanOptions.Builder(mountPoint)
				.withOptions(options)
				.withTimeout(timeout)
				.withSingleThreadEnabled(true)
				.build();

		this.memoryContainingHandle = new Memory(Native.POINTER_SIZE);
		DokanAPI.DokanCreateFileSystem(dokanOptions, dokanOperations, memoryContainingHandle);
	}

	public synchronized void unmount() {
		DokanAPI.DokanCloseHandle(memoryContainingHandle.getPointer(0));
	}


	/*
	/**
	 * The general mount method. If the underlying system supports shutdown hooks, one is installed in case the JVM is shutting down and the filesystem is still mounted.
	 *
	 * @param mountPoint path pointing to an empty directory or unused drive letter
	 * @param volumeName the displayed name of the volume (only important when a drive letter is used as a mount point)
	 * @param volumeSerialnumber the serial number of the volume (only important when a drive letter is used as a mount point)
	 * @param blocking if true the mount and further file system calls are foreground operations and thus will block this thread. To unmount the device you have to use the dokanctl.exe tool.
	 * @param timeout timeout after which a not processed file system call is canceled and the volume is unmounted
	 * @param allocationUnitSize the size of the smallest allocatable space in bytes
	 * @param sectorSize the sector size
	 * @param UNCName
	 * @param threadCount the number of threads spawned for processing filesystem calls
	 * @param options an {@link MaskValueSet} containing {@link MountOption}s
	@Override
	public final synchronized void mount(Path mountPoint, String volumeName, int volumeSerialnumber, boolean blocking, @Unsigned int timeout, @Unsigned int allocationUnitSize, @Unsigned int sectorSize, String UNCName, @Unsigned short threadCount, MaskValueSet<MountOption> options) {
		this.dokanOptions = new DokanOptions(mountPoint.toString(), threadCount, options, UNCName, timeout, allocationUnitSize, sectorSize);
		this.mountPoint = mountPoint;
		this.volumeName = volumeName;
		this.volumeSerialnumber = volumeSerialnumber;

		try {
			int mountStatus;

			if (DokanUtils.canHandleShutdownHooks()) {
				Runtime.getRuntime().addShutdownHook(new Thread(this::unmount));
			}

			if (blocking) {
				mountStatus = execMount(dokanOptions);
			} else {
				try {
					mountStatus = CompletableFuture.supplyAsync(() -> execMount(dokanOptions)).get(TIMEOUT, TimeUnit.MILLISECONDS);
				} catch (TimeoutException e) {
					// ok
					mountStatus = 0;
				}
				isMounted.set(true);
			}
			if (mountStatus < 0) {
				throw new RuntimeException("Negative result of mount operation. Code" + mountStatus + " -- " + MountError.fromInt(mountStatus).getDescription());
			}
		} catch (UnsatisfiedLinkError | Exception e) {
			throw new MountFailedException("Unable to mount filesystem.", e);
		}
	}

	/**
	 * Additional method for easy mounting with a lot of default values
	 *
	 * @param mountPoint
	 * @param mountOptions
	public void mount(Path mountPoint, MaskValueSet<MountOption> mountOptions) {
		String uncName = null;
		@Unsigned short threadCount = 5;
		@Unsigned int timeout = 3000;
		@Unsigned int allocationUnitSize = 4096;
		@Unsigned int sectorsize = 512;
		String volumeName = "DOKAN";
		int volumeSerialnumber = 30975;
		mount(mountPoint, volumeName, volumeSerialnumber, false, timeout, allocationUnitSize, sectorsize, uncName, threadCount, mountOptions);
	}

	@Override
	public final synchronized void unmount() {
		if (!volumeIsStillMounted()) {
			isMounted.set(false);
		}

		if (isMounted.get()) {
			if (DokanNativeMethods.DokanRemoveMountPoint(new WString(mountPoint.toAbsolutePath().toString()))) {
				isMounted.set(false);
			} else {
				throw new UnmountFailedException("Unmount of " + volumeName + "(" + mountPoint + ") failed. Try again, shut down JVM or use `dokanctl.exe to unmount manually.");
			}
		}
	}

	private boolean volumeIsStillMounted() {
		char[] mntPtCharArray = mountPoint.toAbsolutePath().toString().toCharArray();
		IntByReference lengthPointer = new IntByReference();
		Pointer startOfList = DokanNativeMethods.DokanGetMountPointList(false, lengthPointer);

		@Unsigned int length = lengthPointer.getValue();
		List<DokanControl> list = DokanControl.getDokanControlList(startOfList, length);
		// It is not enough that the entry.MountPoint contains the actual mount point. It also has to ends afterwards.
		boolean mountPointInList = list.stream().anyMatch(entry -> Arrays.equals(entry.MountPoint, 12, 12 + mntPtCharArray.length, mntPtCharArray, 0, mntPtCharArray.length) && (entry.MountPoint.length == 12 + mntPtCharArray.length || entry.MountPoint[12 + mntPtCharArray.length] == '\0'));
		DokanNativeMethods.DokanReleaseMountPointList(startOfList);
		return mountPointInList;
	}


	@Override
	public void close() {
		unmount();
	}

	 */


	private static class DokanCallbackThreadInitializer extends CallbackThreadInitializer {

		private String prefix;
		private AtomicInteger counter;

		DokanCallbackThreadInitializer(String prefix) {
			super(true, false, prefix, new ThreadGroup(prefix));
			this.prefix = prefix;
			this.counter = new AtomicInteger(0);
		}

		@Override
		public String getName(Callback cb) {
			return prefix + counter.getAndIncrement();
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}
	}
}
