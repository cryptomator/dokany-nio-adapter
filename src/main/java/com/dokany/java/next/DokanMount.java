package com.dokany.java.next;


import com.dokany.java.next.nativeannotations.EnumSet;
import com.dokany.java.next.structures.DokanOperations;
import com.dokany.java.next.structures.DokanOptions;
import com.sun.jna.Callback;
import com.sun.jna.CallbackThreadInitializer;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * TODO: Add Description to this class
 */
public class DokanMount implements AutoCloseable {

	private static final int TIMEOUT = 5000;

	static {
		DokanAPI.DokanInit();
	}

	private final DokanOperations dokanOperations;
	private final DokanOptions dokanOptions;
	private final CallbackThreadInitializer callbackThreadInitializer;
	private final Pointer memoryContainingHandle;

	private volatile boolean isUnmounted;

	DokanMount(DokanOperations dokanOperations, DokanOptions dokanOptions, Memory dokanInstanceHandle, CallbackThreadInitializer callbackThreadInitializer) {
		this.dokanOperations = dokanOperations;
		this.dokanOptions = dokanOptions;
		this.callbackThreadInitializer = callbackThreadInitializer;
		this.memoryContainingHandle = dokanInstanceHandle;
		this.isUnmounted = false;
	}

	//TODO: extend to cover more dokanOptions variables
	public static DokanMount mount(DokanFileSystem fs, Path mountPoint, @EnumSet int mountOptions) throws DokanException {
		var callbackThreadInitializer = new DokanCallbackThreadInitializer("dokan-");
		var dokanOperations = prepare(fs, callbackThreadInitializer);
		var dokanOptions = new DokanOptions.Builder() //
				.withOptions(mountOptions) //
				.withTimeout(TIMEOUT) //
				.withSingleThreadEnabled(true) //
				.build(mountPoint);
		var memoryContainingHandle = new Memory(Native.POINTER_SIZE);
		memoryContainingHandle.clear(Native.POINTER_SIZE);

		int result = DokanAPI.DokanCreateFileSystem(dokanOptions, dokanOperations, memoryContainingHandle);
		if (result != 0) {
			throw new DokanException("DokanCreateFileSystem returned non-zero result: " + result); //TODO: thrown own exception
		}

		return new DokanMount(dokanOperations, dokanOptions, memoryContainingHandle, callbackThreadInitializer);
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
		}
		if (!notImplementedMethods.contains("findStreams")) {
			dokanOperations.setFindStreams(fs::findStreams);
			Native.setCallbackThreadInitializer(dokanOperations.FindStreams, callbackThreadInitializer);
		}
		return dokanOperations;
	}

	public synchronized void unmount() {
		if (isUnmounted) {
			return;
		}

		if (isRunning()) {
			DokanAPI.DokanCloseHandle(memoryContainingHandle.getPointer(0));
		}
		this.memoryContainingHandle.clear(Native.POINTER_SIZE);
		this.isUnmounted = true;
	}

	@Override
	public void close() {
		unmount();
	}

	public synchronized boolean isRunning() {
		return DokanAPI.DokanIsFileSystemRunning(memoryContainingHandle.getPointer(0));
	}


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
