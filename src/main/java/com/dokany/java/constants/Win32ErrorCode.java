package com.dokany.java.constants;


public enum Win32ErrorCode implements EnumInteger {

	ERROR_SUCCESS(0X00000000, "The operation completed successfully."),
	NERR_Success(0X00000000, "The operation completed successfully."),
	ERROR_INVALID_FUNCTION(0X00000001, "Incorrect function."),
	ERROR_FILE_NOT_FOUND(0X00000002, "The system cannot find the file specified."),
	ERROR_PATH_NOT_FOUND(0X00000003, "The system cannot find the path specified."),
	ERROR_TOO_MANY_OPEN_FILES(0X00000004, "The system cannot open the file."),
	ERROR_ACCESS_DENIED(0X00000005, "Access is denied."),
	ERROR_INVALID_HANDLE(0X00000006, "The handle is invalid."),
	ERROR_ARENA_TRASHED(0X00000007, "The storage control blocks were destroyed."),
	ERROR_NOT_ENOUGH_MEMORY(0X00000008, "Not enough storage is available to process this   command."),
	ERROR_INVALID_BLOCK(0X00000009, "The storage control block address is invalid."),
	ERROR_BAD_ENVIRONMENT(0X0000000A, "The environment is incorrect."),
	ERROR_BAD_FORMAT(0X0000000B, "An attempt was made to load a program with an   incorrect format."),
	ERROR_INVALID_ACCESS(0X0000000C, "The access code is invalid."),
	ERROR_INVALID_DATA(0X0000000D, "The data is invalid."),
	ERROR_OUTOFMEMORY(0X0000000E, "Not enough storage is available to complete this   operation."),
	ERROR_INVALID_DRIVE(0X0000000F, "The system cannot find the drive specified."),
	ERROR_CURRENT_DIRECTORY(0X00000010, "The directory cannot be removed."),
	ERROR_NOT_SAME_DEVICE(0X00000011, "The system cannot move the file to a different disk   drive."),
	ERROR_NO_MORE_FILES(0X00000012, "There are no more files."),
	ERROR_WRITE_PROTECT(0X00000013, "The media is write-protected."),
	ERROR_BAD_UNIT(0X00000014, "The system cannot find the device specified."),
	ERROR_NOT_READY(0X00000015, "The device is not ready."),
	ERROR_BAD_COMMAND(0X00000016, "The device does not recognize the command."),
	ERROR_CRC(0X00000017, "Data error (cyclic redundancy check)."),
	ERROR_BAD_LENGTH(0X00000018, "The program issued a command but the command length is   incorrect."),
	ERROR_SEEK(0X00000019, "The drive cannot locate a specific area or track on   the disk."),
	ERROR_NOT_DOS_DISK(0X0000001A, "The specified disk cannot be accessed."),
	ERROR_SECTOR_NOT_FOUND(0X0000001B, "The drive cannot find the sector requested."),
	ERROR_OUT_OF_PAPER(0X0000001C, "The printer is out of paper."),
	ERROR_WRITE_FAULT(0X0000001D, "The system cannot write to the specified device."),
	ERROR_READ_FAULT(0X0000001E, "The system cannot read from the specified device."),
	ERROR_GEN_FAILURE(0X0000001F, "A device attached to the system is not functioning."),
	ERROR_SHARING_VIOLATION(0X00000020, "The process cannot access the file because it is being   used by another process."),
	ERROR_LOCK_VIOLATION(0X00000021, "The process cannot access the file because another   process has locked a portion of the file."),
	ERROR_WRONG_DISK(0X00000022, "The wrong disk is in the drive. Insert %2 (Volume   Serial Number: %3) into drive %1."),
	ERROR_SHARING_BUFFER_EXCEEDED(0X00000024, "Too many files opened for sharing."),
	ERROR_HANDLE_EOF(0X00000026, "Reached the end of the file."),
	ERROR_HANDLE_DISK_FULL(0X00000027, "The disk is full."),
	ERROR_NOT_SUPPORTED(0X00000032, "The request is not supported."),
	ERROR_REM_NOT_LIST(0X00000033, "Windows cannot find the network path. Verify that the   network path is correct and the destination computer is not busy or turned   off. If Windows still cannot find the network path, contact your network   administrator."),
	ERROR_DUP_NAME(0X00000034, "You were not connected because a duplicate name exists   on the network. Go to System in Control Panel to change the computer name,   and then try again."),
	ERROR_BAD_NETPATH(0X00000035, "The network path was not found."),
	ERROR_NETWORK_BUSY(0X00000036, "The network is busy."),
	ERROR_DEV_NOT_EXIST(0X00000037, "The specified network resource or device is no longer   available."),
	ERROR_TOO_MANY_CMDS(0X00000038, "The network BIOS command limit has been reached."),
	ERROR_ADAP_HDW_ERR(0X00000039, "A network adapter hardware error occurred."),
	ERROR_BAD_NET_RESP(0X0000003A, "The specified server cannot perform the requested   operation."),
	ERROR_UNEXP_NET_ERR(0X0000003B, "An unexpected network error occurred."),
	ERROR_BAD_REM_ADAP(0X0000003C, "The remote adapter is not compatible."),
	ERROR_PRINTQ_FULL(0X0000003D, "The print queue is full."),
	ERROR_NO_SPOOL_SPACE(0X0000003E, "Space to store the file waiting to be printed is not   available on the server."),
	ERROR_PRINT_CANCELLED(0X0000003F, "Your file waiting to be printed was deleted."),
	ERROR_NETNAME_DELETED(0X00000040, "The specified network name is no longer available."),
	ERROR_NETWORK_ACCESS_DENIED(0X00000041, "Network access is denied."),
	ERROR_BAD_DEV_TYPE(0X00000042, "The network resource type is not correct."),
	ERROR_BAD_NET_NAME(0X00000043, "The network name cannot be found."),
	ERROR_TOO_MANY_NAMES(0X00000044, "The name limit for the local computer network adapter   card was exceeded."),
	ERROR_TOO_MANY_SESS(0X00000045, "The network BIOS session limit was exceeded."),
	ERROR_SHARING_PAUSED(0X00000046, "The remote server has been paused or is in the process   of being started."),
	ERROR_REQ_NOT_ACCEP(0X00000047, "No more connections can be made to this remote   computer at this time because the computer has accepted the maximum number of   connections."),
	ERROR_REDIR_PAUSED(0X00000048, "The specified printer or disk device has been paused."),
	ERROR_FILE_EXISTS(0X00000050, "The file exists."),
	ERROR_CANNOT_MAKE(0X00000052, "The directory or file cannot be created."),
	ERROR_FAIL_I24(0X00000053, "Fail on INT 24."),
	ERROR_OUT_OF_STRUCTURES(0X00000054, "Storage to process this request is not available."),
	ERROR_ALREADY_ASSIGNED(0X00000055, "The local device name is already in use."),
	ERROR_INVALID_PASSWORD(0X00000056, "The specified network password is not correct."),
	ERROR_INVALID_PARAMETER(0X00000057, "The parameter is incorrect."),
	ERROR_NET_WRITE_FAULT(0X00000058, "A write fault occurred on the network."),
	ERROR_NO_PROC_SLOTS(0X00000059, "The system cannot start another process at this time."),
	ERROR_TOO_MANY_SEMAPHORES(0X00000064, "Cannot create another system semaphore."),
	ERROR_EXCL_SEM_ALREADY_OWNED(0X00000065, "The exclusive semaphore is owned by another process."),
	ERROR_SEM_IS_SET(0X00000066, "The semaphore is set and cannot be closed."),
	ERROR_TOO_MANY_SEM_REQUESTS(0X00000067, "The semaphore cannot be set again."),
	ERROR_INVALID_AT_INTERRUPT_TIME(0X00000068, "Cannot request exclusive semaphores at interrupt time."),
	ERROR_SEM_OWNER_DIED(0X00000069, "The previous ownership of this semaphore has ended."),
	ERROR_SEM_USER_LIMIT(0X0000006A, "Insert the disk for drive %1."),
	ERROR_DISK_CHANGE(0X0000006B, "The program stopped because an alternate disk was not   inserted."),
	ERROR_DRIVE_LOCKED(0X0000006C, "The disk is in use or locked by another process."),
	ERROR_BROKEN_PIPE(0X0000006D, "The pipe has been ended."),
	ERROR_OPEN_FAILED(0X0000006E, "The system cannot open the device or file specified."),
	ERROR_BUFFER_OVERFLOW(0X0000006F, "The file name is too long."),
	ERROR_DISK_FULL(0X00000070, "There is not enough space on the disk."),
	ERROR_NO_MORE_SEARCH_HANDLES(0X00000071, "No more internal file identifiers are available."),
	ERROR_INVALID_TARGET_HANDLE(0X00000072, "The target internal file identifier is incorrect."),
	ERROR_INVALID_CATEGORY(0X00000075, "The Input Output Control (IOCTL) call made by the   application program is not correct."),
	ERROR_INVALID_VERIFY_SWITCH(0X00000076, "The verify-on-write switch parameter value is not   correct."),
	ERROR_BAD_DRIVER_LEVEL(0X00000077, "The system does not support the command requested."),
	ERROR_CALL_NOT_IMPLEMENTED(0X00000078, "This function is not supported on this system."),
	ERROR_SEM_TIMEOUT(0X00000079, "The semaphore time-out period has expired."),
	ERROR_INSUFFICIENT_BUFFER(0X0000007A, "The data area passed to a system call is too small."),
	ERROR_INVALID_NAME(0X0000007B, "The file name, directory name, or volume label syntax   is incorrect."),
	ERROR_INVALID_LEVEL(0X0000007C, "The system call level is not correct."),
	ERROR_NO_VOLUME_LABEL(0X0000007D, "The disk has no volume label."),
	ERROR_MOD_NOT_FOUND(0X0000007E, "The specified module could not be found."),
	ERROR_PROC_NOT_FOUND(0X0000007F, "The specified procedure could not be found."),
	ERROR_WAIT_NO_CHILDREN(0X00000080, "There are no child processes to wait for."),
	ERROR_CHILD_NOT_COMPLETE(0X00000081, "The %1 application cannot be run in Win32 mode."),
	ERROR_DIRECT_ACCESS_HANDLE(0X00000082, "Attempt to use a file handle to an open disk partition   for an operation other than raw disk I/O."),
	ERROR_NEGATIVE_SEEK(0X00000083, "An attempt was made to move the file pointer before   the beginning of the file."),
	ERROR_SEEK_ON_DEVICE(0X00000084, "The file pointer cannot be set on the specified device   or file."),
	ERROR_IS_JOIN_TARGET(0X00000085, "None"),
	ERROR_IS_JOINED(0X00000086, "None"),
	ERROR_IS_SUBSTED(0X00000087, "None"),
	ERROR_NOT_JOINED(0X00000088, "None"),
	ERROR_NOT_SUBSTED(0X00000089, "The system tried to delete the substitution of a drive   that is not substituted."),
	ERROR_JOIN_TO_JOIN(0X0000008A, "The system tried to join a drive to a directory on a   joined drive."),
	ERROR_SUBST_TO_SUBST(0X0000008B, "The system tried to substitute a drive to a directory   on a substituted drive."),
	ERROR_JOIN_TO_SUBST(0X0000008C, "The system tried to join a drive to a directory on a   substituted drive."),
	ERROR_SUBST_TO_JOIN(0X0000008D, "None"),
	ERROR_BUSY_DRIVE(0X0000008E, "None"),
	ERROR_SAME_DRIVE(0X0000008F, "The system cannot join or substitute a drive to or for   a directory on the same drive."),
	ERROR_DIR_NOT_ROOT(0X00000090, "The directory is not a subdirectory of the root   directory."),
	ERROR_DIR_NOT_EMPTY(0X00000091, "The directory is not empty."),
	ERROR_IS_SUBST_PATH(0X00000092, "The path specified is being used in a substitute."),
	ERROR_IS_JOIN_PATH(0X00000093, "Not enough resources are available to process this   command."),
	ERROR_PATH_BUSY(0X00000094, "The path specified cannot be used at this time."),
	ERROR_IS_SUBST_TARGET(0X00000095, "An attempt was made to join or substitute a drive for   which a directory on the drive is the target of a previous substitute."),
	ERROR_SYSTEM_TRACE(0X00000096, "System trace information was not specified in your   CONFIG.SYS file, or tracing is disallowed."),
	ERROR_INVALID_EVENT_COUNT(0X00000097, "The number of specified semaphore events for   DosMuxSemWait is not correct."),
	ERROR_TOO_MANY_MUXWAITERS(0X00000098, "DosMuxSemWait did not execute; too many semaphores are   already set."),
	ERROR_INVALID_LIST_FORMAT(0X00000099, "The DosMuxSemWait list is not correct."),
	ERROR_LABEL_TOO_LONG(0X0000009A, "The volume label you entered exceeds the label   character limit of the destination file system."),
	ERROR_TOO_MANY_TCBS(0X0000009B, "Cannot create another thread."),
	ERROR_SIGNAL_REFUSED(0X0000009C, "The recipient process has refused the signal."),
	ERROR_DISCARDED(0X0000009D, "The segment is already discarded and cannot be locked."),
	ERROR_NOT_LOCKED(0X0000009E, "The segment is already unlocked."),
	ERROR_BAD_THREADID_ADDR(0X0000009F, "The address for the thread ID is not correct."),
	ERROR_BAD_ARGUMENTS(0X000000A0, "One or more arguments are not correct."),
	ERROR_BAD_PATHNAME(0X000000A1, "The specified path is invalid."),
	ERROR_SIGNAL_PENDING(0X000000A2, "A signal is already pending."),
	ERROR_MAX_THRDS_REACHED(0X000000A4, "No more threads can be created in the system."),
	ERROR_LOCK_FAILED(0X000000A7, "Unable to lock a region of a file."),
	ERROR_BUSY(0X000000AA, "The requested resource is in use."),
	ERROR_CANCEL_VIOLATION(0X000000AD, "A lock request was not outstanding for the supplied   cancel region."),
	ERROR_ATOMIC_LOCKS_NOT_SUPPORTED(0X000000AE, "The file system does not support atomic changes to the   lock type."),
	ERROR_INVALID_SEGMENT_NUMBER(0X000000B4, "The system detected a segment number that was not   correct."),
	ERROR_INVALID_ORDINAL(0X000000B6, "The operating system cannot run %1."),
	ERROR_ALREADY_EXISTS(0X000000B7, "Cannot create a file when that file already exists."),
	ERROR_INVALID_FLAG_NUMBER(0X000000BA, "The flag passed is not correct."),
	ERROR_SEM_NOT_FOUND(0X000000BB, "The specified system semaphore name was not found."),
	ERROR_INVALID_STARTING_CODESEG(0X000000BC, "The operating system cannot run %1."),
	ERROR_INVALID_STACKSEG(0X000000BD, "The operating system cannot run %1."),
	ERROR_INVALID_MODULETYPE(0X000000BE, "The operating system cannot run %1."),
	ERROR_INVALID_EXE_SIGNATURE(0X000000BF, "Cannot run %1 in Win32 mode."),
	ERROR_EXE_MARKED_INVALID(0X000000C0, "The operating system cannot run %1."),
	ERROR_BAD_EXE_FORMAT(0X000000C1, "%1 is not a valid Win32 application."),
	ERROR_ITERATED_DATA_EXCEEDS_64k(0X000000C2, "The operating system cannot run %1."),
	ERROR_INVALID_MINALLOCSIZE(0X000000C3, "The operating system cannot run %1."),
	ERROR_DYNLINK_FROM_INVALID_RING(0X000000C4, "The operating system cannot run this application   program."),
	ERROR_IOPL_NOT_ENABLED(0X000000C5, "The operating system is not presently configured to   run this application."),
	ERROR_INVALID_SEGDPL(0X000000C6, "The operating system cannot run %1."),
	ERROR_AUTODATASEG_EXCEEDS_64k(0X000000C7, "The operating system cannot run this application   program."),
	ERROR_RING2SEG_MUST_BE_MOVABLE(0X000000C8, "The code segment cannot be greater than or equal to 64   KB."),
	ERROR_RELOC_CHAIN_XEEDS_SEGLIM(0X000000C9, "The operating system cannot run %1."),
	ERROR_INFLOOP_IN_RELOC_CHAIN(0X000000CA, "The operating system cannot run %1."),
	ERROR_ENVVAR_NOT_FOUND(0X000000CB, "The system could not find the environment option that   was entered."),
	ERROR_NO_SIGNAL_SENT(0X000000CD, "No process in the command subtree has a signal   handler."),
	ERROR_FILENAME_EXCED_RANGE(0X000000CE, "The file name or extension is too long."),
	ERROR_RING2_STACK_IN_USE(0X000000CF, "The ring 2 stack is in use."),
	ERROR_META_EXPANSION_TOO_LONG(0X000000D0, "The asterisk (*) or question mark (?) global file name   characters are entered incorrectly, or too many global file name characters   are specified."),
	ERROR_INVALID_SIGNAL_NUMBER(0X000000D1, "The signal being posted is not correct."),
	ERROR_THREAD_1_INACTIVE(0X000000D2, "The signal handler cannot be set."),
	ERROR_LOCKED(0X000000D4, "The segment is locked and cannot be reallocated."),
	ERROR_TOO_MANY_MODULES(0X000000D6, "Too many dynamic-link modules are attached to this   program or dynamic-link module."),
	ERROR_NESTING_NOT_ALLOWED(0X000000D7, "Cannot nest calls to LoadModule."),
	ERROR_EXE_MACHINE_TYPE_MISMATCH(0X000000D8, "This version of %1 is not compatible with the version   of Windows you're running. Check your computer's system information to see   whether you need an x86 (32-bit) or x64 (64-bit) version of the program, and   then contact the software publisher."),
	ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY(0X000000D9, "The image file %1 is signed, unable to modify."),
	ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY(0X000000DA, "The image file %1 is strong signed, unable to modify."),
	ERROR_FILE_CHECKED_OUT(0X000000DC, "This file is checked out or locked for editing by   another user."),
	ERROR_CHECKOUT_REQUIRED(0X000000DD, "The file must be checked out before saving changes."),
	ERROR_BAD_FILE_TYPE(0X000000DE, "The file type being saved or retrieved has been   blocked."),
	ERROR_FILE_TOO_LARGE(0X000000DF, "The file size exceeds the limit allowed and cannot be   saved."),
	ERROR_FORMS_AUTH_REQUIRED(0X000000E0, "Access denied. Before opening files in this location,   you must first browse to the website and select the option to sign in   automatically."),
	ERROR_VIRUS_INFECTED(0X000000E1, "Operation did not complete successfully because the   file contains a virus."),
	ERROR_VIRUS_DELETED(0X000000E2, "This file contains a virus and cannot be opened. Due   to the nature of this virus, the file has been removed from this location."),
	ERROR_PIPE_LOCAL(0X000000E5, "The pipe is local."),
	ERROR_BAD_PIPE(0X000000E6, "The pipe state is invalid."),
	ERROR_PIPE_BUSY(0X000000E7, "All pipe instances are busy."),
	ERROR_NO_DATA(0X000000E8, "The pipe is being closed."),
	ERROR_PIPE_NOT_CONNECTED(0X000000E9, "No process is on the other end of the pipe."),
	ERROR_MORE_DATA(0X000000EA, "More data is available."),
	ERROR_VC_DISCONNECTED(0X000000F0, "The session was canceled."),
	ERROR_INVALID_EA_NAME(0X000000FE, "The specified extended attribute name was invalid."),
	ERROR_EA_LIST_INCONSISTENT(0X000000FF, "The extended attributes are inconsistent."),
	WAIT_TIMEOUT(0X00000102, "The wait operation timed out."),
	ERROR_NO_MORE_ITEMS(0X00000103, "No more data is available."),
	ERROR_CANNOT_COPY(0X0000010A, "The copy functions cannot be used."),
	ERROR_DIRECTORY(0X0000010B, "The directory name is invalid."),
	ERROR_EAS_DIDNT_FIT(0X00000113, "The extended attributes did not fit in the buffer."),
	ERROR_EA_FILE_CORRUPT(0X00000114, "The extended attribute file on the mounted file system   is corrupt."),
	ERROR_EA_TABLE_FULL(0X00000115, "The extended attribute table file is full."),
	ERROR_INVALID_EA_HANDLE(0X00000116, "The specified extended attribute handle is invalid."),
	ERROR_EAS_NOT_SUPPORTED(0X0000011A, "The mounted file system does not support extended   attributes."),
	ERROR_NOT_OWNER(0X00000120, "Attempt to release mutex not owned by caller."),
	ERROR_TOO_MANY_POSTS(0X0000012A, "Too many posts were made to a semaphore."),
	ERROR_PARTIAL_COPY(0X0000012B, "Only part of a ReadProcessMemory or WriteProcessMemory   request was completed."),
	ERROR_OPLOCK_NOT_GRANTED(0X0000012C, "The oplock request is denied."),
	ERROR_INVALID_OPLOCK_PROTOCOL(0X0000012D, "An invalid oplock acknowledgment was received by the   system."),
	ERROR_DISK_TOO_FRAGMENTED(0X0000012E, "The volume is too fragmented to complete this   operation."),
	ERROR_DELETE_PENDING(0X0000012F, "The file cannot be opened because it is in the process   of being deleted."),
	ERROR_MR_MID_NOT_FOUND(0X0000013D, "The system cannot find message text for message number   0x%1 in the message file for %2."),
	ERROR_SCOPE_NOT_FOUND(0X0000013E, "The scope specified was not found."),
	ERROR_FAIL_NOACTION_REBOOT(0X0000015E, "No action was taken because a system reboot is   required."),
	ERROR_FAIL_SHUTDOWN(0X0000015F, "The shutdown operation failed."),
	ERROR_FAIL_RESTART(0X00000160, "The restart operation failed."),
	ERROR_MAX_SESSIONS_REACHED(0X00000161, "The maximum number of sessions has been reached."),
	ERROR_THREAD_MODE_ALREADY_BACKGROUND(0X00000190, "The thread is already in background processing mode."),
	ERROR_THREAD_MODE_NOT_BACKGROUND(0X00000191, "The thread is not in background processing mode."),
	ERROR_PROCESS_MODE_ALREADY_BACKGROUND(0X00000192, "The process is already in background processing mode."),
	ERROR_PROCESS_MODE_NOT_BACKGROUND(0X00000193, "The process is not in background processing mode."),
	ERROR_INVALID_ADDRESS(0X000001E7, "Attempt to access invalid address."),
	ERROR_USER_PROFILE_LOAD(0X000001F4, "User profile cannot be loaded."),
	ERROR_ARITHMETIC_OVERFLOW(0X00000216, "Arithmetic result exceeded 32 bits."),
	ERROR_PIPE_CONNECTED(0X00000217, "There is a process on the other end of the pipe."),
	ERROR_PIPE_LISTENING(0X00000218, "Waiting for a process to open the other end of the   pipe."),
	ERROR_VERIFIER_STOP(0X00000219, "Application verifier has found an error in the current   process."),
	ERROR_ABIOS_ERROR(0X0000021A, "An error occurred in the ABIOS subsystem."),
	ERROR_WX86_WARNING(0X0000021B, "A warning occurred in the WX86 subsystem."),
	ERROR_WX86_ERROR(0X0000021C, "An error occurred in the WX86 subsystem."),
	ERROR_TIMER_NOT_CANCELED(0X0000021D, "An attempt was made to cancel or set a timer that has   an associated asynchronous procedure call (APC) and the subject thread is not   the thread that originally set the timer with an associated APC routine."),
	ERROR_UNWIND(0X0000021E, "Unwind exception code."),
	ERROR_BAD_STACK(0X0000021F, "An invalid or unaligned stack was encountered during   an unwind operation."),
	ERROR_INVALID_UNWIND_TARGET(0X00000220, "An invalid unwind target was encountered during an   unwind operation."),
	ERROR_INVALID_PORT_ATTRIBUTES(0X00000221, "Invalid object attributes specified to NtCreatePort or   invalid port attributes specified to NtConnectPort."),
	ERROR_PORT_MESSAGE_TOO_LONG(0X00000222, "Length of message passed to NtRequestPort or   NtRequestWaitReplyPort was longer than the maximum message allowed by the   port."),
	ERROR_INVALID_QUOTA_LOWER(0X00000223, "An attempt was made to lower a quota limit below the   current usage."),
	ERROR_DEVICE_ALREADY_ATTACHED(0X00000224, "An attempt was made to attach to a device that was   already attached to another device."),
	ERROR_INSTRUCTION_MISALIGNMENT(0X00000225, "An attempt was made to execute an instruction at an   unaligned address, and the host system does not support unaligned instruction   references."),
	ERROR_PROFILING_NOT_STARTED(0X00000226, "Profiling not started."),
	ERROR_PROFILING_NOT_STOPPED(0X00000227, "Profiling not stopped."),
	ERROR_COULD_NOT_INTERPRET(0X00000228, "The passed ACL did not contain the minimum required   information."),
	ERROR_PROFILING_AT_LIMIT(0X00000229, "The number of active profiling objects is at the   maximum and no more can be started."),
	ERROR_CANT_WAIT(0X0000022A, "Used to indicate that an operation cannot continue   without blocking for I/O."),
	ERROR_CANT_TERMINATE_SELF(0X0000022B, "Indicates that a thread attempted to terminate itself   by default (called NtTerminateThread with NULL) and it was the last thread in   the current process."),
	ERROR_UNEXPECTED_MM_CREATE_ERR(0X0000022C, "If an MM error is returned that is not defined in the   standard FsRtl filter, it is converted to one of the following errors that is   guaranteed to be in the filter. In this case, information is lost; however,   the filter correctly handles the exception."),
	ERROR_UNEXPECTED_MM_MAP_ERROR(0X0000022D, "If an MM error is returned that is not defined in the   standard FsRtl filter, it is converted to one of the following errors that is   guaranteed to be in the filter. In this case, information is lost; however,   the filter correctly handles the exception."),
	ERROR_UNEXPECTED_MM_EXTEND_ERR(0X0000022E, "If an MM error is returned that is not defined in the   standard FsRtl filter, it is converted to one of the following errors that is   guaranteed to be in the filter. In this case, information is lost; however,   the filter correctly handles the exception."),
	ERROR_BAD_FUNCTION_TABLE(0X0000022F, "A malformed function table was encountered during an   unwind operation."),
	ERROR_NO_GUID_TRANSLATION(0X00000230, "Indicates that an attempt was made to assign   protection to a file system file or directory and one of the SIDs in the security   descriptor could not be translated into a GUID that could be stored by the   file system. This causes the protection attempt to fail, which might cause a   file creation attempt to fail."),
	ERROR_INVALID_LDT_SIZE(0X00000231, "Indicates that an attempt was made to grow a local   domain table (LDT) by setting its size, or that the size was not an even   number of selectors."),
	ERROR_INVALID_LDT_OFFSET(0X00000233, "Indicates that the starting value for the LDT   information was not an integral multiple of the selector size."),
	ERROR_INVALID_LDT_DESCRIPTOR(0X00000234, "Indicates that the user supplied an invalid descriptor   when trying to set up LDT descriptors."),
	ERROR_TOO_MANY_THREADS(0X00000235, "Indicates a process has too many threads to perform   the requested action. For example, assignment of a primary token can be   performed only when a process has zero or one threads."),
	ERROR_THREAD_NOT_IN_PROCESS(0X00000236, "An attempt was made to operate on a thread within a   specific process, but the thread specified is not in the process specified."),
	ERROR_PAGEFILE_QUOTA_EXCEEDED(0X00000237, "Page file quota was exceeded."),
	ERROR_LOGON_SERVER_CONFLICT(0X00000238, "The Netlogon service cannot start because another   Netlogon service running in the domain conflicts with the specified role."),
	ERROR_SYNCHRONIZATION_REQUIRED(0X00000239, "On applicable Windows Server releases, the Security   Accounts Manager (SAM) database is significantly out of synchronization with   the copy on the domain controller. A complete synchronization is required."),
	ERROR_NET_OPEN_FAILED(0X0000023A, "The NtCreateFile API failed. This error should never   be returned to an application, it is a place holder for the Windows LAN   Manager Redirector to use in its internal error mapping routines."),
	ERROR_IO_PRIVILEGE_FAILED(0X0000023B, "{Privilege Failed} The I/O permissions for the process   could not be changed."),
	ERROR_CONTROL_C_EXIT(0X0000023C, "{Application Exit by CTRL+C} The application   terminated as a result of a CTRL+C."),
	ERROR_MISSING_SYSTEMFILE(0X0000023D, "{Missing System File} The required system file %hs is   bad or missing."),
	ERROR_UNHANDLED_EXCEPTION(0X0000023E, "{Application Error} The exception %s (0x%08lx)   occurred in the application at location 0x%08lx."),
	ERROR_APP_INIT_FAILURE(0X0000023F, "{Application Error} The application failed to   initialize properly (0x%lx). Click OK to terminate the application."),
	ERROR_PAGEFILE_CREATE_FAILED(0X00000240, "{Unable to Create Paging File} The creation of the   paging file %hs failed (%lx). The requested size was %ld."),
	ERROR_INVALID_IMAGE_HASH(0X00000241, "The hash for the image cannot be found in the system   catalogs. The image is likely corrupt or the victim of tampering."),
	ERROR_NO_PAGEFILE(0X00000242, "{No Paging File Specified} No paging file was   specified in the system configuration."),
	ERROR_ILLEGAL_FLOAT_CONTEXT(0X00000243, "{EXCEPTION} A real-mode application issued a   floating-point instruction, and floating-point hardware is not present."),
	ERROR_NO_EVENT_PAIR(0X00000244, "An event pair synchronization operation was performed   using the thread-specific client/server event pair object, but no event pair   object was associated with the thread."),
	ERROR_DOMAIN_CTRLR_CONFIG_ERROR(0X00000245, "A domain server has an incorrect configuration."),
	ERROR_ILLEGAL_CHARACTER(0X00000246, "An illegal character was encountered. For a multibyte   character set, this includes a lead byte without a succeeding trail byte. For   the Unicode character set, this includes the characters 0xFFFF and 0xFFFE."),
	ERROR_UNDEFINED_CHARACTER(0X00000247, "The Unicode character is not defined in the Unicode   character set installed on the system."),
	ERROR_FLOPPY_VOLUME(0X00000248, "The paging file cannot be created on a floppy disk."),
	ERROR_BIOS_FAILED_TO_CONNECT_INTERRUPT(0X00000249, "The system bios failed to connect a system interrupt   to the device or bus for which the device is connected."),
	ERROR_BACKUP_CONTROLLER(0X0000024A, "This operation is only allowed for the primary domain   controller (PDC) of the domain."),
	ERROR_MUTANT_LIMIT_EXCEEDED(0X0000024B, "An attempt was made to acquire a mutant such that its   maximum count would have been exceeded."),
	ERROR_FS_DRIVER_REQUIRED(0X0000024C, "A volume has been accessed for which a file system   driver is required that has not yet been loaded."),
	ERROR_CANNOT_LOAD_REGISTRY_FILE(0X0000024D, "{Registry File Failure} The registry cannot load the   hive (file): %hs or its log or alternate. It is corrupt, absent, or not   writable."),
	ERROR_DEBUG_ATTACH_FAILED(0X0000024E, "{Unexpected Failure in DebugActiveProcess} An   unexpected failure occurred while processing a DebugActiveProcess API   request. Choosing OK will terminate the process, and choosing Cancel will   ignore the error."),
	ERROR_SYSTEM_PROCESS_TERMINATED(0X0000024F, "{Fatal System Error} The %hs system process terminated   unexpectedly with a status of 0x%08x (0x%08x 0x%08x). The system has been shut   down."),
	ERROR_DATA_NOT_ACCEPTED(0X00000250, "{Data Not Accepted} The transport driver interface   (TDI) client could not handle the data received during an indication."),
	ERROR_VDM_HARD_ERROR(0X00000251, "The NT Virtual DOS Machine (NTVDM) encountered a hard error."),
	ERROR_DRIVER_CANCEL_TIMEOUT(0X00000252, "{Cancel Timeout} The driver %hs failed to complete a   canceled I/O request in the allotted time."),
	ERROR_REPLY_MESSAGE_MISMATCH(0X00000253, "{Reply Message Mismatch} An attempt was made to reply   to a local procedure call (LPC) message, but the thread specified by the   client ID in the message was not waiting on that message."),
	ERROR_LOST_WRITEBEHIND_DATA(0X00000254, "{Delayed Write Failed} Windows was unable to save all   the data for the file %hs. The data has been lost. This error might be caused   by a failure of your computer hardware or network connection. Try to save   this file elsewhere."),
	ERROR_CLIENT_SERVER_PARAMETERS_INVALID(0X00000255, "The parameters passed to the server in the   client/server shared memory window were invalid. Too much data might have   been put in the shared memory window."),
	ERROR_NOT_TINY_STREAM(0X00000256, "The stream is not a tiny stream."),
	ERROR_STACK_OVERFLOW_READ(0X00000257, "The request must be handled by the stack overflow   code."),
	ERROR_CONVERT_TO_LARGE(0X00000258, "Internal OFS status codes indicating how an allocation   operation is handled. Either it is retried after the containing onode is   moved or the extent stream is converted to a large stream."),
	ERROR_FOUND_OUT_OF_SCOPE(0X00000259, "The attempt to find the object found an object   matching by ID on the volume but it is out of the scope of the handle used   for the operation."),
	ERROR_ALLOCATE_BUCKET(0X0000025A, "The bucket array must be grown. Retry transaction   after doing so."),
	ERROR_MARSHALL_OVERFLOW(0X0000025B, "The user/kernel marshaling buffer has overflowed."),
	ERROR_INVALID_VARIANT(0X0000025C, "The supplied variant structure contains invalid data."),
	ERROR_BAD_COMPRESSION_BUFFER(0X0000025D, "The specified buffer contains ill-formed data."),
	ERROR_AUDIT_FAILED(0X0000025E, "{Audit Failed} An attempt to generate a security audit   failed."),
	ERROR_TIMER_RESOLUTION_NOT_SET(0X0000025F, "The timer resolution was not previously set by the   current process."),
	ERROR_INSUFFICIENT_LOGON_INFO(0X00000260, "There is insufficient account information to log you   on."),
	ERROR_BAD_DLL_ENTRYPOINT(0X00000261, "{Invalid DLL Entrypoint} The dynamic link library %hs   is not written correctly. The stack pointer has been left in an inconsistent   state. The entry point should be declared as WINAPI or STDCALL. Select YES to   fail the DLL load. Select NO to continue execution. Selecting NO can cause   the application to operate incorrectly."),
	ERROR_BAD_SERVICE_ENTRYPOINT(0X00000262, "{Invalid Service Callback Entrypoint} The %hs service   is not written correctly. The stack pointer has been left in an inconsistent   state. The callback entry point should be declared as WINAPI or STDCALL.   Selecting OK will cause the service to continue operation. However, the   service process might operate incorrectly."),
	ERROR_IP_ADDRESS_CONFLICT1(0X00000263, "There is an IP address conflict with another system on   the network."),
	ERROR_IP_ADDRESS_CONFLICT2(0X00000264, "There is an IP address conflict with another system on   the network."),
	ERROR_REGISTRY_QUOTA_LIMIT(0X00000265, "{Low On Registry Space} The system has reached the   maximum size allowed for the system part of the registry. Additional storage   requests will be ignored."),
	ERROR_NO_CALLBACK_ACTIVE(0X00000266, "A callback return system service cannot be executed   when no callback is active."),
	ERROR_PWD_TOO_SHORT(0X00000267, "The password provided is too short to meet the policy   of your user account. Choose a longer password."),
	ERROR_PWD_TOO_RECENT(0X00000268, "The policy of your user account does not allow you to   change passwords too frequently. This is done to prevent users from changing   back to a familiar, but potentially discovered, password. If you feel your   password has been compromised, contact your administrator immediately to have   a new one assigned."),
	ERROR_PWD_HISTORY_CONFLICT(0X00000269, "You have attempted to change your password to one that   you have used in the past. The policy of your user account does not allow   this. Select a password that you have not previously used."),
	ERROR_UNSUPPORTED_COMPRESSION(0X0000026A, "The specified compression format is unsupported."),
	ERROR_INVALID_HW_PROFILE(0X0000026B, "The specified hardware profile configuration is   invalid."),
	ERROR_INVALID_PLUGPLAY_DEVICE_PATH(0X0000026C, "The specified Plug and Play registry device path is   invalid."),
	ERROR_QUOTA_LIST_INCONSISTENT(0X0000026D, "The specified quota list is internally inconsistent   with its descriptor."),
	ERROR_EVALUATION_EXPIRATION(0X0000026E, "{Windows Evaluation Notification} The evaluation   period for this installation of Windows has expired. This system will shut   down in 1 hour. To restore access to this installation of Windows, upgrade   this installation using a licensed distribution of this product."),
	ERROR_ILLEGAL_DLL_RELOCATION(0X0000026F, "{Illegal System DLL Relocation} The system DLL %hs was   relocated in memory. The application will not run properly. The relocation   occurred because the DLL %hs occupied an address range reserved for Windows   system DLLs. The vendor supplying the DLL should be contacted for a new DLL."),
	ERROR_DLL_INIT_FAILED_LOGOFF(0X00000270, "{DLL Initialization Failed} The application failed to   initialize because the window station is shutting down."),
	ERROR_VALIDATE_CONTINUE(0X00000271, "The validation process needs to continue on to the   next step."),
	ERROR_NO_MORE_MATCHES(0X00000272, "There are no more matches for the current index   enumeration."),
	ERROR_RANGE_LIST_CONFLICT(0X00000273, "The range could not be added to the range list because   of a conflict."),
	ERROR_SERVER_SID_MISMATCH(0X00000274, "The server process is running under a SID different   than that required by the client."),
	ERROR_CANT_ENABLE_DENY_ONLY(0X00000275, "A group marked use for deny only cannot be enabled."),
	ERROR_FLOAT_MULTIPLE_FAULTS(0X00000276, "{EXCEPTION} Multiple floating point faults."),
	ERROR_FLOAT_MULTIPLE_TRAPS(0X00000277, "{EXCEPTION} Multiple floating point traps."),
	ERROR_NOINTERFACE(0X00000278, "The requested interface is not supported."),
	ERROR_DRIVER_FAILED_SLEEP(0X00000279, "{System Standby Failed} The driver %hs does not   support standby mode. Updating this driver might allow the system to go to   standby mode."),
	ERROR_CORRUPT_SYSTEM_FILE(0X0000027A, "The system file %1 has become corrupt and has been   replaced."),
	ERROR_COMMITMENT_MINIMUM(0X0000027B, "{Virtual Memory Minimum Too Low} Your system is low on   virtual memory. Windows is increasing the size of your virtual memory paging   file. During this process, memory requests for some applications might be   denied. For more information, see Help."),
	ERROR_PNP_RESTART_ENUMERATION(0X0000027C, "A device was removed so enumeration must be restarted."),
	ERROR_SYSTEM_IMAGE_BAD_SIGNATURE(0X0000027D, "{Fatal System Error} The system image %s is not   properly signed. The file has been replaced with the signed file. The system   has been shut down."),
	ERROR_PNP_REBOOT_REQUIRED(0X0000027E, "Device will not start without a reboot."),
	ERROR_INSUFFICIENT_POWER(0X0000027F, "There is not enough power to complete the requested   operation."),
	ERROR_SYSTEM_SHUTDOWN(0X00000281, "The system is in the process of shutting down."),
	ERROR_PORT_NOT_SET(0X00000282, "An attempt to remove a process DebugPort was made, but   a port was not already associated with the process."),
	ERROR_DS_VERSION_CHECK_FAILURE(0X00000283, "This version of Windows is not compatible with the   behavior version of directory forest, domain, or domain controller."),
	ERROR_RANGE_NOT_FOUND(0X00000284, "The specified range could not be found in the range   list."),
	ERROR_NOT_SAFE_MODE_DRIVER(0X00000286, "The driver was not loaded because the system is   booting into safe mode."),
	ERROR_FAILED_DRIVER_ENTRY(0X00000287, "The driver was not loaded because it failed its   initialization call."),
	ERROR_DEVICE_ENUMERATION_ERROR(0X00000288, "The device encountered an error while applying power   or reading the device configuration. This might be caused by a failure of   your hardware or by a poor connection."),
	ERROR_MOUNT_POINT_NOT_RESOLVED(0X00000289, "The create operation failed because the name contained   at least one mount point that resolves to a volume to which the specified   device object is not attached."),
	ERROR_INVALID_DEVICE_OBJECT_PARAMETER(0X0000028A, "The device object parameter is either not a valid   device object or is not attached to the volume specified by the file name."),
	ERROR_MCA_OCCURED(0X0000028B, "A machine check error has occurred. Check the system   event log for additional information."),
	ERROR_DRIVER_DATABASE_ERROR(0X0000028C, "There was an error [%2] processing the driver   database."),
	ERROR_SYSTEM_HIVE_TOO_LARGE(0X0000028D, "The system hive size has exceeded its limit."),
	ERROR_DRIVER_FAILED_PRIOR_UNLOAD(0X0000028E, "The driver could not be loaded because a previous   version of the driver is still in memory."),
	ERROR_VOLSNAP_PREPARE_HIBERNATE(0X0000028F, "{Volume Shadow Copy Service} Wait while the Volume   Shadow Copy Service prepares volume %hs for hibernation."),
	ERROR_HIBERNATION_FAILURE(0X00000290, "The system has failed to hibernate (the error code is   %hs). Hibernation will be disabled until the system is restarted."),
	ERROR_FILE_SYSTEM_LIMITATION(0X00000299, "The requested operation could not be completed due to   a file system limitation."),
	ERROR_ASSERTION_FAILURE(0X0000029C, "An assertion failure has occurred."),
	ERROR_ACPI_ERROR(0X0000029D, "An error occurred in the Advanced Configuration and   Power Interface (ACPI) subsystem."),
	ERROR_WOW_ASSERTION(0X0000029E, "WOW assertion error."),
	ERROR_PNP_BAD_MPS_TABLE(0X0000029F, "A device is missing in the system BIOS MultiProcessor   Specification (MPS) table. This device will not be used. Contact your system   vendor for system BIOS update."),
	ERROR_PNP_TRANSLATION_FAILED(0X000002A0, "A translator failed to translate resources."),
	ERROR_PNP_IRQ_TRANSLATION_FAILED(0X000002A1, "An interrupt request (IRQ) translator failed to   translate resources."),
	ERROR_PNP_INVALID_ID(0X000002A2, "Driver %2 returned invalid ID for a child device (%3)."),
	ERROR_WAKE_SYSTEM_DEBUGGER(0X000002A3, "{Kernel Debugger Awakened} the system debugger was   awakened by an interrupt."),
	ERROR_HANDLES_CLOSED(0X000002A4, "{Handles Closed} Handles to objects have been   automatically closed because of the requested operation."),
	ERROR_EXTRANEOUS_INFORMATION(0X000002A5, "{Too Much Information} The specified ACL contained   more information than was expected."),
	ERROR_RXACT_COMMIT_NECESSARY(0X000002A6, "This warning level status indicates that the   transaction state already exists for the registry subtree, but that a   transaction commit was previously aborted. The commit has NOT been completed,   but it has not been rolled back either (so it can still be committed if   desired)."),
	ERROR_MEDIA_CHECK(0X000002A7, "{Media Changed} The media might have changed."),
	ERROR_GUID_SUBSTITUTION_MADE(0X000002A8, "{GUID Substitution} During the translation of a GUID   to a Windows SID, no administratively defined GUID prefix was found. A   substitute prefix was used, which will not compromise system security.   However, this might provide more restrictive access than intended."),
	ERROR_STOPPED_ON_SYMLINK(0X000002A9, "The create operation stopped after reaching a symbolic   link."),
	ERROR_LONGJUMP(0X000002AA, "A long jump has been executed."),
	ERROR_PLUGPLAY_QUERY_VETOED(0X000002AB, "The Plug and Play query operation was not successful."),
	ERROR_UNWIND_CONSOLIDATE(0X000002AC, "A frame consolidation has been executed."),
	ERROR_REGISTRY_HIVE_RECOVERED(0X000002AD, "{Registry Hive Recovered} Registry hive (file): %hs   was corrupted and it has been recovered. Some data might have been lost."),
	ERROR_DLL_MIGHT_BE_INSECURE(0X000002AE, "The application is attempting to run executable code   from the module %hs. This might be insecure. An alternative, %hs, is   available. Should the application use the secure module %hs?"),
	ERROR_DLL_MIGHT_BE_INCOMPATIBLE(0X000002AF, "The application is loading executable code from the   module %hs. This is secure, but might be incompatible with previous releases   of the operating system. An alternative, %hs, is available. Should the   application use the secure module %hs?"),
	ERROR_DBG_EXCEPTION_NOT_HANDLED(0X000002B0, "Debugger did not handle the exception."),
	ERROR_DBG_REPLY_LATER(0X000002B1, "Debugger will reply later."),
	ERROR_DBG_UNABLE_TO_PROVIDE_HANDLE(0X000002B2, "Debugger cannot provide handle."),
	ERROR_DBG_TERMINATE_THREAD(0X000002B3, "Debugger terminated thread."),
	ERROR_DBG_TERMINATE_PROCESS(0X000002B4, "Debugger terminated process."),
	ERROR_DBG_CONTROL_C(0X000002B5, "Debugger got control C."),
	ERROR_DBG_PRINTEXCEPTION_C(0X000002B6, "Debugger printed exception on control C."),
	ERROR_DBG_RIPEXCEPTION(0X000002B7, "Debugger received Routing Information Protocol (RIP)   exception."),
	ERROR_DBG_CONTROL_BREAK(0X000002B8, "Debugger received control break."),
	ERROR_DBG_COMMAND_EXCEPTION(0X000002B9, "Debugger command communication exception."),
	ERROR_OBJECT_NAME_EXISTS(0X000002BA, "{Object Exists} An attempt was made to create an   object and the object name already existed."),
	ERROR_THREAD_WAS_SUSPENDED(0X000002BB, "{Thread Suspended} A thread termination occurred while   the thread was suspended. The thread was resumed and termination proceeded."),
	ERROR_IMAGE_NOT_AT_BASE(0X000002BC, "{Image Relocated} An image file could not be mapped at   the address specified in the image file. Local fixes must be performed on   this image."),
	ERROR_RXACT_STATE_CREATED(0X000002BD, "This informational level status indicates that a   specified registry subtree transaction state did not yet exist and had to be   created."),
	ERROR_SEGMENT_NOTIFICATION(0X000002BE, "{Segment Load} A virtual DOS machine (VDM) is loading,   unloading, or moving an MS-DOS or Win16 program segment image. An exception   is raised so a debugger can load, unload, or track symbols and breakpoints   within these 16-bit segments."),
	ERROR_BAD_CURRENT_DIRECTORY(0X000002BF, "{Invalid Current Directory} The process cannot switch   to the startup current directory %hs. Select OK to set current directory to   %hs, or select CANCEL to exit."),
	ERROR_FT_READ_RECOVERY_FROM_BACKUP(0X000002C0, "{Redundant Read} To satisfy a read request, the NT   fault-tolerant file system successfully read the requested data from a   redundant copy. This was done because the file system encountered a failure   on a member of the fault-tolerant volume, but it was unable to reassign the   failing area of the device."),
	ERROR_FT_WRITE_RECOVERY(0X000002C1, "{Redundant Write} To satisfy a write request, the   Windows NT operating system fault-tolerant file system successfully wrote a   redundant copy of the information. This was done because the file system   encountered a failure on a member of the fault-tolerant volume, but it was   not able to reassign the failing area of the device."),
	ERROR_IMAGE_MACHINE_TYPE_MISMATCH(0X000002C2, "{Machine Type Mismatch} The image file %hs is valid,   but is for a machine type other than the current machine. Select OK to   continue, or CANCEL to fail the DLL load."),
	ERROR_RECEIVE_PARTIAL(0X000002C3, "{Partial Data Received} The network transport returned   partial data to its client. The remaining data will be sent later."),
	ERROR_RECEIVE_EXPEDITED(0X000002C4, "{Expedited Data Received} The network transport   returned data to its client that was marked as expedited by the remote   system."),
	ERROR_RECEIVE_PARTIAL_EXPEDITED(0X000002C5, "{Partial Expedited Data Received} The network   transport returned partial data to its client and this data was marked as   expedited by the remote system. The remaining data will be sent later."),
	ERROR_EVENT_DONE(0X000002C6, "{TDI Event Done} The TDI indication has completed   successfully."),
	ERROR_EVENT_PENDING(0X000002C7, "{TDI Event Pending} The TDI indication has entered the   pending state."),
	ERROR_CHECKING_FILE_SYSTEM(0X000002C8, "Checking file system on %wZ."),
	ERROR_FATAL_APP_EXIT(0X000002C9, "{Fatal Application Exit} %hs."),
	ERROR_PREDEFINED_HANDLE(0X000002CA, "The specified registry key is referenced by a   predefined handle."),
	ERROR_WAS_UNLOCKED(0X000002CB, "{Page Unlocked} The page protection of a locked page   was changed to 'No Access' and the page was unlocked from memory and from the   process."),
	ERROR_WAS_LOCKED(0X000002CD, "{Page Locked} One of the pages to lock was already   locked."),
	ERROR_ALREADY_WIN32(0X000002CF, "The value already corresponds with a Win 32 error   code."),
	ERROR_IMAGE_MACHINE_TYPE_MISMATCH_EXE(0X000002D0, "{Machine Type Mismatch} The image file %hs is valid,   but is for a machine type other than the current machine."),
	ERROR_NO_YIELD_PERFORMED(0X000002D1, "A yield execution was performed and no thread was   available to run."),
	ERROR_TIMER_RESUME_IGNORED(0X000002D2, "The resume flag to a timer API was ignored."),
	ERROR_ARBITRATION_UNHANDLED(0X000002D3, "The arbiter has deferred arbitration of these   resources to its parent."),
	ERROR_CARDBUS_NOT_SUPPORTED(0X000002D4, "The inserted CardBus device cannot be started because   of a configuration error on %hs."),
	ERROR_MP_PROCESSOR_MISMATCH(0X000002D5, "The CPUs in this multiprocessor system are not all the   same revision level. To use all processors the operating system restricts   itself to the features of the least capable processor in the system. If   problems occur with this system, contact the CPU manufacturer to see if this   mix of processors is supported."),
	ERROR_HIBERNATED(0X000002D6, "The system was put into hibernation."),
	ERROR_RESUME_HIBERNATION(0X000002D7, "The system was resumed from hibernation."),
	ERROR_FIRMWARE_UPDATED(0X000002D8, "Windows has detected that the system firmware (BIOS)   was updated (previous firmware date = %2, current firmware date %3)."),
	ERROR_DRIVERS_LEAKING_LOCKED_PAGES(0X000002D9, "A device driver is leaking locked I/O pages, causing   system degradation. The system has automatically enabled a tracking code to   try and catch the culprit."),
	ERROR_WAKE_SYSTEM(0X000002DA, "The system has awoken."),
	ERROR_ABANDONED_WAIT_0(0X000002DF, "The call failed because the handle associated with it   was closed."),
	ERROR_ELEVATION_REQUIRED(0X000002E4, "The requested operation requires elevation."),
	ERROR_REPARSE(0X000002E5, "A reparse should be performed by the object manager   because the name of the file resulted in a symbolic link."),
	ERROR_OPLOCK_BREAK_IN_PROGRESS(0X000002E6, "An open/create operation completed while an oplock   break is underway."),
	ERROR_VOLUME_MOUNTED(0X000002E7, "A new volume has been mounted by a file system."),
	ERROR_RXACT_COMMITTED(0X000002E8, "This success level status indicates that the   transaction state already exists for the registry subtree, but that a   transaction commit was previously aborted. The commit has now been completed."),
	ERROR_NOTIFY_CLEANUP(0X000002E9, "This indicates that a notify change request has been   completed due to closing the handle which made the notify change request."),
	ERROR_PRIMARY_TRANSPORT_CONNECT_FAILED(0X000002EA, "{Connect Failure on Primary Transport} An attempt was   made to connect to the remote server %hs on the primary transport, but the   connection failed. The computer was able to connect on a secondary transport."),
	ERROR_PAGE_FAULT_TRANSITION(0X000002EB, "Page fault was a transition fault."),
	ERROR_PAGE_FAULT_DEMAND_ZERO(0X000002EC, "Page fault was a demand zero fault."),
	ERROR_PAGE_FAULT_COPY_ON_WRITE(0X000002ED, "Page fault was a demand zero fault."),
	ERROR_PAGE_FAULT_GUARD_PAGE(0X000002EE, "Page fault was a demand zero fault."),
	ERROR_PAGE_FAULT_PAGING_FILE(0X000002EF, "Page fault was satisfied by reading from a secondary   storage device."),
	ERROR_CACHE_PAGE_LOCKED(0X000002F0, "Cached page was locked during operation."),
	ERROR_CRASH_DUMP(0X000002F1, "Crash dump exists in paging file."),
	ERROR_BUFFER_ALL_ZEROS(0X000002F2, "Specified buffer contains all zeros."),
	ERROR_REPARSE_OBJECT(0X000002F3, "A reparse should be performed by the object manager   because the name of the file resulted in a symbolic link."),
	ERROR_RESOURCE_REQUIREMENTS_CHANGED(0X000002F4, "The device has succeeded a query-stop and its resource   requirements have changed."),
	ERROR_TRANSLATION_COMPLETE(0X000002F5, "The translator has translated these resources into the   global space and no further translations should be performed."),
	ERROR_NOTHING_TO_TERMINATE(0X000002F6, "A process being terminated has no threads to   terminate."),
	ERROR_PROCESS_NOT_IN_JOB(0X000002F7, "The specified process is not part of a job."),
	ERROR_PROCESS_IN_JOB(0X000002F8, "The specified process is part of a job."),
	ERROR_VOLSNAP_HIBERNATE_READY(0X000002F9, "{Volume Shadow Copy Service} The system is now ready   for hibernation."),
	ERROR_FSFILTER_OP_COMPLETED_SUCCESSFULLY(0X000002FA, "A file system or file system filter driver has   successfully completed an FsFilter operation."),
	ERROR_INTERRUPT_VECTOR_ALREADY_CONNECTED(0X000002FB, "The specified interrupt vector was already connected."),
	ERROR_INTERRUPT_STILL_CONNECTED(0X000002FC, "The specified interrupt vector is still connected."),
	ERROR_WAIT_FOR_OPLOCK(0X000002FD, "An operation is blocked waiting for an oplock."),
	ERROR_DBG_EXCEPTION_HANDLED(0X000002FE, "Debugger handled exception."),
	ERROR_DBG_CONTINUE(0X000002FF, "Debugger continued."),
	ERROR_CALLBACK_POP_STACK(0X00000300, "An exception occurred in a user mode callback and the   kernel callback frame should be removed."),
	ERROR_COMPRESSION_DISABLED(0X00000301, "Compression is disabled for this volume."),
	ERROR_CANTFETCHBACKWARDS(0X00000302, "The data provider cannot fetch backward through a   result set."),
	ERROR_CANTSCROLLBACKWARDS(0X00000303, "The data provider cannot scroll backward through a   result set."),
	ERROR_ROWSNOTRELEASED(0X00000304, "The data provider requires that previously fetched   data is released before asking for more data."),
	ERROR_BAD_ACCESSOR_FLAGS(0X00000305, "The data provider was not able to interpret the flags   set for a column binding in an accessor."),
	ERROR_ERRORS_ENCOUNTERED(0X00000306, "One or more errors occurred while processing the   request."),
	ERROR_NOT_CAPABLE(0X00000307, "The implementation is not capable of performing the   request."),
	ERROR_REQUEST_OUT_OF_SEQUENCE(0X00000308, "The client of a component requested an operation that   is not valid given the state of the component instance."),
	ERROR_VERSION_PARSE_ERROR(0X00000309, "A version number could not be parsed."),
	ERROR_BADSTARTPOSITION(0X0000030A, "The iterator's start position is invalid."),
	ERROR_MEMORY_HARDWARE(0X0000030B, "The hardware has reported an uncorrectable memory   error."),
	ERROR_DISK_REPAIR_DISABLED(0X0000030C, "The attempted operation required self-healing to be   enabled."),
	ERROR_INSUFFICIENT_RESOURCE_FOR_SPECIFIED_SHARED_SECTION_SIZE(0X0000030D, "The Desktop heap encountered an error while allocating   session memory. There is more information in the system event log."),
	ERROR_SYSTEM_POWERSTATE_TRANSITION(0X0000030E, "The system power state is transitioning from %2 to %3."),
	ERROR_SYSTEM_POWERSTATE_COMPLEX_TRANSITION(0X0000030F, "The system power state is transitioning from %2 to %3   but could enter %4."),
	ERROR_MCA_EXCEPTION(0X00000310, "A thread is getting dispatched with MCA EXCEPTION   because of MCA."),
	ERROR_ACCESS_AUDIT_BY_POLICY(0X00000311, "Access to %1 is monitored by policy rule %2."),
	ERROR_ACCESS_DISABLED_NO_SAFER_UI_BY_POLICY(0X00000312, "Access to %1 has been restricted by your administrator   by policy rule %2."),
	ERROR_ABANDON_HIBERFILE(0X00000313, "A valid hibernation file has been invalidated and   should be abandoned."),
	ERROR_LOST_WRITEBEHIND_DATA_NETWORK_DISCONNECTED(0X00000314, "{Delayed Write Failed} Windows was unable to save all   the data for the file %hs; the data has been lost. This error can be caused   by network connectivity issues. Try to save this file elsewhere."),
	ERROR_LOST_WRITEBEHIND_DATA_NETWORK_SERVER_ERROR(0X00000315, "{Delayed Write Failed} Windows was unable to save all   the data for the file %hs; the data has been lost. This error was returned by   the server on which the file exists. Try to save this file elsewhere."),
	ERROR_LOST_WRITEBEHIND_DATA_LOCAL_DISK_ERROR(0X00000316, "{Delayed Write Failed} Windows was unable to save all   the data for the file %hs; the data has been lost. This error can be caused   if the device has been removed or the media is write-protected."),
	ERROR_EA_ACCESS_DENIED(0X000003E2, "Access to the extended attribute was denied."),
	ERROR_OPERATION_ABORTED(0X000003E3, "The I/O operation has been aborted because of either a   thread exit or an application request."),
	ERROR_IO_INCOMPLETE(0X000003E4, "Overlapped I/O event is not in a signaled state."),
	ERROR_IO_PENDING(0X000003E5, "Overlapped I/O operation is in progress."),
	ERROR_NOACCESS(0X000003E6, "Invalid access to memory location."),
	ERROR_SWAPERROR(0X000003E7, "Error performing in-page operation."),
	ERROR_STACK_OVERFLOW(0X000003E9, "Recursion too deep; the stack overflowed."),
	ERROR_INVALID_MESSAGE(0X000003EA, "The window cannot act on the sent message."),
	ERROR_CAN_NOT_COMPLETE(0X000003EB, "Cannot complete this function."),
	ERROR_INVALID_FLAGS(0X000003EC, "Invalid flags."),
	ERROR_UNRECOGNIZED_VOLUME(0X000003ED, "The volume does not contain a recognized file system.   Be sure that all required file system drivers are loaded and that the volume   is not corrupted."),
	ERROR_FILE_INVALID(0X000003EE, "The volume for a file has been externally altered so   that the opened file is no longer valid."),
	ERROR_FULLSCREEN_MODE(0X000003EF, "The requested operation cannot be performed in   full-screen mode."),
	ERROR_NO_TOKEN(0X000003F0, "An attempt was made to reference a token that does not   exist."),
	ERROR_BADDB(0X000003F1, "The configuration registry database is corrupt."),
	ERROR_BADKEY(0X000003F2, "The configuration registry key is invalid."),
	ERROR_CANTOPEN(0X000003F3, "The configuration registry key could not be opened."),
	ERROR_CANTREAD(0X000003F4, "The configuration registry key could not be read."),
	ERROR_CANTWRITE(0X000003F5, "The configuration registry key could not be written."),
	ERROR_REGISTRY_RECOVERED(0X000003F6, "One of the files in the registry database had to be   recovered by use of a log or alternate copy. The recovery was successful."),
	ERROR_REGISTRY_CORRUPT(0X000003F7, "The registry is corrupted. The structure of one of the   files containing registry data is corrupted, or the system's memory image of   the file is corrupted, or the file could not be recovered because the   alternate copy or log was absent or corrupted."),
	ERROR_REGISTRY_IO_FAILED(0X000003F8, "An I/O operation initiated by the registry failed and   cannot be recovered. The registry could not read in, write out, or flush one   of the files that contain the system's image of the registry."),
	ERROR_NOT_REGISTRY_FILE(0X000003F9, "The system attempted to load or restore a file into   the registry, but the specified file is not in a registry file format."),
	ERROR_KEY_DELETED(0X000003FA, "Illegal operation attempted on a registry key that has   been marked for deletion."),
	ERROR_NO_LOG_SPACE(0X000003FB, "System could not allocate the required space in a   registry log."),
	ERROR_KEY_HAS_CHILDREN(0X000003FC, "Cannot create a symbolic link in a registry key that   already has subkeys or values."),
	ERROR_CHILD_MUST_BE_VOLATILE(0X000003FD, "Cannot create a stable subkey under a volatile parent   key."),
	ERROR_NOTIFY_ENUM_DIR(0X000003FE, "A notify change request is being completed and the   information is not being returned in the caller's buffer. The caller now   needs to enumerate the files to find the changes."),
	ERROR_DEPENDENT_SERVICES_RUNNING(0X0000041B, "A stop control has been sent to a service that other   running services are dependent on."),
	ERROR_INVALID_SERVICE_CONTROL(0X0000041C, "The requested control is not valid for this service."),
	ERROR_SERVICE_REQUEST_TIMEOUT(0X0000041D, "The service did not respond to the start or control   request in a timely fashion."),
	ERROR_SERVICE_NO_THREAD(0X0000041E, "A thread could not be created for the service."),
	ERROR_SERVICE_DATABASE_LOCKED(0X0000041F, "The service database is locked."),
	ERROR_SERVICE_ALREADY_RUNNING(0X00000420, "An instance of the service is already running."),
	ERROR_INVALID_SERVICE_ACCOUNT(0X00000421, "The account name is invalid or does not exist, or the   password is invalid for the account name specified."),
	ERROR_SERVICE_DISABLED(0X00000422, "The service cannot be started, either because it is   disabled or because it has no enabled devices associated with it."),
	ERROR_CIRCULAR_DEPENDENCY(0X00000423, "Circular service dependency was specified."),
	ERROR_SERVICE_DOES_NOT_EXIST(0X00000424, "The specified service does not exist as an installed   service."),
	ERROR_SERVICE_CANNOT_ACCEPT_CTRL(0X00000425, "The service cannot accept control messages at this   time."),
	ERROR_SERVICE_NOT_ACTIVE(0X00000426, "The service has not been started."),
	ERROR_FAILED_SERVICE_CONTROLLER_CONNECT(0X00000427, "The service process could not connect to the service   controller."),
	ERROR_EXCEPTION_IN_SERVICE(0X00000428, "An exception occurred in the service when handling the   control request."),
	ERROR_DATABASE_DOES_NOT_EXIST(0X00000429, "The database specified does not exist."),
	ERROR_SERVICE_SPECIFIC_ERROR(0X0000042A, "The service has returned a service-specific error   code."),
	ERROR_PROCESS_ABORTED(0X0000042B, "The process terminated unexpectedly."),
	ERROR_SERVICE_DEPENDENCY_FAIL(0X0000042C, "The dependency service or group failed to start."),
	ERROR_SERVICE_LOGON_FAILED(0X0000042D, "The service did not start due to a logon failure."),
	ERROR_SERVICE_START_HANG(0X0000042E, "After starting, the service stopped responding in a   start-pending state."),
	ERROR_INVALID_SERVICE_LOCK(0X0000042F, "The specified service database lock is invalid."),
	ERROR_SERVICE_MARKED_FOR_DELETE(0X00000430, "The specified service has been marked for deletion."),
	ERROR_SERVICE_EXISTS(0X00000431, "The specified service already exists."),
	ERROR_ALREADY_RUNNING_LKG(0X00000432, "The system is currently running with the   last-known-good configuration."),
	ERROR_SERVICE_DEPENDENCY_DELETED(0X00000433, "The dependency service does not exist or has been   marked for deletion."),
	ERROR_BOOT_ALREADY_ACCEPTED(0X00000434, "The current boot has already been accepted for use as   the last-known-good control set."),
	ERROR_SERVICE_NEVER_STARTED(0X00000435, "No attempts to start the service have been made since   the last boot."),
	ERROR_DUPLICATE_SERVICE_NAME(0X00000436, "The name is already in use as either a service name or   a service display name."),
	ERROR_DIFFERENT_SERVICE_ACCOUNT(0X00000437, "The account specified for this service is different   from the account specified for other services running in the same process."),
	ERROR_CANNOT_DETECT_DRIVER_FAILURE(0X00000438, "Failure actions can only be set for Win32 services,   not for drivers."),
	ERROR_CANNOT_DETECT_PROCESS_ABORT(0X00000439, "This service runs in the same process as the service   control manager. Therefore, the service control manager cannot take action if   this service's process terminates unexpectedly."),
	ERROR_NO_RECOVERY_PROGRAM(0X0000043A, "No recovery program has been configured for this   service."),
	ERROR_SERVICE_NOT_IN_EXE(0X0000043B, "The executable program that this service is configured   to run in does not implement the service."),
	ERROR_NOT_SAFEBOOT_SERVICE(0X0000043C, "This service cannot be started in Safe Mode."),
	ERROR_END_OF_MEDIA(0X0000044C, "The physical end of the tape has been reached."),
	ERROR_FILEMARK_DETECTED(0X0000044D, "A tape access reached a filemark."),
	ERROR_BEGINNING_OF_MEDIA(0X0000044E, "The beginning of the tape or a partition was   encountered."),
	ERROR_SETMARK_DETECTED(0X0000044F, "A tape access reached the end of a set of files."),
	ERROR_NO_DATA_DETECTED(0X00000450, "No more data is on the tape."),
	ERROR_PARTITION_FAILURE(0X00000451, "Tape could not be partitioned."),
	ERROR_INVALID_BLOCK_LENGTH(0X00000452, "When accessing a new tape of a multivolume partition,   the current block size is incorrect."),
	ERROR_DEVICE_NOT_PARTITIONED(0X00000453, "Tape partition information could not be found when   loading a tape."),
	ERROR_UNABLE_TO_LOCK_MEDIA(0X00000454, "Unable to lock the media eject mechanism."),
	ERROR_UNABLE_TO_UNLOAD_MEDIA(0X00000455, "Unable to unload the media."),
	ERROR_MEDIA_CHANGED(0X00000456, "The media in the drive might have changed."),
	ERROR_BUS_RESET(0X00000457, "The I/O bus was reset."),
	ERROR_NO_MEDIA_IN_DRIVE(0X00000458, "No media in drive."),
	ERROR_NO_UNICODE_TRANSLATION(0X00000459, "No mapping for the Unicode character exists in the   target multibyte code page."),
	ERROR_DLL_INIT_FAILED(0X0000045A, "A DLL initialization routine failed."),
	ERROR_SHUTDOWN_IN_PROGRESS(0X0000045B, "A system shutdown is in progress."),
	ERROR_NO_SHUTDOWN_IN_PROGRESS(0X0000045C, "Unable to abort the system shutdown because no   shutdown was in progress."),
	ERROR_IO_DEVICE(0X0000045D, "The request could not be performed because of an I/O   device error."),
	ERROR_SERIAL_NO_DEVICE(0X0000045E, "No serial device was successfully initialized. The   serial driver will unload."),
	ERROR_IRQ_BUSY(0X0000045F, "Unable to open a device that was sharing an IRQ with   other devices. At least one other device that uses that IRQ was already   opened."),
	ERROR_MORE_WRITES(0X00000460, "A serial I/O operation was completed by another write   to the serial port. (The IOCTL_SERIAL_XOFF_COUNTER reached zero.)"),
	ERROR_COUNTER_TIMEOUT(0X00000461, "A serial I/O operation completed because the time-out   period expired. (The IOCTL_SERIAL_XOFF_COUNTER did not reach zero.)"),
	ERROR_FLOPPY_ID_MARK_NOT_FOUND(0X00000462, "No ID address mark was found on the floppy disk."),
	ERROR_FLOPPY_WRONG_CYLINDER(0X00000463, "Mismatch between the floppy disk sector ID field and   the floppy disk controller track address."),
	ERROR_FLOPPY_UNKNOWN_ERROR(0X00000464, "The floppy disk controller reported an error that is   not recognized by the floppy disk driver."),
	ERROR_FLOPPY_BAD_REGISTERS(0X00000465, "The floppy disk controller returned inconsistent   results in its registers."),
	ERROR_DISK_RECALIBRATE_FAILED(0X00000466, "While accessing the hard disk, a recalibrate operation   failed, even after retries."),
	ERROR_DISK_OPERATION_FAILED(0X00000467, "While accessing the hard disk, a disk operation failed   even after retries."),
	ERROR_DISK_RESET_FAILED(0X00000468, "While accessing the hard disk, a disk controller reset   was needed, but that also failed."),
	ERROR_EOM_OVERFLOW(0X00000469, "Physical end of tape encountered."),
	ERROR_NOT_ENOUGH_SERVER_MEMORY(0X0000046A, "Not enough server storage is available to process this   command."),
	ERROR_POSSIBLE_DEADLOCK(0X0000046B, "A potential deadlock condition has been detected."),
	ERROR_MAPPED_ALIGNMENT(0X0000046C, "The base address or the file offset specified does not   have the proper alignment."),
	ERROR_SET_POWER_STATE_VETOED(0X00000474, "An attempt to change the system power state was vetoed   by another application or driver."),
	ERROR_SET_POWER_STATE_FAILED(0X00000475, "The system BIOS failed an attempt to change the system   power state."),
	ERROR_TOO_MANY_LINKS(0X00000476, "An attempt was made to create more links on a file   than the file system supports."),
	ERROR_OLD_WIN_VERSION(0X0000047E, "The specified program requires a newer version of   Windows."),
	ERROR_APP_WRONG_OS(0X0000047F, "The specified program is not a Windows or MS-DOS   program."),
	ERROR_SINGLE_INSTANCE_APP(0X00000480, "Cannot start more than one instance of the specified   program."),
	ERROR_RMODE_APP(0X00000481, "The specified program was written for an earlier   version of Windows."),
	ERROR_INVALID_DLL(0X00000482, "One of the library files needed to run this   application is damaged."),
	ERROR_NO_ASSOCIATION(0X00000483, "No application is associated with the specified file   for this operation."),
	ERROR_DDE_FAIL(0X00000484, "An error occurred in sending the command to the   application."),
	ERROR_DLL_NOT_FOUND(0X00000485, "One of the library files needed to run this   application cannot be found."),
	ERROR_NO_MORE_USER_HANDLES(0X00000486, "The current process has used all of its system   allowance of handles for Windows manager objects."),
	ERROR_MESSAGE_SYNC_ONLY(0X00000487, "The message can be used only with synchronous   operations."),
	ERROR_SOURCE_ELEMENT_EMPTY(0X00000488, "The indicated source element has no media."),
	ERROR_DESTINATION_ELEMENT_FULL(0X00000489, "The indicated destination element already contains   media."),
	ERROR_ILLEGAL_ELEMENT_ADDRESS(0X0000048A, "The indicated element does not exist."),
	ERROR_MAGAZINE_NOT_PRESENT(0X0000048B, "The indicated element is part of a magazine that is   not present."),
	ERROR_DEVICE_REINITIALIZATION_NEEDED(0X0000048C, "The indicated device requires re-initialization due to   hardware errors."),
	ERROR_DEVICE_REQUIRES_CLEANING(0X0000048D, "The device has indicated that cleaning is required   before further operations are attempted."),
	ERROR_DEVICE_DOOR_OPEN(0X0000048E, "The device has indicated that its door is open."),
	ERROR_DEVICE_NOT_CONNECTED(0X0000048F, "The device is not connected."),
	ERROR_NOT_FOUND(0X00000490, "Element not found."),
	ERROR_NO_MATCH(0X00000491, "There was no match for the specified key in the index."),
	ERROR_SET_NOT_FOUND(0X00000492, "The property set specified does not exist on the   object."),
	ERROR_POINT_NOT_FOUND(0X00000493, "The point passed to GetMouseMovePoints is not in the   buffer."),
	ERROR_NO_TRACKING_SERVICE(0X00000494, "The tracking (workstation) service is not running."),
	ERROR_NO_VOLUME_ID(0X00000495, "The volume ID could not be found."),
	ERROR_UNABLE_TO_REMOVE_REPLACED(0X00000497, "Unable to remove the file to be replaced."),
	ERROR_UNABLE_TO_MOVE_REPLACEMENT(0X00000498, "Unable to move the replacement file to the file to be   replaced. The file to be replaced has retained its original name."),
	ERROR_UNABLE_TO_MOVE_REPLACEMENT_2(0X00000499, "Unable to move the replacement file to the file to be   replaced. The file to be replaced has been renamed using the backup name."),
	ERROR_JOURNAL_DELETE_IN_PROGRESS(0X0000049A, "The volume change journal is being deleted."),
	ERROR_JOURNAL_NOT_ACTIVE(0X0000049B, "The volume change journal is not active."),
	ERROR_POTENTIAL_FILE_FOUND(0X0000049C, "A file was found, but it might not be the correct   file."),
	ERROR_JOURNAL_ENTRY_DELETED(0X0000049D, "The journal entry has been deleted from the journal."),
	ERROR_SHUTDOWN_IS_SCHEDULED(0X000004A6, "A system shutdown has already been scheduled."),
	ERROR_SHUTDOWN_USERS_LOGGED_ON(0X000004A7, "The system shutdown cannot be initiated because there   are other users logged on to the computer."),
	ERROR_BAD_DEVICE(0X000004B0, "The specified device name is invalid."),
	ERROR_CONNECTION_UNAVAIL(0X000004B1, "The device is not currently connected but it is a   remembered connection."),
	ERROR_DEVICE_ALREADY_REMEMBERED(0X000004B2, "The local device name has a remembered connection to   another network resource."),
	ERROR_NO_NET_OR_BAD_PATH(0X000004B3, "The network path was either typed incorrectly, does   not exist, or the network provider is not currently available. Try retyping   the path or contact your network administrator."),
	ERROR_BAD_PROVIDER(0X000004B4, "The specified network provider name is invalid."),
	ERROR_CANNOT_OPEN_PROFILE(0X000004B5, "Unable to open the network connection profile."),
	ERROR_BAD_PROFILE(0X000004B6, "The network connection profile is corrupted."),
	ERROR_NOT_CONTAINER(0X000004B7, "Cannot enumerate a noncontainer."),
	ERROR_EXTENDED_ERROR(0X000004B8, "An extended error has occurred."),
	ERROR_INVALID_GROUPNAME(0X000004B9, "The format of the specified group name is invalid."),
	ERROR_INVALID_COMPUTERNAME(0X000004BA, "The format of the specified computer name is invalid."),
	ERROR_INVALID_EVENTNAME(0X000004BB, "The format of the specified event name is invalid."),
	ERROR_INVALID_DOMAINNAME(0X000004BC, "The format of the specified domain name is invalid."),
	ERROR_INVALID_SERVICENAME(0X000004BD, "The format of the specified service name is invalid."),
	ERROR_INVALID_NETNAME(0X000004BE, "The format of the specified network name is invalid."),
	ERROR_INVALID_SHARENAME(0X000004BF, "The format of the specified share name is invalid."),
	ERROR_INVALID_PASSWORDNAME(0X000004C0, "The format of the specified password is invalid."),
	ERROR_INVALID_MESSAGENAME(0X000004C1, "The format of the specified message name is invalid."),
	ERROR_INVALID_MESSAGEDEST(0X000004C2, "The format of the specified message destination is   invalid."),
	ERROR_SESSION_CREDENTIAL_CONFLICT(0X000004C3, "Multiple connections to a server or shared resource by   the same user, using more than one user name, are not allowed. Disconnect all   previous connections to the server or shared resource and try again."),
	ERROR_REMOTE_SESSION_LIMIT_EXCEEDED(0X000004C4, "An attempt was made to establish a session to a   network server, but there are already too many sessions established to that   server."),
	ERROR_DUP_DOMAINNAME(0X000004C5, "The workgroup or domain name is already in use by   another computer on the network."),
	ERROR_NO_NETWORK(0X000004C6, "The network is not present or not started."),
	ERROR_CANCELLED(0X000004C7, "The operation was canceled by the user."),
	ERROR_USER_MAPPED_FILE(0X000004C8, "The requested operation cannot be performed on a file   with a user-mapped section open."),
	ERROR_CONNECTION_REFUSED(0X000004C9, "The remote system refused the network connection."),
	ERROR_GRACEFUL_DISCONNECT(0X000004CA, "The network connection was gracefully closed."),
	ERROR_ADDRESS_ALREADY_ASSOCIATED(0X000004CB, "The network transport endpoint already has an address   associated with it."),
	ERROR_ADDRESS_NOT_ASSOCIATED(0X000004CC, "An address has not yet been associated with the   network endpoint."),
	ERROR_CONNECTION_INVALID(0X000004CD, "An operation was attempted on a nonexistent network   connection."),
	ERROR_CONNECTION_ACTIVE(0X000004CE, "An invalid operation was attempted on an active   network connection."),
	ERROR_NETWORK_UNREACHABLE(0X000004CF, "The network location cannot be reached. For   information about network troubleshooting, see Windows Help."),
	ERROR_HOST_UNREACHABLE(0X000004D0, "The network location cannot be reached. For   information about network troubleshooting, see Windows Help."),
	ERROR_PROTOCOL_UNREACHABLE(0X000004D1, "The network location cannot be reached. For   information about network troubleshooting, see Windows Help."),
	ERROR_PORT_UNREACHABLE(0X000004D2, "No service is operating at the destination network   endpoint on the remote system."),
	ERROR_REQUEST_ABORTED(0X000004D3, "The request was aborted."),
	ERROR_CONNECTION_ABORTED(0X000004D4, "The network connection was aborted by the local   system."),
	ERROR_RETRY(0X000004D5, "The operation could not be completed. A retry should   be performed."),
	ERROR_CONNECTION_COUNT_LIMIT(0X000004D6, "A connection to the server could not be made because   the limit on the number of concurrent connections for this account has been   reached."),
	ERROR_LOGIN_TIME_RESTRICTION(0X000004D7, "Attempting to log on during an unauthorized time of   day for this account."),
	ERROR_LOGIN_WKSTA_RESTRICTION(0X000004D8, "The account is not authorized to log on from this   station."),
	ERROR_INCORRECT_ADDRESS(0X000004D9, "The network address could not be used for the   operation requested."),
	ERROR_ALREADY_REGISTERED(0X000004DA, "The service is already registered."),
	ERROR_SERVICE_NOT_FOUND(0X000004DB, "The specified service does not exist."),
	ERROR_NOT_AUTHENTICATED(0X000004DC, "The operation being requested was not performed   because the user has not been authenticated."),
	ERROR_NOT_LOGGED_ON(0X000004DD, "The operation being requested was not performed   because the user has not logged on to the network. The specified service does   not exist."),
	ERROR_CONTINUE(0X000004DE, "Continue with work in progress."),
	ERROR_ALREADY_INITIALIZED(0X000004DF, "An attempt was made to perform an initialization   operation when initialization has already been completed."),
	ERROR_NO_MORE_DEVICES(0X000004E0, "No more local devices."),
	ERROR_NO_SUCH_SITE(0X000004E1, "The specified site does not exist."),
	ERROR_DOMAIN_CONTROLLER_EXISTS(0X000004E2, "A domain controller with the specified name already   exists."),
	ERROR_ONLY_IF_CONNECTED(0X000004E3, "This operation is supported only when you are   connected to the server."),
	ERROR_OVERRIDE_NOCHANGES(0X000004E4, "The group policy framework should call the extension   even if there are no changes."),
	ERROR_BAD_USER_PROFILE(0X000004E5, "The specified user does not have a valid profile."),
	ERROR_NOT_SUPPORTED_ON_SBS(0X000004E6, "This operation is not supported on a computer running   Windows Server 2003 operating system for Small Business Server."),
	ERROR_SERVER_SHUTDOWN_IN_PROGRESS(0X000004E7, "The server machine is shutting down."),
	ERROR_HOST_DOWN(0X000004E8, "The remote system is not available. For information   about network troubleshooting, see Windows Help."),
	ERROR_NON_ACCOUNT_SID(0X000004E9, "The security identifier provided is not from an   account domain."),
	ERROR_NON_DOMAIN_SID(0X000004EA, "The security identifier provided does not have a domain   component."),
	ERROR_APPHELP_BLOCK(0X000004EB, "AppHelp dialog canceled, thus preventing the   application from starting."),
	ERROR_ACCESS_DISABLED_BY_POLICY(0X000004EC, "This program is blocked by Group Policy. For more   information, contact your system administrator."),
	ERROR_REG_NAT_CONSUMPTION(0X000004ED, "A program attempt to use an invalid register value.   Normally caused by an uninitialized register. This error is Itanium specific."),
	ERROR_CSCSHARE_OFFLINE(0X000004EE, "The share is currently offline or does not exist."),
	ERROR_PKINIT_FAILURE(0X000004EF, "The Kerberos protocol encountered an error while   validating the KDC certificate during smartcard logon. There is more   information in the system event log."),
	ERROR_SMARTCARD_SUBSYSTEM_FAILURE(0X000004F0, "The Kerberos protocol encountered an error while   attempting to utilize the smartcard subsystem."),
	ERROR_DOWNGRADE_DETECTED(0X000004F1, "The system detected a possible attempt to compromise   security. Ensure that you can contact the server that authenticated you."),
	ERROR_MACHINE_LOCKED(0X000004F7, "The machine is locked and cannot be shut down without   the force option."),
	ERROR_CALLBACK_SUPPLIED_INVALID_DATA(0X000004F9, "An application-defined callback gave invalid data when   called."),
	ERROR_SYNC_FOREGROUND_REFRESH_REQUIRED(0X000004FA, "The Group Policy framework should call the extension   in the synchronous foreground policy refresh."),
	ERROR_DRIVER_BLOCKED(0X000004FB, "This driver has been blocked from loading."),
	ERROR_INVALID_IMPORT_OF_NON_DLL(0X000004FC, "A DLL referenced a module that was neither a DLL nor   the process's executable image."),
	ERROR_ACCESS_DISABLED_WEBBLADE(0X000004FD, "Windows cannot open this program because it has been   disabled."),
	ERROR_ACCESS_DISABLED_WEBBLADE_TAMPER(0X000004FE, "Windows cannot open this program because the license   enforcement system has been tampered with or become corrupted."),
	ERROR_RECOVERY_FAILURE(0X000004FF, "A transaction recover failed."),
	ERROR_ALREADY_FIBER(0X00000500, "The current thread has already been converted to a   fiber."),
	ERROR_ALREADY_THREAD(0X00000501, "The current thread has already been converted from a   fiber."),
	ERROR_STACK_BUFFER_OVERRUN(0X00000502, "The system detected an overrun of a stack-based buffer   in this application. This overrun could potentially allow a malicious user to   gain control of this application."),
	ERROR_PARAMETER_QUOTA_EXCEEDED(0X00000503, "Data present in one of the parameters is more than the   function can operate on."),
	ERROR_DEBUGGER_INACTIVE(0X00000504, "An attempt to perform an operation on a debug object   failed because the object is in the process of being deleted."),
	ERROR_DELAY_LOAD_FAILED(0X00000505, "An attempt to delay-load a .dll or get a function   address in a delay-loaded .dll failed."),
	ERROR_VDM_DISALLOWED(0X00000506, "%1 is a 16-bit application. You do not have   permissions to execute 16-bit applications. Check your permissions with your   system administrator."),
	ERROR_UNIDENTIFIED_ERROR(0X00000507, "Insufficient information exists to identify the cause   of failure."),
	ERROR_INVALID_CRUNTIME_PARAMETER(0X00000508, "None"),
	ERROR_BEYOND_VDL(0X00000509, "The operation occurred beyond the valid data length of   the file."),
	ERROR_INCOMPATIBLE_SERVICE_SID_TYPE(0X0000050A, "The service start failed because one or more services   in the same process have an incompatible service SID type setting. A service   with a restricted service SID type can only coexist in the same process with   other services with a restricted SID type."),
	ERROR_DRIVER_PROCESS_TERMINATED(0X0000050B, "The process hosting the driver for this device has   been terminated."),
	ERROR_IMPLEMENTATION_LIMIT(0X0000050C, "An operation attempted to exceed an   implementation-defined limit."),
	ERROR_PROCESS_IS_PROTECTED(0X0000050D, "Either the target process, or the target thread's   containing process, is a protected process."),
	ERROR_SERVICE_NOTIFY_CLIENT_LAGGING(0X0000050E, "The service notification client is lagging too far   behind the current state of services in the machine."),
	ERROR_DISK_QUOTA_EXCEEDED(0X0000050F, "An operation failed because the storage quota was exceeded."),
	ERROR_CONTENT_BLOCKED(0X00000510, "An operation failed because the content was blocked."),
	ERROR_INCOMPATIBLE_SERVICE_PRIVILEGE(0X00000511, "A privilege that the service requires to function   properly does not exist in the service account configuration. The Services   Microsoft Management Console (MMC) snap-in (Services.msc) and the Local   Security Settings MMC snap-in (Secpol.msc) can be used to view the service   configuration and the account configuration."),
	ERROR_INVALID_LABEL(0X00000513, "Indicates a particular SID cannot be assigned as the   label of an object."),
	ERROR_NOT_ALL_ASSIGNED(0X00000514, "Not all privileges or groups referenced are assigned   to the caller."),
	ERROR_SOME_NOT_MAPPED(0X00000515, "Some mapping between account names and SIDs was not   done."),
	ERROR_NO_QUOTAS_FOR_ACCOUNT(0X00000516, "No system quota limits are specifically set for this   account."),
	ERROR_LOCAL_USER_SESSION_KEY(0X00000517, "No encryption key is available. A well-known   encryption key was returned."),
	ERROR_NULL_LM_PASSWORD(0X00000518, "The password is too complex to be converted to a LAN   Manager password. The LAN Manager password returned is a null string."),
	ERROR_UNKNOWN_REVISION(0X00000519, "The revision level is unknown."),
	ERROR_REVISION_MISMATCH(0X0000051A, "Indicates two revision levels are incompatible."),
	ERROR_INVALID_OWNER(0X0000051B, "This SID cannot be assigned as the owner of this   object."),
	ERROR_INVALID_PRIMARY_GROUP(0X0000051C, "This SID cannot be assigned as the primary group of an   object."),
	ERROR_NO_IMPERSONATION_TOKEN(0X0000051D, "An attempt has been made to operate on an   impersonation token by a thread that is not currently impersonating a client."),
	ERROR_CANT_DISABLE_MANDATORY(0X0000051E, "The group cannot be disabled."),
	ERROR_NO_LOGON_SERVERS(0X0000051F, "There are currently no logon servers available to   service the logon request."),
	ERROR_NO_SUCH_LOGON_SESSION(0X00000520, "A specified logon session does not exist. It might   already have been terminated."),
	ERROR_NO_SUCH_PRIVILEGE(0X00000521, "A specified privilege does not exist."),
	ERROR_PRIVILEGE_NOT_HELD(0X00000522, "A required privilege is not held by the client."),
	ERROR_INVALID_ACCOUNT_NAME(0X00000523, "The name provided is not a properly formed account   name."),
	ERROR_USER_EXISTS(0X00000524, "The specified account already exists."),
	ERROR_NO_SUCH_USER(0X00000525, "The specified account does not exist."),
	ERROR_GROUP_EXISTS(0X00000526, "The specified group already exists."),
	ERROR_NO_SUCH_GROUP(0X00000527, "The specified group does not exist."),
	ERROR_MEMBER_IN_GROUP(0X00000528, "Either the specified user account is already a member   of the specified group, or the specified group cannot be deleted because it   contains a member."),
	ERROR_MEMBER_NOT_IN_GROUP(0X00000529, "The specified user account is not a member of the   specified group account."),
	ERROR_LAST_ADMIN(0X0000052A, "The last remaining administration account cannot be   disabled or deleted."),
	ERROR_WRONG_PASSWORD(0X0000052B, "Unable to update the password. The value provided as   the current password is incorrect."),
	ERROR_ILL_FORMED_PASSWORD(0X0000052C, "Unable to update the password. The value provided for   the new password contains values that are not allowed in passwords."),
	ERROR_PASSWORD_RESTRICTION(0X0000052D, "Unable to update the password. The value provided for   the new password does not meet the length, complexity, or history requirements   of the domain."),
	ERROR_LOGON_FAILURE(0X0000052E, "Logon failure: Unknown user name or bad password."),
	ERROR_ACCOUNT_RESTRICTION(0X0000052F, "Logon failure: User account restriction. Possible   reasons are blank passwords not allowed, logon hour restrictions, or a policy   restriction has been enforced."),
	ERROR_INVALID_LOGON_HOURS(0X00000530, "Logon failure: Account logon time restriction   violation."),
	ERROR_INVALID_WORKSTATION(0X00000531, "Logon failure: User not allowed to log on to this   computer."),
	ERROR_PASSWORD_EXPIRED(0X00000532, "Logon failure: The specified account password has   expired."),
	ERROR_ACCOUNT_DISABLED(0X00000533, "Logon failure: Account currently disabled."),
	ERROR_NONE_MAPPED(0X00000534, "No mapping between account names and SIDs was done."),
	ERROR_TOO_MANY_LUIDS_REQUESTED(0X00000535, "Too many local user identifiers (LUIDs) were requested   at one time."),
	ERROR_LUIDS_EXHAUSTED(0X00000536, "No more LUIDs are available."),
	ERROR_INVALID_SUB_AUTHORITY(0X00000537, "The sub-authority part of an SID is invalid for this   particular use."),
	ERROR_INVALID_ACL(0X00000538, "The ACL structure is invalid."),
	ERROR_INVALID_SID(0X00000539, "The SID structure is invalid."),
	ERROR_INVALID_SECURITY_DESCR(0X0000053A, "The security descriptor structure is invalid."),
	ERROR_BAD_INHERITANCE_ACL(0X0000053C, "The inherited ACL or ACE could not be built."),
	ERROR_SERVER_DISABLED(0X0000053D, "The server is currently disabled."),
	ERROR_SERVER_NOT_DISABLED(0X0000053E, "The server is currently enabled."),
	ERROR_INVALID_ID_AUTHORITY(0X0000053F, "The value provided was an invalid value for an   identifier authority."),
	ERROR_ALLOTTED_SPACE_EXCEEDED(0X00000540, "No more memory is available for security information   updates."),
	ERROR_INVALID_GROUP_ATTRIBUTES(0X00000541, "The specified attributes are invalid, or incompatible   with the attributes for the group as a whole."),
	ERROR_BAD_IMPERSONATION_LEVEL(0X00000542, "Either a required impersonation level was not   provided, or the provided impersonation level is invalid."),
	ERROR_CANT_OPEN_ANONYMOUS(0X00000543, "Cannot open an anonymous level security token."),
	ERROR_BAD_VALIDATION_CLASS(0X00000544, "The validation information class requested was   invalid."),
	ERROR_BAD_TOKEN_TYPE(0X00000545, "The type of the token is inappropriate for its   attempted use."),
	ERROR_NO_SECURITY_ON_OBJECT(0X00000546, "Unable to perform a security operation on an object   that has no associated security."),
	ERROR_CANT_ACCESS_DOMAIN_INFO(0X00000547, "Configuration information could not be read from the   domain controller, either because the machine is unavailable, or access has   been denied."),
	ERROR_INVALID_SERVER_STATE(0X00000548, "The SAM or local security authority (LSA) server was   in the wrong state to perform the security operation."),
	ERROR_INVALID_DOMAIN_STATE(0X00000549, "The domain was in the wrong state to perform the   security operation."),
	ERROR_INVALID_DOMAIN_ROLE(0X0000054A, "This operation is only allowed for the PDC of the   domain."),
	ERROR_NO_SUCH_DOMAIN(0X0000054B, "The specified domain either does not exist or could   not be contacted."),
	ERROR_DOMAIN_EXISTS(0X0000054C, "The specified domain already exists."),
	ERROR_DOMAIN_LIMIT_EXCEEDED(0X0000054D, "An attempt was made to exceed the limit on the number   of domains per server."),
	ERROR_INTERNAL_DB_CORRUPTION(0X0000054E, "Unable to complete the requested operation because of   either a catastrophic media failure or a data structure corruption on the   disk."),
	ERROR_INTERNAL_ERROR(0X0000054F, "An internal error occurred."),
	ERROR_GENERIC_NOT_MAPPED(0X00000550, "Generic access types were contained in an access mask   that should already be mapped to nongeneric types."),
	ERROR_BAD_DESCRIPTOR_FORMAT(0X00000551, "A security descriptor is not in the right format   (absolute or self-relative)."),
	ERROR_NOT_LOGON_PROCESS(0X00000552, "The requested action is restricted for use by logon   processes only. The calling process has not registered as a logon process."),
	ERROR_LOGON_SESSION_EXISTS(0X00000553, "Cannot start a new logon session with an ID that is   already in use."),
	ERROR_NO_SUCH_PACKAGE(0X00000554, "A specified authentication package is unknown."),
	ERROR_BAD_LOGON_SESSION_STATE(0X00000555, "The logon session is not in a state that is consistent   with the requested operation."),
	ERROR_LOGON_SESSION_COLLISION(0X00000556, "The logon session ID is already in use."),
	ERROR_INVALID_LOGON_TYPE(0X00000557, "A logon request contained an invalid logon type value."),
	ERROR_CANNOT_IMPERSONATE(0X00000558, "Unable to impersonate using a named pipe until data   has been read from that pipe."),
	ERROR_RXACT_INVALID_STATE(0X00000559, "The transaction state of a registry subtree is incompatible   with the requested operation."),
	ERROR_RXACT_COMMIT_FAILURE(0X0000055A, "An internal security database corruption has been   encountered."),
	ERROR_SPECIAL_ACCOUNT(0X0000055B, "Cannot perform this operation on built-in accounts."),
	ERROR_SPECIAL_GROUP(0X0000055C, "Cannot perform this operation on this built-in special   group."),
	ERROR_SPECIAL_USER(0X0000055D, "Cannot perform this operation on this built-in special   user."),
	ERROR_MEMBERS_PRIMARY_GROUP(0X0000055E, "The user cannot be removed from a group because the group   is currently the user's primary group."),
	ERROR_TOKEN_ALREADY_IN_USE(0X0000055F, "The token is already in use as a primary token."),
	ERROR_NO_SUCH_ALIAS(0X00000560, "The specified local group does not exist."),
	ERROR_MEMBER_NOT_IN_ALIAS(0X00000561, "The specified account name is not a member of the   group."),
	ERROR_MEMBER_IN_ALIAS(0X00000562, "The specified account name is already a member of the   group."),
	ERROR_ALIAS_EXISTS(0X00000563, "The specified local group already exists."),
	ERROR_LOGON_NOT_GRANTED(0X00000564, "Logon failure: The user has not been granted the   requested logon type at this computer."),
	ERROR_TOO_MANY_SECRETS(0X00000565, "The maximum number of secrets that can be stored in a   single system has been exceeded."),
	ERROR_SECRET_TOO_LONG(0X00000566, "The length of a secret exceeds the maximum length   allowed."),
	ERROR_INTERNAL_DB_ERROR(0X00000567, "The local security authority database contains an   internal inconsistency."),
	ERROR_TOO_MANY_CONTEXT_IDS(0X00000568, "During a logon attempt, the user's security context   accumulated too many SIDs."),
	ERROR_LOGON_TYPE_NOT_GRANTED(0X00000569, "Logon failure: The user has not been granted the   requested logon type at this computer."),
	ERROR_NT_CROSS_ENCRYPTION_REQUIRED(0X0000056A, "A cross-encrypted password is necessary to change a   user password."),
	ERROR_NO_SUCH_MEMBER(0X0000056B, "A member could not be added to or removed from the   local group because the member does not exist."),
	ERROR_INVALID_MEMBER(0X0000056C, "A new member could not be added to a local group   because the member has the wrong account type."),
	ERROR_TOO_MANY_SIDS(0X0000056D, "Too many SIDs have been specified."),
	ERROR_LM_CROSS_ENCRYPTION_REQUIRED(0X0000056E, "A cross-encrypted password is necessary to change this   user password."),
	ERROR_NO_INHERITANCE(0X0000056F, "Indicates an ACL contains no inheritable components."),
	ERROR_FILE_CORRUPT(0X00000570, "The file or directory is corrupted and unreadable."),
	ERROR_DISK_CORRUPT(0X00000571, "The disk structure is corrupted and unreadable."),
	ERROR_NO_USER_SESSION_KEY(0X00000572, "There is no user session key for the specified logon   session."),
	ERROR_LICENSE_QUOTA_EXCEEDED(0X00000573, "The service being accessed is licensed for a   particular number of connections. No more connections can be made to the   service at this time because the service has accepted the maximum number of   connections."),
	ERROR_WRONG_TARGET_NAME(0X00000574, "Logon failure: The target account name is incorrect."),
	ERROR_MUTUAL_AUTH_FAILED(0X00000575, "Mutual authentication failed. The server's password is   out of date at the domain controller."),
	ERROR_TIME_SKEW(0X00000576, "There is a time and/or date difference between the   client and server."),
	ERROR_CURRENT_DOMAIN_NOT_ALLOWED(0X00000577, "This operation cannot be performed on the current   domain."),
	ERROR_INVALID_WINDOW_HANDLE(0X00000578, "Invalid window handle."),
	ERROR_INVALID_MENU_HANDLE(0X00000579, "Invalid menu handle."),
	ERROR_INVALID_CURSOR_HANDLE(0X0000057A, "Invalid cursor handle."),
	ERROR_INVALID_ACCEL_HANDLE(0X0000057B, "Invalid accelerator table handle."),
	ERROR_INVALID_HOOK_HANDLE(0X0000057C, "Invalid hook handle."),
	ERROR_INVALID_DWP_HANDLE(0X0000057D, "Invalid handle to a multiple-window position   structure."),
	ERROR_TLW_WITH_WSCHILD(0X0000057E, "Cannot create a top-level child window."),
	ERROR_CANNOT_FIND_WND_CLASS(0X0000057F, "Cannot find window class."),
	ERROR_WINDOW_OF_OTHER_THREAD(0X00000580, "Invalid window; it belongs to other thread."),
	ERROR_HOTKEY_ALREADY_REGISTERED(0X00000581, "Hot key is already registered."),
	ERROR_CLASS_ALREADY_EXISTS(0X00000582, "Class already exists."),
	ERROR_CLASS_DOES_NOT_EXIST(0X00000583, "Class does not exist."),
	ERROR_CLASS_HAS_WINDOWS(0X00000584, "Class still has open windows."),
	ERROR_INVALID_INDEX(0X00000585, "Invalid index."),
	ERROR_INVALID_ICON_HANDLE(0X00000586, "Invalid icon handle."),
	ERROR_PRIVATE_DIALOG_INDEX(0X00000587, "Using private DIALOG window words."),
	ERROR_LISTBOX_ID_NOT_FOUND(0X00000588, "The list box identifier was not found."),
	ERROR_NO_WILDCARD_CHARACTERS(0X00000589, "No wildcards were found."),
	ERROR_CLIPBOARD_NOT_OPEN(0X0000058A, "Thread does not have a clipboard open."),
	ERROR_HOTKEY_NOT_REGISTERED(0X0000058B, "Hot key is not registered."),
	ERROR_WINDOW_NOT_DIALOG(0X0000058C, "The window is not a valid dialog window."),
	ERROR_CONTROL_ID_NOT_FOUND(0X0000058D, "Control ID not found."),
	ERROR_INVALID_COMBOBOX_MESSAGE(0X0000058E, "Invalid message for a combo box because it does not   have an edit control."),
	ERROR_WINDOW_NOT_COMBOBOX(0X0000058F, "The window is not a combo box."),
	ERROR_INVALID_EDIT_HEIGHT(0X00000590, "Height must be less than 256."),
	ERROR_DC_NOT_FOUND(0X00000591, "Invalid device context (DC) handle."),
	ERROR_INVALID_HOOK_FILTER(0X00000592, "Invalid hook procedure type."),
	ERROR_INVALID_FILTER_PROC(0X00000593, "Invalid hook procedure."),
	ERROR_HOOK_NEEDS_HMOD(0X00000594, "Cannot set nonlocal hook without a module handle."),
	ERROR_GLOBAL_ONLY_HOOK(0X00000595, "This hook procedure can only be set globally."),
	ERROR_JOURNAL_HOOK_SET(0X00000596, "The journal hook procedure is already installed."),
	ERROR_HOOK_NOT_INSTALLED(0X00000597, "The hook procedure is not installed."),
	ERROR_INVALID_LB_MESSAGE(0X00000598, "Invalid message for single-selection list box."),
	ERROR_SETCOUNT_ON_BAD_LB(0X00000599, "LB_SETCOUNT sent to non-lazy list box."),
	ERROR_LB_WITHOUT_TABSTOPS(0X0000059A, "This list box does not support tab stops."),
	ERROR_DESTROY_OBJECT_OF_OTHER_THREAD(0X0000059B, "Cannot destroy object created by another thread."),
	ERROR_CHILD_WINDOW_MENU(0X0000059C, "Child windows cannot have menus."),
	ERROR_NO_SYSTEM_MENU(0X0000059D, "The window does not have a system menu."),
	ERROR_INVALID_MSGBOX_STYLE(0X0000059E, "Invalid message box style."),
	ERROR_INVALID_SPI_VALUE(0X0000059F, "Invalid system-wide (SPI_*) parameter."),
	ERROR_SCREEN_ALREADY_LOCKED(0X000005A0, "Screen already locked."),
	ERROR_HWNDS_HAVE_DIFF_PARENT(0X000005A1, "All handles to windows in a multiple-window position   structure must have the same parent."),
	ERROR_NOT_CHILD_WINDOW(0X000005A2, "The window is not a child window."),
	ERROR_INVALID_GW_COMMAND(0X000005A3, "Invalid GW_* command."),
	ERROR_INVALID_THREAD_ID(0X000005A4, "Invalid thread identifier."),
	ERROR_NON_MDICHILD_WINDOW(0X000005A5, "Cannot process a message from a window that is not a   multiple document interface (MDI) window."),
	ERROR_POPUP_ALREADY_ACTIVE(0X000005A6, "Pop-up menu already active."),
	ERROR_NO_SCROLLBARS(0X000005A7, "The window does not have scroll bars."),
	ERROR_INVALID_SCROLLBAR_RANGE(0X000005A8, "Scroll bar range cannot be greater than MAXLONG."),
	ERROR_INVALID_SHOWWIN_COMMAND(0X000005A9, "Cannot show or remove the window in the way specified."),
	ERROR_NO_SYSTEM_RESOURCES(0X000005AA, "Insufficient system resources exist to complete the   requested service."),
	ERROR_NONPAGED_SYSTEM_RESOURCES(0X000005AB, "Insufficient system resources exist to complete the   requested service."),
	ERROR_PAGED_SYSTEM_RESOURCES(0X000005AC, "Insufficient system resources exist to complete the   requested service."),
	ERROR_WORKING_SET_QUOTA(0X000005AD, "Insufficient quota to complete the requested service."),
	ERROR_PAGEFILE_QUOTA(0X000005AE, "Insufficient quota to complete the requested service."),
	ERROR_COMMITMENT_LIMIT(0X000005AF, "The paging file is too small for this operation to   complete."),
	ERROR_MENU_ITEM_NOT_FOUND(0X000005B0, "A menu item was not found."),
	ERROR_INVALID_KEYBOARD_HANDLE(0X000005B1, "Invalid keyboard layout handle."),
	ERROR_HOOK_TYPE_NOT_ALLOWED(0X000005B2, "Hook type not allowed."),
	ERROR_REQUIRES_INTERACTIVE_WINDOWSTATION(0X000005B3, "This operation requires an interactive window station."),
	ERROR_TIMEOUT(0X000005B4, "This operation returned because the time-out period   expired."),
	ERROR_INVALID_MONITOR_HANDLE(0X000005B5, "Invalid monitor handle."),
	ERROR_INCORRECT_SIZE(0X000005B6, "Incorrect size argument."),
	ERROR_SYMLINK_CLASS_DISABLED(0X000005B7, "The symbolic link cannot be followed because its type   is disabled."),
	ERROR_SYMLINK_NOT_SUPPORTED(0X000005B8, "This application does not support the current   operation on symbolic links."),
	ERROR_EVENTLOG_FILE_CORRUPT(0X000005DC, "The event log file is corrupted."),
	ERROR_EVENTLOG_CANT_START(0X000005DD, "No event log file could be opened, so the event   logging service did not start."),
	ERROR_LOG_FILE_FULL(0X000005DE, "The event log file is full."),
	ERROR_EVENTLOG_FILE_CHANGED(0X000005DF, "The event log file has changed between read   operations."),
	ERROR_INVALID_TASK_NAME(0X0000060E, "The specified task name is invalid."),
	ERROR_INVALID_TASK_INDEX(0X0000060F, "The specified task index is invalid."),
	ERROR_THREAD_ALREADY_IN_TASK(0X00000610, "The specified thread is already joining a task."),
	ERROR_INSTALL_SERVICE_FAILURE(0X00000641, "The Windows Installer service could not be accessed.   This can occur if the Windows Installer is not correctly installed. Contact   your support personnel for assistance."),
	ERROR_INSTALL_USEREXIT(0X00000642, "User canceled installation."),
	ERROR_INSTALL_FAILURE(0X00000643, "Fatal error during installation."),
	ERROR_INSTALL_SUSPEND(0X00000644, "Installation suspended, incomplete."),
	ERROR_UNKNOWN_PRODUCT(0X00000645, "This action is valid only for products that are   currently installed."),
	ERROR_UNKNOWN_FEATURE(0X00000646, "Feature ID not registered."),
	ERROR_UNKNOWN_COMPONENT(0X00000647, "Component ID not registered."),
	ERROR_UNKNOWN_PROPERTY(0X00000648, "Unknown property."),
	ERROR_INVALID_HANDLE_STATE(0X00000649, "Handle is in an invalid state."),
	ERROR_BAD_CONFIGURATION(0X0000064A, "The configuration data for this product is corrupt.   Contact your support personnel."),
	ERROR_INDEX_ABSENT(0X0000064B, "Component qualifier not present."),
	ERROR_INSTALL_SOURCE_ABSENT(0X0000064C, "The installation source for this product is not   available. Verify that the source exists and that you can access it."),
	ERROR_INSTALL_PACKAGE_VERSION(0X0000064D, "This installation package cannot be installed by the   Windows Installer service. You must install a Windows service pack that contains   a newer version of the Windows Installer service."),
	ERROR_PRODUCT_UNINSTALLED(0X0000064E, "Product is uninstalled."),
	ERROR_BAD_QUERY_SYNTAX(0X0000064F, "SQL query syntax invalid or unsupported."),
	ERROR_INVALID_FIELD(0X00000650, "Record field does not exist."),
	ERROR_DEVICE_REMOVED(0X00000651, "The device has been removed."),
	ERROR_INSTALL_ALREADY_RUNNING(0X00000652, "Another installation is already in progress. Complete   that installation before proceeding with this install."),
	ERROR_INSTALL_PACKAGE_OPEN_FAILED(0X00000653, "This installation package could not be opened. Verify   that the package exists and that you can access it, or contact the   application vendor to verify that this is a valid Windows Installer package."),
	ERROR_INSTALL_PACKAGE_INVALID(0X00000654, "This installation package could not be opened. Contact   the application vendor to verify that this is a valid Windows Installer   package."),
	ERROR_INSTALL_UI_FAILURE(0X00000655, "There was an error starting the Windows Installer   service user interface. Contact your support personnel."),
	ERROR_INSTALL_LOG_FAILURE(0X00000656, "Error opening installation log file. Verify that the   specified log file location exists and that you can write to it."),
	ERROR_INSTALL_LANGUAGE_UNSUPPORTED(0X00000657, "The language of this installation package is not   supported by your system."),
	ERROR_INSTALL_TRANSFORM_FAILURE(0X00000658, "Error applying transforms. Verify that the specified   transform paths are valid."),
	ERROR_INSTALL_PACKAGE_REJECTED(0X00000659, "This installation is forbidden by system policy.   Contact your system administrator."),
	ERROR_FUNCTION_NOT_CALLED(0X0000065A, "Function could not be executed."),
	ERROR_FUNCTION_FAILED(0X0000065B, "Function failed during execution."),
	ERROR_INVALID_TABLE(0X0000065C, "Invalid or unknown table specified."),
	ERROR_DATATYPE_MISMATCH(0X0000065D, "Data supplied is of wrong type."),
	ERROR_UNSUPPORTED_TYPE(0X0000065E, "Data of this type is not supported."),
	ERROR_CREATE_FAILED(0X0000065F, "The Windows Installer service failed to start. Contact   your support personnel."),
	ERROR_INSTALL_TEMP_UNWRITABLE(0X00000660, "The Temp folder is on a drive that is full or is   inaccessible. Free up space on the drive or verify that you have write   permission on the Temp folder."),
	ERROR_INSTALL_PLATFORM_UNSUPPORTED(0X00000661, "This installation package is not supported by this   processor type. Contact your product vendor."),
	ERROR_INSTALL_NOTUSED(0X00000662, "Component not used on this computer."),
	ERROR_PATCH_PACKAGE_OPEN_FAILED(0X00000663, "This update package could not be opened. Verify that   the update package exists and that you can access it, or contact the   application vendor to verify that this is a valid Windows Installer update   package."),
	ERROR_PATCH_PACKAGE_INVALID(0X00000664, "This update package could not be opened. Contact the   application vendor to verify that this is a valid Windows Installer update   package."),
	ERROR_PATCH_PACKAGE_UNSUPPORTED(0X00000665, "This update package cannot be processed by the Windows   Installer service. You must install a Windows service pack that contains a   newer version of the Windows Installer service."),
	ERROR_PRODUCT_VERSION(0X00000666, "Another version of this product is already installed.   Installation of this version cannot continue. To configure or remove the existing   version of this product, use Add/Remove Programs in Control Panel."),
	ERROR_INVALID_COMMAND_LINE(0X00000667, "Invalid command-line argument. Consult the Windows   Installer SDK for detailed command line help."),
	ERROR_INSTALL_REMOTE_DISALLOWED(0X00000668, "None"),
	ERROR_SUCCESS_REBOOT_INITIATED(0X00000669, "The requested operation completed successfully. The   system will be restarted so the changes can take effect."),
	ERROR_PATCH_TARGET_NOT_FOUND(0X0000066A, "The upgrade cannot be installed by the Windows   Installer service because the program to be upgraded might be missing, or the   upgrade might update a different version of the program. Verify that the   program to be upgraded exists on your computer and that you have the correct   upgrade."),
	ERROR_PATCH_PACKAGE_REJECTED(0X0000066B, "The update package is not permitted by a software   restriction policy."),
	ERROR_INSTALL_TRANSFORM_REJECTED(0X0000066C, "One or more customizations are not permitted by a   software restriction policy."),
	ERROR_INSTALL_REMOTE_PROHIBITED(0X0000066D, "The Windows Installer does not permit installation   from a Remote Desktop Connection."),
	ERROR_PATCH_REMOVAL_UNSUPPORTED(0X0000066E, "Uninstallation of the update package is not supported."),
	ERROR_UNKNOWN_PATCH(0X0000066F, "The update is not applied to this product."),
	ERROR_PATCH_NO_SEQUENCE(0X00000670, "No valid sequence could be found for the set of   updates."),
	ERROR_PATCH_REMOVAL_DISALLOWED(0X00000671, "Update removal was disallowed by policy."),
	ERROR_INVALID_PATCH_XML(0X00000672, "The XML update data is invalid."),
	ERROR_PATCH_MANAGED_ADVERTISED_PRODUCT(0X00000673, "Windows Installer does not permit updating of managed   advertised products. At least one feature of the product must be installed   before applying the update."),
	ERROR_INSTALL_SERVICE_SAFEBOOT(0X00000674, "The Windows Installer service is not accessible in   Safe Mode. Try again when your computer is not in Safe Mode or you can use   System Restore to return your machine to a previous good state."),
	RPC_S_INVALID_STRING_BINDING(0X000006A4, "The string binding is invalid."),
	RPC_S_WRONG_KIND_OF_BINDING(0X000006A5, "The binding handle is not the correct type."),
	RPC_S_INVALID_BINDING(0X000006A6, "The binding handle is invalid."),
	RPC_S_PROTSEQ_NOT_SUPPORTED(0X000006A7, "The RPC protocol sequence is not supported."),
	RPC_S_INVALID_RPC_PROTSEQ(0X000006A8, "The RPC protocol sequence is invalid."),
	RPC_S_INVALID_STRING_UUID(0X000006A9, "None"),
	RPC_S_INVALID_ENDPOINT_FORMAT(0X000006AA, "The endpoint format is invalid."),
	RPC_S_INVALID_NET_ADDR(0X000006AB, "The network address is invalid."),
	RPC_S_NO_ENDPOINT_FOUND(0X000006AC, "No endpoint was found."),
	RPC_S_INVALID_TIMEOUT(0X000006AD, "The time-out value is invalid."),
	RPC_S_OBJECT_NOT_FOUND(0X000006AE, "The object UUID) was not found."),
	RPC_S_ALREADY_REGISTERED(0X000006AF, "The object UUID) has already been registered."),
	RPC_S_TYPE_ALREADY_REGISTERED(0X000006B0, "The type UUID has already been registered."),
	RPC_S_ALREADY_LISTENING(0X000006B1, "The RPC server is already listening."),
	RPC_S_NO_PROTSEQS_REGISTERED(0X000006B2, "No protocol sequences have been registered."),
	RPC_S_NOT_LISTENING(0X000006B3, "The RPC server is not listening."),
	RPC_S_UNKNOWN_MGR_TYPE(0X000006B4, "The manager type is unknown."),
	RPC_S_UNKNOWN_IF(0X000006B5, "The interface is unknown."),
	RPC_S_NO_BINDINGS(0X000006B6, "There are no bindings."),
	RPC_S_NO_PROTSEQS(0X000006B7, "There are no protocol sequences."),
	RPC_S_CANT_CREATE_ENDPOINT(0X000006B8, "The endpoint cannot be created."),
	RPC_S_OUT_OF_RESOURCES(0X000006B9, "Not enough resources are available to complete this   operation."),
	RPC_S_SERVER_UNAVAILABLE(0X000006BA, "The RPC server is unavailable."),
	RPC_S_SERVER_TOO_BUSY(0X000006BB, "The RPC server is too busy to complete this operation."),
	RPC_S_INVALID_NETWORK_OPTIONS(0X000006BC, "The network options are invalid."),
	RPC_S_NO_CALL_ACTIVE(0X000006BD, "There are no RPCs active on this thread."),
	RPC_S_CALL_FAILED(0X000006BE, "The RPC failed."),
	RPC_S_CALL_FAILED_DNE(0X000006BF, "The RPC failed and did not execute."),
	RPC_S_PROTOCOL_ERROR(0X000006C0, "An RPC protocol error occurred."),
	RPC_S_PROXY_ACCESS_DENIED(0X000006C1, "Access to the HTTP proxy is denied."),
	RPC_S_UNSUPPORTED_TRANS_SYN(0X000006C2, "The transfer syntax is not supported by the RPC   server."),
	RPC_S_UNSUPPORTED_TYPE(0X000006C4, "The UUID type is not supported."),
	RPC_S_INVALID_TAG(0X000006C5, "The tag is invalid."),
	RPC_S_INVALID_BOUND(0X000006C6, "The array bounds are invalid."),
	RPC_S_NO_ENTRY_NAME(0X000006C7, "The binding does not contain an entry name."),
	RPC_S_INVALID_NAME_SYNTAX(0X000006C8, "The name syntax is invalid."),
	RPC_S_UNSUPPORTED_NAME_SYNTAX(0X000006C9, "The name syntax is not supported."),
	RPC_S_UUID_NO_ADDRESS(0X000006CB, "No network address is available to use to construct a   UUID."),
	RPC_S_DUPLICATE_ENDPOINT(0X000006CC, "The endpoint is a duplicate."),
	RPC_S_UNKNOWN_AUTHN_TYPE(0X000006CD, "The authentication type is unknown."),
	RPC_S_MAX_CALLS_TOO_SMALL(0X000006CE, "The maximum number of calls is too small."),
	RPC_S_STRING_TOO_LONG(0X000006CF, "The string is too long."),
	RPC_S_PROTSEQ_NOT_FOUND(0X000006D0, "The RPC protocol sequence was not found."),
	RPC_S_PROCNUM_OUT_OF_RANGE(0X000006D1, "The procedure number is out of range."),
	RPC_S_BINDING_HAS_NO_AUTH(0X000006D2, "The binding does not contain any authentication information."),
	RPC_S_UNKNOWN_AUTHN_SERVICE(0X000006D3, "The authentication service is unknown."),
	RPC_S_UNKNOWN_AUTHN_LEVEL(0X000006D4, "The authentication level is unknown."),
	RPC_S_INVALID_AUTH_IDENTITY(0X000006D5, "The security context is invalid."),
	RPC_S_UNKNOWN_AUTHZ_SERVICE(0X000006D6, "The authorization service is unknown."),
	EPT_S_INVALID_ENTRY(0X000006D7, "The entry is invalid."),
	EPT_S_CANT_PERFORM_OP(0X000006D8, "The server endpoint cannot perform the operation."),
	EPT_S_NOT_REGISTERED(0X000006D9, "There are no more endpoints available from the   endpoint mapper."),
	RPC_S_NOTHING_TO_EXPORT(0X000006DA, "No interfaces have been exported."),
	RPC_S_INCOMPLETE_NAME(0X000006DB, "The entry name is incomplete."),
	RPC_S_INVALID_VERS_OPTION(0X000006DC, "The version option is invalid."),
	RPC_S_NO_MORE_MEMBERS(0X000006DD, "There are no more members."),
	RPC_S_NOT_ALL_OBJS_UNEXPORTED(0X000006DE, "There is nothing to unexport."),
	RPC_S_INTERFACE_NOT_FOUND(0X000006DF, "The interface was not found."),
	RPC_S_ENTRY_ALREADY_EXISTS(0X000006E0, "The entry already exists."),
	RPC_S_ENTRY_NOT_FOUND(0X000006E1, "The entry is not found."),
	RPC_S_NAME_SERVICE_UNAVAILABLE(0X000006E2, "The name service is unavailable."),
	RPC_S_INVALID_NAF_ID(0X000006E3, "The network address family is invalid."),
	RPC_S_CANNOT_SUPPORT(0X000006E4, "The requested operation is not supported."),
	RPC_S_NO_CONTEXT_AVAILABLE(0X000006E5, "No security context is available to allow   impersonation."),
	RPC_S_INTERNAL_ERROR(0X000006E6, "An internal error occurred in an RPC."),
	RPC_S_ZERO_DIVIDE(0X000006E7, "The RPC server attempted an integer division by zero."),
	RPC_S_ADDRESS_ERROR(0X000006E8, "An addressing error occurred in the RPC server."),
	RPC_S_FP_DIV_ZERO(0X000006E9, "A floating-point operation at the RPC server caused a   division by zero."),
	RPC_S_FP_UNDERFLOW(0X000006EA, "A floating-point underflow occurred at the RPC server."),
	RPC_S_FP_OVERFLOW(0X000006EB, "A floating-point overflow occurred at the RPC server."),
	RPC_X_NO_MORE_ENTRIES(0X000006EC, "The list of RPC servers available for the binding of   auto handles has been exhausted."),
	RPC_X_SS_CHAR_TRANS_OPEN_FAIL(0X000006ED, "Unable to open the character translation table file."),
	RPC_X_SS_CHAR_TRANS_SHORT_FILE(0X000006EE, "The file containing the character translation table   has fewer than 512 bytes."),
	RPC_X_SS_IN_NULL_CONTEXT(0X000006EF, "A null context handle was passed from the client to   the host during an RPC."),
	RPC_X_SS_CONTEXT_DAMAGED(0X000006F1, "The context handle changed during an RPC."),
	RPC_X_SS_HANDLES_MISMATCH(0X000006F2, "The binding handles passed to an RPC do not match."),
	RPC_X_SS_CANNOT_GET_CALL_HANDLE(0X000006F3, "The stub is unable to get the RPC handle."),
	RPC_X_NULL_REF_POINTER(0X000006F4, "A null reference pointer was passed to the stub."),
	RPC_X_ENUM_VALUE_OUT_OF_RANGE(0X000006F5, "The enumeration value is out of range."),
	RPC_X_BYTE_COUNT_TOO_SMALL(0X000006F6, "The byte count is too small."),
	RPC_X_BAD_STUB_DATA(0X000006F7, "The stub received bad data."),
	ERROR_INVALID_USER_BUFFER(0X000006F8, "The supplied user buffer is not valid for the   requested operation."),
	ERROR_UNRECOGNIZED_MEDIA(0X000006F9, "The disk media is not recognized. It might not be   formatted."),
	ERROR_NO_TRUST_LSA_SECRET(0X000006FA, "The workstation does not have a trust secret."),
	ERROR_NO_TRUST_SAM_ACCOUNT(0X000006FB, "The security database on the server does not have a   computer account for this workstation trust relationship."),
	ERROR_TRUSTED_DOMAIN_FAILURE(0X000006FC, "The trust relationship between the primary domain and   the trusted domain failed."),
	ERROR_TRUSTED_RELATIONSHIP_FAILURE(0X000006FD, "The trust relationship between this workstation and   the primary domain failed."),
	ERROR_TRUST_FAILURE(0X000006FE, "The network logon failed."),
	RPC_S_CALL_IN_PROGRESS(0X000006FF, "An RPC is already in progress for this thread."),
	ERROR_NETLOGON_NOT_STARTED(0X00000700, "An attempt was made to log on, but the network logon   service was not started."),
	ERROR_ACCOUNT_EXPIRED(0X00000701, "The user's account has expired."),
	ERROR_REDIRECTOR_HAS_OPEN_HANDLES(0X00000702, "The redirector is in use and cannot be unloaded."),
	ERROR_PRINTER_DRIVER_ALREADY_INSTALLED(0X00000703, "The specified printer driver is already installed."),
	ERROR_UNKNOWN_PORT(0X00000704, "The specified port is unknown."),
	ERROR_UNKNOWN_PRINTER_DRIVER(0X00000705, "The printer driver is unknown."),
	ERROR_UNKNOWN_PRINTPROCESSOR(0X00000706, "The print processor is unknown."),
	ERROR_INVALID_SEPARATOR_FILE(0X00000707, "The specified separator file is invalid."),
	ERROR_INVALID_PRIORITY(0X00000708, "The specified priority is invalid."),
	ERROR_INVALID_PRINTER_NAME(0X00000709, "The printer name is invalid."),
	ERROR_PRINTER_ALREADY_EXISTS(0X0000070A, "The printer already exists."),
	ERROR_INVALID_PRINTER_COMMAND(0X0000070B, "The printer command is invalid."),
	ERROR_INVALID_DATATYPE(0X0000070C, "The specified data type is invalid."),
	ERROR_INVALID_ENVIRONMENT(0X0000070D, "The environment specified is invalid."),
	RPC_S_NO_MORE_BINDINGS(0X0000070E, "There are no more bindings."),
	ERROR_NOLOGON_INTERDOMAIN_TRUST_ACCOUNT(0X0000070F, "The account used is an interdomain trust account. Use   your global user account or local user account to access this server."),
	ERROR_NOLOGON_WORKSTATION_TRUST_ACCOUNT(0X00000710, "The account used is a computer account. Use your   global user account or local user account to access this server."),
	ERROR_NOLOGON_SERVER_TRUST_ACCOUNT(0X00000711, "The account used is a server trust account. Use your   global user account or local user account to access this server."),
	ERROR_DOMAIN_TRUST_INCONSISTENT(0X00000712, "The name or SID of the domain specified is   inconsistent with the trust information for that domain."),
	ERROR_SERVER_HAS_OPEN_HANDLES(0X00000713, "The server is in use and cannot be unloaded."),
	ERROR_RESOURCE_DATA_NOT_FOUND(0X00000714, "The specified image file did not contain a resource   section."),
	ERROR_RESOURCE_TYPE_NOT_FOUND(0X00000715, "The specified resource type cannot be found in the   image file."),
	ERROR_RESOURCE_NAME_NOT_FOUND(0X00000716, "The specified resource name cannot be found in the   image file."),
	ERROR_RESOURCE_LANG_NOT_FOUND(0X00000717, "The specified resource language ID cannot be found in   the image file."),
	ERROR_NOT_ENOUGH_QUOTA(0X00000718, "Not enough quota is available to process this command."),
	RPC_S_NO_INTERFACES(0X00000719, "No interfaces have been registered."),
	RPC_S_CALL_CANCELLED(0X0000071A, "The RPC was canceled."),
	RPC_S_BINDING_INCOMPLETE(0X0000071B, "The binding handle does not contain all the required information."),
	RPC_S_COMM_FAILURE(0X0000071C, "A communications failure occurred during an RPC."),
	RPC_S_UNSUPPORTED_AUTHN_LEVEL(0X0000071D, "The requested authentication level is not supported."),
	RPC_S_NO_PRINC_NAME(0X0000071E, "No principal name is registered."),
	RPC_S_NOT_RPC_ERROR(0X0000071F, "The error specified is not a valid Windows RPC error   code."),
	RPC_S_UUID_LOCAL_ONLY(0X00000720, "A UUID that is valid only on this computer has been   allocated."),
	RPC_S_SEC_PKG_ERROR(0X00000721, "A security package-specific error occurred."),
	RPC_S_NOT_CANCELLED(0X00000722, "The thread is not canceled."),
	RPC_X_INVALID_ES_ACTION(0X00000723, "Invalid operation on the encoding/decoding handle."),
	RPC_X_WRONG_ES_VERSION(0X00000724, "Incompatible version of the serializing package."),
	RPC_X_WRONG_STUB_VERSION(0X00000725, "Incompatible version of the RPC stub."),
	RPC_X_INVALID_PIPE_OBJECT(0X00000726, "The RPC pipe object is invalid or corrupted."),
	RPC_X_WRONG_PIPE_ORDER(0X00000727, "An invalid operation was attempted on an RPC pipe   object."),
	RPC_X_WRONG_PIPE_VERSION(0X00000728, "Unsupported RPC pipe version."),
	RPC_S_GROUP_MEMBER_NOT_FOUND(0X0000076A, "The group member was not found."),
	EPT_S_CANT_CREATE(0X0000076B, "The endpoint mapper database entry could not be   created."),
	RPC_S_INVALID_OBJECT(0X0000076C, "The object UUID is the nil UUID."),
	ERROR_INVALID_TIME(0X0000076D, "The specified time is invalid."),
	ERROR_INVALID_FORM_NAME(0X0000076E, "The specified form name is invalid."),
	ERROR_INVALID_FORM_SIZE(0X0000076F, "The specified form size is invalid."),
	ERROR_ALREADY_WAITING(0X00000770, "The specified printer handle is already being waited   on."),
	ERROR_PRINTER_DELETED(0X00000771, "The specified printer has been deleted."),
	ERROR_INVALID_PRINTER_STATE(0X00000772, "The state of the printer is invalid."),
	ERROR_PASSWORD_MUST_CHANGE(0X00000773, "The user's password must be changed before logging on   the first time."),
	ERROR_DOMAIN_CONTROLLER_NOT_FOUND(0X00000774, "Could not find the domain controller for this domain."),
	ERROR_ACCOUNT_LOCKED_OUT(0X00000775, "The referenced account is currently locked out and   cannot be logged on to."),
	OR_INVALID_OXID(0X00000776, "The object exporter specified was not found."),
	OR_INVALID_OID(0X00000777, "The object specified was not found."),
	OR_INVALID_SET(0X00000778, "The object set specified was not found."),
	RPC_S_SEND_INCOMPLETE(0X00000779, "Some data remains to be sent in the request buffer."),
	RPC_S_INVALID_ASYNC_HANDLE(0X0000077A, "Invalid asynchronous RPC handle."),
	RPC_S_INVALID_ASYNC_CALL(0X0000077B, "Invalid asynchronous RPC call handle for this   operation."),
	RPC_X_PIPE_CLOSED(0X0000077C, "The RPC pipe object has already been closed."),
	RPC_X_PIPE_DISCIPLINE_ERROR(0X0000077D, "The RPC call completed before all pipes were   processed."),
	RPC_X_PIPE_EMPTY(0X0000077E, "No more data is available from the RPC pipe."),
	ERROR_NO_SITENAME(0X0000077F, "No site name is available for this machine."),
	ERROR_CANT_ACCESS_FILE(0X00000780, "The file cannot be accessed by the system."),
	ERROR_CANT_RESOLVE_FILENAME(0X00000781, "The name of the file cannot be resolved by the system."),
	RPC_S_ENTRY_TYPE_MISMATCH(0X00000782, "The entry is not of the expected type."),
	RPC_S_NOT_ALL_OBJS_EXPORTED(0X00000783, "Not all object UUIDs could be exported to the   specified entry."),
	RPC_S_INTERFACE_NOT_EXPORTED(0X00000784, "The interface could not be exported to the specified entry."),
	RPC_S_PROFILE_NOT_ADDED(0X00000785, "The specified profile entry could not be added."),
	RPC_S_PRF_ELT_NOT_ADDED(0X00000786, "The specified profile element could not be added."),
	RPC_S_PRF_ELT_NOT_REMOVED(0X00000787, "The specified profile element could not be removed."),
	RPC_S_GRP_ELT_NOT_ADDED(0X00000788, "The group element could not be added."),
	RPC_S_GRP_ELT_NOT_REMOVED(0X00000789, "The group element could not be removed."),
	ERROR_KM_DRIVER_BLOCKED(0X0000078A, "The printer driver is not compatible with a policy   enabled on your computer that blocks Windows NT 4.0 operating system drivers."),
	ERROR_CONTEXT_EXPIRED(0X0000078B, "The context has expired and can no longer be used."),
	ERROR_PER_USER_TRUST_QUOTA_EXCEEDED(0X0000078C, "The current user's delegated trust creation quota has   been exceeded."),
	ERROR_ALL_USER_TRUST_QUOTA_EXCEEDED(0X0000078D, "The total delegated trust creation quota has been   exceeded."),
	ERROR_USER_DELETE_TRUST_QUOTA_EXCEEDED(0X0000078E, "The current user's delegated trust deletion quota has   been exceeded."),
	ERROR_AUTHENTICATION_FIREWALL_FAILED(0X0000078F, "Logon failure: The machine you are logging on to is   protected by an authentication firewall. The specified account is not allowed   to authenticate to the machine."),
	ERROR_REMOTE_PRINT_CONNECTIONS_BLOCKED(0X00000790, "Remote connections to the Print Spooler are blocked by   a policy set on your machine."),
	ERROR_INVALID_PIXEL_FORMAT(0X000007D0, "The pixel format is invalid."),
	ERROR_BAD_DRIVER(0X000007D1, "The specified driver is invalid."),
	ERROR_INVALID_WINDOW_STYLE(0X000007D2, "The window style or class attribute is invalid for   this operation."),
	ERROR_METAFILE_NOT_SUPPORTED(0X000007D3, "The requested metafile operation is not supported."),
	ERROR_TRANSFORM_NOT_SUPPORTED(0X000007D4, "The requested transformation operation is not   supported."),
	ERROR_CLIPPING_NOT_SUPPORTED(0X000007D5, "The requested clipping operation is not supported."),
	ERROR_INVALID_CMM(0X000007DA, "The specified color management module is invalid."),
	ERROR_INVALID_PROFILE(0X000007DB, "The specified color profile is invalid."),
	ERROR_TAG_NOT_FOUND(0X000007DC, "The specified tag was not found."),
	ERROR_TAG_NOT_PRESENT(0X000007DD, "A required tag is not present."),
	ERROR_DUPLICATE_TAG(0X000007DE, "The specified tag is already present."),
	ERROR_PROFILE_NOT_ASSOCIATED_WITH_DEVICE(0X000007DF, "The specified color profile is not associated with any   device."),
	ERROR_PROFILE_NOT_FOUND(0X000007E0, "The specified color profile was not found."),
	ERROR_INVALID_COLORSPACE(0X000007E1, "The specified color space is invalid."),
	ERROR_ICM_NOT_ENABLED(0X000007E2, "Image Color Management is not enabled."),
	ERROR_DELETING_ICM_XFORM(0X000007E3, "There was an error while deleting the color transform."),
	ERROR_INVALID_TRANSFORM(0X000007E4, "The specified color transform is invalid."),
	ERROR_COLORSPACE_MISMATCH(0X000007E5, "The specified transform does not match the bitmap's   color space."),
	ERROR_INVALID_COLORINDEX(0X000007E6, "The specified named color index is not present in the   profile."),
	ERROR_PROFILE_DOES_NOT_MATCH_DEVICE(0X000007E7, "The specified profile is intended for a device of a   different type than the specified device."),
	ERROR_IO_REISSUE_AS_CACHED(0X00000F6E, "Reissue the given operation as a cached I/O operation."),

	ERROR_CAN_NOT_DEL_LOCAL_WINS(0X00000FA1, "The local WINS cannot be deleted."),

	ERROR_STATIC_INIT(0X00000FA2, "The importation from the file failed."),

	ERROR_INC_BACKUP(0X00000FA3, "The backup failed. Was a full backup done before?"),

	ERROR_FULL_BACKUP(0X00000FA4, "The backup failed. Check the directory to which you   are backing the database."),

	ERROR_REC_NON_EXISTENT(0X00000FA5, "The name does not exist in the WINS database."),

	ERROR_RPL_NOT_ALLOWED(0X00000FA6, "Replication with a nonconfigured partner is not   allowed."),

	PEERDIST_ERROR_CONTENTINFO_VERSION_UNSUPPORTED(0X00000FD2, "The version of the supplied content information is not   supported."),

	PEERDIST_ERROR_CANNOT_PARSE_CONTENTINFO(0X00000FD3, "The supplied content information is malformed."),

	PEERDIST_ERROR_MISSING_DATA(0X00000FD4, "The requested data cannot be found in local or peer   caches."),

	PEERDIST_ERROR_NO_MORE(0X00000FD5, "No more data is available or required."),

	PEERDIST_ERROR_NOT_INITIALIZED(0X00000FD6, "The supplied object has not been initialized."),

	PEERDIST_ERROR_ALREADY_INITIALIZED(0X00000FD7, "The supplied object has already been initialized."),

	PEERDIST_ERROR_SHUTDOWN_IN_PROGRESS(0X00000FD8, "A shutdown operation is already in progress."),

	PEERDIST_ERROR_INVALIDATED(0X00000FD9, "The supplied object has already been invalidated."),

	PEERDIST_ERROR_ALREADY_EXISTS(0X00000FDA, "An element already exists and was not replaced."),

	PEERDIST_ERROR_OPERATION_NOTFOUND(0X00000FDB, "Cannot cancel the requested operation as it has   already been completed."),

	PEERDIST_ERROR_ALREADY_COMPLETED(0X00000FDC, "Cannot perform the requested operation because it has   already been carried out."),

	PEERDIST_ERROR_OUT_OF_BOUNDS(0X00000FDD, "An operation accessed data beyond the bounds of valid   data."),

	PEERDIST_ERROR_VERSION_UNSUPPORTED(0X00000FDE, "The requested version is not supported."),

	PEERDIST_ERROR_INVALID_CONFIGURATION(0X00000FDF, "A configuration value is invalid."),

	PEERDIST_ERROR_SERVICE_UNAVAILABLE(0X00000FE1, "PeerDist Service is still initializing and will be   available shortly."),

	ERROR_WMI_GUID_NOT_FOUND(0X00001068, "The GUID passed was not recognized as valid by a WMI   data provider."),

	ERROR_WMI_INSTANCE_NOT_FOUND(0X00001069, "The instance name passed was not recognized as valid   by a WMI data provider."),

	ERROR_WMI_ITEMID_NOT_FOUND(0X0000106A, "The data item ID passed was not recognized as valid by   a WMI data provider."),

	ERROR_WMI_TRY_AGAIN(0X0000106B, "The WMI request could not be completed and should be   retried."),

	ERROR_WMI_DP_NOT_FOUND(0X0000106C, "The WMI data provider could not be located."),

	ERROR_WMI_UNRESOLVED_INSTANCE_REF(0X0000106D, "The WMI data provider references an instance set that   has not been registered."),

	ERROR_WMI_ALREADY_ENABLED(0X0000106E, "The WMI data block or event notification has already   been enabled."),

	ERROR_WMI_GUID_DISCONNECTED(0X0000106F, "The WMI data block is no longer available."),

	ERROR_WMI_SERVER_UNAVAILABLE(0X00001070, "The WMI data service is not available."),

	ERROR_WMI_DP_FAILED(0X00001071, "The WMI data provider failed to carry out the request."),

	ERROR_WMI_INVALID_MOF(0X00001072, "The WMI Managed Object Format (MOF) information is not   valid."),

	ERROR_WMI_INVALID_REGINFO(0X00001073, "The WMI registration information is not valid."),

	ERROR_WMI_ALREADY_DISABLED(0X00001074, "The WMI data block or event notification has already   been disabled."),

	ERROR_WMI_READ_ONLY(0X00001075, "The WMI data item or data block is read-only."),

	ERROR_WMI_SET_FAILURE(0X00001076, "The WMI data item or data block could not be changed."),

	ERROR_INVALID_MEDIA(0X000010CC, "The media identifier does not represent a valid   medium."),

	ERROR_INVALID_LIBRARY(0X000010CD, "The library identifier does not represent a valid   library."),

	ERROR_INVALID_MEDIA_POOL(0X000010CE, "The media pool identifier does not represent a valid   media pool."),

	ERROR_DRIVE_MEDIA_MISMATCH(0X000010CF, "The drive and medium are not compatible, or they exist   in different libraries."),

	ERROR_MEDIA_OFFLINE(0X000010D0, "The medium currently exists in an offline library and   must be online to perform this operation."),

	ERROR_LIBRARY_OFFLINE(0X000010D1, "The operation cannot be performed on an offline   library."),

	ERROR_EMPTY(0X000010D2, "The library, drive, or media pool is empty."),

	ERROR_NOT_EMPTY(0X000010D3, "The library, drive, or media pool must be empty to   perform this operation."),

	ERROR_MEDIA_UNAVAILABLE(0X000010D4, "No media is currently available in this media pool or   library."),

	ERROR_RESOURCE_DISABLED(0X000010D5, "A resource required for this operation is disabled."),

	ERROR_INVALID_CLEANER(0X000010D6, "The media identifier does not represent a valid   cleaner."),

	ERROR_UNABLE_TO_CLEAN(0X000010D7, "The drive cannot be cleaned or does not support   cleaning."),

	ERROR_OBJECT_NOT_FOUND(0X000010D8, "The object identifier does not represent a valid   object."),

	ERROR_DATABASE_FAILURE(0X000010D9, "Unable to read from or write to the database."),

	ERROR_DATABASE_FULL(0X000010DA, "The database is full."),

	ERROR_MEDIA_INCOMPATIBLE(0X000010DB, "The medium is not compatible with the device or media   pool."),

	ERROR_RESOURCE_NOT_PRESENT(0X000010DC, "The resource required for this operation does not   exist."),

	ERROR_INVALID_OPERATION(0X000010DD, "The operation identifier is not valid."),

	ERROR_MEDIA_NOT_AVAILABLE(0X000010DE, "The media is not mounted or ready for use."),

	ERROR_DEVICE_NOT_AVAILABLE(0X000010DF, "The device is not ready for use."),

	ERROR_REQUEST_REFUSED(0X000010E0, "The operator or administrator has refused the request."),

	ERROR_INVALID_DRIVE_OBJECT(0X000010E1, "The drive identifier does not represent a valid drive."),

	ERROR_LIBRARY_FULL(0X000010E2, "Library is full. No slot is available for use."),

	ERROR_MEDIUM_NOT_ACCESSIBLE(0X000010E3, "The transport cannot access the medium."),

	ERROR_UNABLE_TO_LOAD_MEDIUM(0X000010E4, "Unable to load the medium into the drive."),

	ERROR_UNABLE_TO_INVENTORY_DRIVE(0X000010E5, "Unable to retrieve the drive status."),

	ERROR_UNABLE_TO_INVENTORY_SLOT(0X000010E6, "Unable to retrieve the slot status."),

	ERROR_UNABLE_TO_INVENTORY_TRANSPORT(0X000010E7, "Unable to retrieve status about the transport."),

	ERROR_TRANSPORT_FULL(0X000010E8, "Cannot use the transport because it is already in use."),

	ERROR_CONTROLLING_IEPORT(0X000010E9, "Unable to open or close the inject/eject port."),

	ERROR_UNABLE_TO_EJECT_MOUNTED_MEDIA(0X000010EA, "Unable to eject the medium because it is in a drive."),

	ERROR_CLEANER_SLOT_SET(0X000010EB, "A cleaner slot is already reserved."),

	ERROR_CLEANER_SLOT_NOT_SET(0X000010EC, "A cleaner slot is not reserved."),

	ERROR_CLEANER_CARTRIDGE_SPENT(0X000010ED, "The cleaner cartridge has performed the maximum number   of drive cleanings."),

	ERROR_UNEXPECTED_OMID(0X000010EE, "Unexpected on-medium identifier."),

	ERROR_CANT_DELETE_LAST_ITEM(0X000010EF, "The last remaining item in this group or resource   cannot be deleted."),

	ERROR_MESSAGE_EXCEEDS_MAX_SIZE(0X000010F0, "The message provided exceeds the maximum size allowed   for this parameter."),

	ERROR_VOLUME_CONTAINS_SYS_FILES(0X000010F1, "The volume contains system or paging files."),

	ERROR_INDIGENOUS_TYPE(0X000010F2, "The media type cannot be removed from this library   because at least one drive in the library reports it can support this media   type."),

	ERROR_NO_SUPPORTING_DRIVES(0X000010F3, "This offline media cannot be mounted on this system   because no enabled drives are present that can be used."),

	ERROR_CLEANER_CARTRIDGE_INSTALLED(0X000010F4, "A cleaner cartridge is present in the tape library."),

	ERROR_IEPORT_FULL(0X000010F5, "Cannot use the IEport because it is not empty."),

	ERROR_FILE_OFFLINE(0X000010FE, "The remote storage service was not able to recall the   file."),

	ERROR_REMOTE_STORAGE_NOT_ACTIVE(0X000010FF, "The remote storage service is not operational at this   time."),

	ERROR_REMOTE_STORAGE_MEDIA_ERROR(0X00001100, "The remote storage service encountered a media error."),

	ERROR_NOT_A_REPARSE_POINT(0X00001126, "The file or directory is not a reparse point."),

	ERROR_REPARSE_ATTRIBUTE_CONFLICT(0X00001127, "The reparse point attribute cannot be set because it   conflicts with an existing attribute."),

	ERROR_INVALID_REPARSE_DATA(0X00001128, "The data present in the reparse point buffer is   invalid."),

	ERROR_REPARSE_TAG_INVALID(0X00001129, "The tag present in the reparse point buffer is   invalid."),

	ERROR_REPARSE_TAG_MISMATCH(0X0000112A, "There is a mismatch between the tag specified in the   request and the tag present in the reparse point."),

	ERROR_VOLUME_NOT_SIS_ENABLED(0X00001194, "Single Instance Storage (SIS) is not available on this   volume."),

	ERROR_DEPENDENT_RESOURCE_EXISTS(0X00001389, "The operation cannot be completed because other   resources depend on this resource."),

	ERROR_DEPENDENCY_NOT_FOUND(0X0000138A, "The cluster resource dependency cannot be found."),

	ERROR_DEPENDENCY_ALREADY_EXISTS(0X0000138B, "The cluster resource cannot be made dependent on the   specified resource because it is already dependent."),

	ERROR_RESOURCE_NOT_ONLINE(0X0000138C, "The cluster resource is not online."),

	ERROR_HOST_NODE_NOT_AVAILABLE(0X0000138D, "A cluster node is not available for this operation."),

	ERROR_RESOURCE_NOT_AVAILABLE(0X0000138E, "The cluster resource is not available."),

	ERROR_RESOURCE_NOT_FOUND(0X0000138F, "The cluster resource could not be found."),

	ERROR_SHUTDOWN_CLUSTER(0X00001390, "The cluster is being shut down."),

	ERROR_CANT_EVICT_ACTIVE_NODE(0X00001391, "A cluster node cannot be evicted from the cluster   unless the node is down or it is the last node."),

	ERROR_OBJECT_ALREADY_EXISTS(0X00001392, "The object already exists."),

	ERROR_OBJECT_IN_LIST(0X00001393, "The object is already in the list."),

	ERROR_GROUP_NOT_AVAILABLE(0X00001394, "The cluster group is not available for any new   requests."),

	ERROR_GROUP_NOT_FOUND(0X00001395, "The cluster group could not be found."),

	ERROR_GROUP_NOT_ONLINE(0X00001396, "The operation could not be completed because the   cluster group is not online."),

	ERROR_HOST_NODE_NOT_RESOURCE_OWNER(0X00001397, "The operation failed because either the specified   cluster node is not the owner of the resource, or the node is not a possible   owner of the resource."),

	ERROR_HOST_NODE_NOT_GROUP_OWNER(0X00001398, "The operation failed because either the specified   cluster node is not the owner of the group, or the node is not a possible   owner of the group."),

	ERROR_RESMON_CREATE_FAILED(0X00001399, "The cluster resource could not be created in the   specified resource monitor."),

	ERROR_RESMON_ONLINE_FAILED(0X0000139A, "The cluster resource could not be brought online by   the resource monitor."),

	ERROR_RESOURCE_ONLINE(0X0000139B, "The operation could not be completed because the   cluster resource is online."),

	ERROR_QUORUM_RESOURCE(0X0000139C, "The cluster resource could not be deleted or brought   offline because it is the quorum resource."),

	ERROR_NOT_QUORUM_CAPABLE(0X0000139D, "The cluster could not make the specified resource a   quorum resource because it is not capable of being a quorum resource."),

	ERROR_CLUSTER_SHUTTING_DOWN(0X0000139E, "The cluster software is shutting down."),

	ERROR_INVALID_STATE(0X0000139F, "The group or resource is not in the correct state to   perform the requested operation."),

	ERROR_RESOURCE_PROPERTIES_STORED(0X000013A0, "The properties were stored but not all changes will   take effect until the next time the resource is brought online."),

	ERROR_NOT_QUORUM_CLASS(0X000013A1, "The cluster could not make the specified resource a   quorum resource because it does not belong to a shared storage class."),

	ERROR_CORE_RESOURCE(0X000013A2, "The cluster resource could not be deleted because it   is a core resource."),

	ERROR_QUORUM_RESOURCE_ONLINE_FAILED(0X000013A3, "The quorum resource failed to come online."),

	ERROR_QUORUMLOG_OPEN_FAILED(0X000013A4, "The quorum log could not be created or mounted   successfully."),

	ERROR_CLUSTERLOG_CORRUPT(0X000013A5, "The cluster log is corrupt."),

	ERROR_CLUSTERLOG_RECORD_EXCEEDS_MAXSIZE(0X000013A6, "The record could not be written to the cluster log   because it exceeds the maximum size."),

	ERROR_CLUSTERLOG_EXCEEDS_MAXSIZE(0X000013A7, "The cluster log exceeds its maximum size."),

	ERROR_CLUSTERLOG_CHKPOINT_NOT_FOUND(0X000013A8, "No checkpoint record was found in the cluster log."),

	ERROR_CLUSTERLOG_NOT_ENOUGH_SPACE(0X000013A9, "The minimum required disk space needed for logging is   not available."),

	ERROR_QUORUM_OWNER_ALIVE(0X000013AA, "The cluster node failed to take control of the quorum   resource because the resource is owned by another active node."),

	ERROR_NETWORK_NOT_AVAILABLE(0X000013AB, "A cluster network is not available for this operation."),

	ERROR_NODE_NOT_AVAILABLE(0X000013AC, "A cluster node is not available for this operation."),

	ERROR_ALL_NODES_NOT_AVAILABLE(0X000013AD, "All cluster nodes must be running to perform this   operation."),

	ERROR_RESOURCE_FAILED(0X000013AE, "A cluster resource failed."),

	ERROR_CLUSTER_INVALID_NODE(0X000013AF, "The cluster node is not valid."),

	ERROR_CLUSTER_NODE_EXISTS(0X000013B0, "The cluster node already exists."),

	ERROR_CLUSTER_JOIN_IN_PROGRESS(0X000013B1, "A node is in the process of joining the cluster."),

	ERROR_CLUSTER_NODE_NOT_FOUND(0X000013B2, "The cluster node was not found."),

	ERROR_CLUSTER_LOCAL_NODE_NOT_FOUND(0X000013B3, "The cluster local node information was not found."),

	ERROR_CLUSTER_NETWORK_EXISTS(0X000013B4, "The cluster network already exists."),

	ERROR_CLUSTER_NETWORK_NOT_FOUND(0X000013B5, "The cluster network was not found."),

	ERROR_CLUSTER_NETINTERFACE_EXISTS(0X000013B6, "The cluster network interface already exists."),

	ERROR_CLUSTER_NETINTERFACE_NOT_FOUND(0X000013B7, "The cluster network interface was not found."),

	ERROR_CLUSTER_INVALID_REQUEST(0X000013B8, "The cluster request is not valid for this object."),

	ERROR_CLUSTER_INVALID_NETWORK_PROVIDER(0X000013B9, "The cluster network provider is not valid."),

	ERROR_CLUSTER_NODE_DOWN(0X000013BA, "The cluster node is down."),

	ERROR_CLUSTER_NODE_UNREACHABLE(0X000013BB, "The cluster node is not reachable."),

	ERROR_CLUSTER_NODE_NOT_MEMBER(0X000013BC, "The cluster node is not a member of the cluster."),

	ERROR_CLUSTER_JOIN_NOT_IN_PROGRESS(0X000013BD, "A cluster join operation is not in progress."),

	ERROR_CLUSTER_INVALID_NETWORK(0X000013BE, "The cluster network is not valid."),

	ERROR_CLUSTER_NODE_UP(0X000013C0, "The cluster node is up."),

	ERROR_CLUSTER_IPADDR_IN_USE(0X000013C1, "The cluster IP address is already in use."),

	ERROR_CLUSTER_NODE_NOT_PAUSED(0X000013C2, "The cluster node is not paused."),

	ERROR_CLUSTER_NO_SECURITY_CONTEXT(0X000013C3, "No cluster security context is available."),

	ERROR_CLUSTER_NETWORK_NOT_INTERNAL(0X000013C4, "The cluster network is not configured for internal   cluster communication."),

	ERROR_CLUSTER_NODE_ALREADY_UP(0X000013C5, "The cluster node is already up."),

	ERROR_CLUSTER_NODE_ALREADY_DOWN(0X000013C6, "The cluster node is already down."),

	ERROR_CLUSTER_NETWORK_ALREADY_ONLINE(0X000013C7, "The cluster network is already online."),

	ERROR_CLUSTER_NETWORK_ALREADY_OFFLINE(0X000013C8, "The cluster network is already offline."),

	ERROR_CLUSTER_NODE_ALREADY_MEMBER(0X000013C9, "The cluster node is already a member of the cluster."),

	ERROR_CLUSTER_LAST_INTERNAL_NETWORK(0X000013CA, "The cluster network is the only one configured for   internal cluster communication between two or more active cluster nodes. The   internal communication capability cannot be removed from the network."),

	ERROR_CLUSTER_NETWORK_HAS_DEPENDENTS(0X000013CB, "One or more cluster resources depend on the network to   provide service to clients. The client access capability cannot be removed   from the network."),

	ERROR_INVALID_OPERATION_ON_QUORUM(0X000013CC, "This operation cannot be performed on the cluster   resource because it is the quorum resource. This quorum resource cannot be   brought offline and its possible owners list cannot be modified."),

	ERROR_DEPENDENCY_NOT_ALLOWED(0X000013CD, "The cluster quorum resource is not allowed to have any   dependencies."),

	ERROR_CLUSTER_NODE_PAUSED(0X000013CE, "The cluster node is paused."),

	ERROR_NODE_CANT_HOST_RESOURCE(0X000013CF, "The cluster resource cannot be brought online. The   owner node cannot run this resource."),

	ERROR_CLUSTER_NODE_NOT_READY(0X000013D0, "The cluster node is not ready to perform the requested   operation."),

	ERROR_CLUSTER_NODE_SHUTTING_DOWN(0X000013D1, "The cluster node is shutting down."),

	ERROR_CLUSTER_JOIN_ABORTED(0X000013D2, "The cluster join operation was aborted."),

	ERROR_CLUSTER_INCOMPATIBLE_VERSIONS(0X000013D3, "The cluster join operation failed due to incompatible   software versions between the joining node and its sponsor."),

	ERROR_CLUSTER_MAXNUM_OF_RESOURCES_EXCEEDED(0X000013D4, "This resource cannot be created because the cluster   has reached the limit on the number of resources it can monitor."),

	ERROR_CLUSTER_SYSTEM_CONFIG_CHANGED(0X000013D5, "The system configuration changed during the cluster   join or form operation. The join or form operation was aborted."),

	ERROR_CLUSTER_RESOURCE_TYPE_NOT_FOUND(0X000013D6, "The specified resource type was not found."),

	ERROR_CLUSTER_RESTYPE_NOT_SUPPORTED(0X000013D7, "The specified node does not support a resource of this   type. This might be due to version inconsistencies or due to the absence of   the resource DLL on this node."),

	ERROR_CLUSTER_RESNAME_NOT_FOUND(0X000013D8, "The specified resource name is not supported by this   resource DLL. This might be due to a bad (or changed) name supplied to the   resource DLL."),

	ERROR_CLUSTER_NO_RPC_PACKAGES_REGISTERED(0X000013D9, "No authentication package could be registered with the   RPC server."),

	ERROR_CLUSTER_OWNER_NOT_IN_PREFLIST(0X000013DA, "You cannot bring the group online because the owner of   the group is not in the preferred list for the group. To change the owner   node for the group, move the group."),

	ERROR_CLUSTER_DATABASE_SEQMISMATCH(0X000013DB, "The join operation failed because the cluster database   sequence number has changed or is incompatible with the locker node. This can   happen during a join operation if the cluster database was changing during   the join."),

	ERROR_RESMON_INVALID_STATE(0X000013DC, "The resource monitor will not allow the fail operation   to be performed while the resource is in its current state. This can happen   if the resource is in a pending state."),

	ERROR_CLUSTER_GUM_NOT_LOCKER(0X000013DD, "A non-locker code received a request to reserve the   lock for making global updates."),

	ERROR_QUORUM_DISK_NOT_FOUND(0X000013DE, "The quorum disk could not be located by the cluster   service."),

	ERROR_DATABASE_BACKUP_CORRUPT(0X000013DF, "The backed-up cluster database is possibly corrupt."),

	ERROR_CLUSTER_NODE_ALREADY_HAS_DFS_ROOT(0X000013E0, "A DFS root already exists in this cluster node."),

	ERROR_RESOURCE_PROPERTY_UNCHANGEABLE(0X000013E1, "An attempt to modify a resource property failed   because it conflicts with another existing property."),

	ERROR_CLUSTER_MEMBERSHIP_INVALID_STATE(0X00001702, "An operation was attempted that is incompatible with   the current membership state of the node."),

	ERROR_CLUSTER_QUORUMLOG_NOT_FOUND(0X00001703, "The quorum resource does not contain the quorum log."),

	ERROR_CLUSTER_MEMBERSHIP_HALT(0X00001704, "The membership engine requested shutdown of the   cluster service on this node."),

	ERROR_CLUSTER_INSTANCE_ID_MISMATCH(0X00001705, "The join operation failed because the cluster instance   ID of the joining node does not match the cluster instance ID of the sponsor   node."),

	ERROR_CLUSTER_NETWORK_NOT_FOUND_FOR_IP(0X00001706, "A matching cluster network for the specified IP   address could not be found."),

	ERROR_CLUSTER_PROPERTY_DATA_TYPE_MISMATCH(0X00001707, "The actual data type of the property did not match the   expected data type of the property."),

	ERROR_CLUSTER_EVICT_WITHOUT_CLEANUP(0X00001708, "The cluster node was evicted from the cluster successfully,   but the node was not cleaned up. To determine what clean-up steps failed and   how to recover, see the Failover Clustering application event log using Event   Viewer."),

	ERROR_CLUSTER_PARAMETER_MISMATCH(0X00001709, "Two or more parameter values specified for a   resource's properties are in conflict."),

	ERROR_NODE_CANNOT_BE_CLUSTERED(0X0000170A, "This computer cannot be made a member of a cluster."),

	ERROR_CLUSTER_WRONG_OS_VERSION(0X0000170B, "This computer cannot be made a member of a cluster   because it does not have the correct version of Windows installed."),

	ERROR_CLUSTER_CANT_CREATE_DUP_CLUSTER_NAME(0X0000170C, "A cluster cannot be created with the specified cluster   name because that cluster name is already in use. Specify a different name   for the cluster."),

	ERROR_CLUSCFG_ALREADY_COMMITTED(0X0000170D, "The cluster configuration action has already been   committed."),

	ERROR_CLUSCFG_ROLLBACK_FAILED(0X0000170E, "The cluster configuration action could not be rolled   back."),

	ERROR_CLUSCFG_SYSTEM_DISK_DRIVE_LETTER_CONFLICT(0X0000170F, "The drive letter assigned to a system disk on one node   conflicted with the drive letter assigned to a disk on another node."),

	ERROR_CLUSTER_OLD_VERSION(0X00001710, "One or more nodes in the cluster are running a version   of Windows that does not support this operation."),

	ERROR_CLUSTER_MISMATCHED_COMPUTER_ACCT_NAME(0X00001711, "The name of the corresponding computer account does   not match the network name for this resource."),

	ERROR_CLUSTER_NO_NET_ADAPTERS(0X00001712, "No network adapters are available."),

	ERROR_CLUSTER_POISONED(0X00001713, "The cluster node has been poisoned."),

	ERROR_CLUSTER_GROUP_MOVING(0X00001714, "The group is unable to accept the request because it   is moving to another node."),

	ERROR_CLUSTER_RESOURCE_TYPE_BUSY(0X00001715, "The resource type cannot accept the request because it   is too busy performing another operation."),

	ERROR_RESOURCE_CALL_TIMED_OUT(0X00001716, "The call to the cluster resource DLL timed out."),

	ERROR_INVALID_CLUSTER_IPV6_ADDRESS(0X00001717, "The address is not valid for an IPv6 Address resource.   A global IPv6 address is required, and it must match a cluster network.   Compatibility addresses are not permitted."),

	ERROR_CLUSTER_INTERNAL_INVALID_FUNCTION(0X00001718, "An internal cluster error occurred. A call to an   invalid function was attempted."),

	ERROR_CLUSTER_PARAMETER_OUT_OF_BOUNDS(0X00001719, "A parameter value is out of acceptable range."),

	ERROR_CLUSTER_PARTIAL_SEND(0X0000171A, "A network error occurred while sending data to another   node in the cluster. The number of bytes transmitted was less than required."),

	ERROR_CLUSTER_REGISTRY_INVALID_FUNCTION(0X0000171B, "An invalid cluster registry operation was attempted."),

	ERROR_CLUSTER_INVALID_STRING_TERMINATION(0X0000171C, "An input string of characters is not properly   terminated."),

	ERROR_CLUSTER_INVALID_STRING_FORMAT(0X0000171D, "An input string of characters is not in a valid format   for the data it represents."),

	ERROR_CLUSTER_DATABASE_TRANSACTION_IN_PROGRESS(0X0000171E, "An internal cluster error occurred. A cluster database   transaction was attempted while a transaction was already in progress."),

	ERROR_CLUSTER_DATABASE_TRANSACTION_NOT_IN_PROGRESS(0X0000171F, "An internal cluster error occurred. There was an   attempt to commit a cluster database transaction while no transaction was in   progress."),

	ERROR_CLUSTER_NULL_DATA(0X00001720, "An internal cluster error occurred. Data was not   properly initialized."),

	ERROR_CLUSTER_PARTIAL_READ(0X00001721, "An error occurred while reading from a stream of data.   An unexpected number of bytes was returned."),

	ERROR_CLUSTER_PARTIAL_WRITE(0X00001722, "An error occurred while writing to a stream of data.   The required number of bytes could not be written."),

	ERROR_CLUSTER_CANT_DESERIALIZE_DATA(0X00001723, "An error occurred while deserializing a stream of   cluster data."),

	ERROR_DEPENDENT_RESOURCE_PROPERTY_CONFLICT(0X00001724, "One or more property values for this resource are in   conflict with one or more property values associated with its dependent   resources."),

	ERROR_CLUSTER_NO_QUORUM(0X00001725, "A quorum of cluster nodes was not present to form a   cluster."),

	ERROR_CLUSTER_INVALID_IPV6_NETWORK(0X00001726, "The cluster network is not valid for an IPv6 address   resource, or it does not match the configured address."),

	ERROR_CLUSTER_INVALID_IPV6_TUNNEL_NETWORK(0X00001727, "The cluster network is not valid for an IPv6 tunnel   resource. Check the configuration of the IP Address resource on which the   IPv6 tunnel resource depends."),

	ERROR_QUORUM_NOT_ALLOWED_IN_THIS_GROUP(0X00001728, "Quorum resource cannot reside in the available storage   group."),

	ERROR_ENCRYPTION_FAILED(0X00001770, "The specified file could not be encrypted."),

	ERROR_DECRYPTION_FAILED(0X00001771, "The specified file could not be decrypted."),

	ERROR_FILE_ENCRYPTED(0X00001772, "The specified file is encrypted and the user does not   have the ability to decrypt it."),

	ERROR_NO_RECOVERY_POLICY(0X00001773, "There is no valid encryption recovery policy   configured for this system."),

	ERROR_NO_EFS(0X00001774, "The required encryption driver is not loaded for this   system."),

	ERROR_WRONG_EFS(0X00001775, "The file was encrypted with a different encryption   driver than is currently loaded."),

	ERROR_NO_USER_KEYS(0X00001776, "There are no Encrypting File System (EFS) keys defined   for the user."),

	ERROR_FILE_NOT_ENCRYPTED(0X00001777, "The specified file is not encrypted."),

	ERROR_NOT_EXPORT_FORMAT(0X00001778, "The specified file is not in the defined EFS export   format."),

	ERROR_FILE_READ_ONLY(0X00001779, "The specified file is read-only."),

	ERROR_DIR_EFS_DISALLOWED(0X0000177A, "The directory has been disabled for encryption."),

	ERROR_EFS_SERVER_NOT_TRUSTED(0X0000177B, "The server is not trusted for remote encryption   operation."),

	ERROR_BAD_RECOVERY_POLICY(0X0000177C, "Recovery policy configured for this system contains   invalid recovery certificate."),

	ERROR_EFS_ALG_BLOB_TOO_BIG(0X0000177D, "The encryption algorithm used on the source file needs   a bigger key buffer than the one on the destination file."),

	ERROR_VOLUME_NOT_SUPPORT_EFS(0X0000177E, "The disk partition does not support file encryption."),

	ERROR_EFS_DISABLED(0X0000177F, "This machine is disabled for file encryption."),

	ERROR_EFS_VERSION_NOT_SUPPORT(0X00001780, "A newer system is required to decrypt this encrypted   file."),

	ERROR_CS_ENCRYPTION_INVALID_SERVER_RESPONSE(0X00001781, "The remote server sent an invalid response for a file   being opened with client-side encryption."),

	ERROR_CS_ENCRYPTION_UNSUPPORTED_SERVER(0X00001782, "Client-side encryption is not supported by the remote   server even though it claims to support it."),

	ERROR_CS_ENCRYPTION_EXISTING_ENCRYPTED_FILE(0X00001783, "File is encrypted and should be opened in client-side   encryption mode."),

	ERROR_CS_ENCRYPTION_NEW_ENCRYPTED_FILE(0X00001784, "A new encrypted file is being created and a $EFS needs   to be provided."),

	ERROR_CS_ENCRYPTION_FILE_NOT_CSE(0X00001785, "The SMB client requested a client-side extension (CSE)   file system control (FSCTL) on a non-CSE file."),

	ERROR_NO_BROWSER_SERVERS_FOUND(0X000017E6, "The list of servers for this workgroup is not   currently available"),

	SCHED_E_SERVICE_NOT_LOCALSYSTEM(0X00001838, "The Task Scheduler service must be configured to run   in the System account to function properly. Individual tasks can be   configured to run in other accounts."),

	ERROR_INVALID_TRANSACTION(0X00001A2C, "The transaction handle associated with this operation   is not valid."),

	ERROR_TRANSACTION_NOT_ACTIVE(0X00001A2D, "The requested operation was made in the context of a   transaction that is no longer active."),

	ERROR_TRANSACTION_REQUEST_NOT_VALID(0X00001A2E, "The requested operation is not valid on the   transaction object in its current state."),

	ERROR_TRANSACTION_NOT_REQUESTED(0X00001A2F, "The caller has called a response API, but the response   is not expected because the transaction manager did not issue the   corresponding request to the caller."),

	ERROR_TRANSACTION_ALREADY_ABORTED(0X00001A30, "It is too late to perform the requested operation   because the transaction has already been aborted."),

	ERROR_TRANSACTION_ALREADY_COMMITTED(0X00001A31, "It is too late to perform the requested operation   because the transaction has already been committed."),

	ERROR_TM_INITIALIZATION_FAILED(0X00001A32, "The transaction manager was unable to be successfully   initialized. Transacted operations are not supported."),

	ERROR_RESOURCEMANAGER_READ_ONLY(0X00001A33, "The specified resource manager made no changes or   updates to the resource under this transaction."),

	ERROR_TRANSACTION_NOT_JOINED(0X00001A34, "The resource manager has attempted to prepare a   transaction that it has not successfully joined."),

	ERROR_TRANSACTION_SUPERIOR_EXISTS(0X00001A35, "The transaction object already has a superior   enlistment, and the caller attempted an operation that would have created a   new superior. Only a single superior enlistment is allowed."),

	ERROR_CRM_PROTOCOL_ALREADY_EXISTS(0X00001A36, "The resource manager tried to register a protocol that   already exists."),

	ERROR_TRANSACTION_PROPAGATION_FAILED(0X00001A37, "The attempt to propagate the transaction failed."),

	ERROR_CRM_PROTOCOL_NOT_FOUND(0X00001A38, "The requested propagation protocol was not registered   as a CRM."),

	ERROR_TRANSACTION_INVALID_MARSHALL_BUFFER(0X00001A39, "The buffer passed in to PushTransaction or   PullTransaction is not in a valid format."),

	ERROR_CURRENT_TRANSACTION_NOT_VALID(0X00001A3A, "The current transaction context associated with the   thread is not a valid handle to a transaction object."),

	ERROR_TRANSACTION_NOT_FOUND(0X00001A3B, "The specified transaction object could not be opened   because it was not found."),

	ERROR_RESOURCEMANAGER_NOT_FOUND(0X00001A3C, "The specified resource manager object could not be   opened because it was not found."),

	ERROR_ENLISTMENT_NOT_FOUND(0X00001A3D, "The specified enlistment object could not be opened   because it was not found."),

	ERROR_TRANSACTIONMANAGER_NOT_FOUND(0X00001A3E, "The specified transaction manager object could not be   opened because it was not found."),

	ERROR_TRANSACTIONMANAGER_NOT_ONLINE(0X00001A3F, "The specified resource manager was unable to create an   enlistment because its associated transaction manager is not online."),

	ERROR_TRANSACTIONMANAGER_RECOVERY_NAME_COLLISION(0X00001A40, "The specified transaction manager was unable to create   the objects contained in its log file in the ObjectB namespace. Therefore,   the transaction manager was unable to recover."),

	ERROR_TRANSACTIONAL_CONFLICT(0X00001A90, "The function attempted to use a name that is reserved   for use by another transaction."),

	ERROR_RM_NOT_ACTIVE(0X00001A91, "Transaction support within the specified file system   resource manager is not started or was shut down due to an error."),

	ERROR_RM_METADATA_CORRUPT(0X00001A92, "The metadata of the resource manager has been   corrupted. The resource manager will not function."),

	ERROR_DIRECTORY_NOT_RM(0X00001A93, "The specified directory does not contain a resource   manager."),

	ERROR_TRANSACTIONS_UNSUPPORTED_REMOTE(0X00001A95, "The remote server or share does not support transacted   file operations."),

	ERROR_LOG_RESIZE_INVALID_SIZE(0X00001A96, "The requested log size is invalid."),

	ERROR_OBJECT_NO_LONGER_EXISTS(0X00001A97, "The object (file, stream, link) corresponding to the   handle has been deleted by a transaction savepoint rollback."),

	ERROR_STREAM_MINIVERSION_NOT_FOUND(0X00001A98, "The specified file miniversion was not found for this   transacted file open."),

	ERROR_STREAM_MINIVERSION_NOT_VALID(0X00001A99, "The specified file miniversion was found but has been   invalidated. The most likely cause is a transaction savepoint rollback."),

	ERROR_MINIVERSION_INACCESSIBLE_FROM_SPECIFIED_TRANSACTION(0X00001A9A, "A miniversion can only be opened in the context of the   transaction that created it."),

	ERROR_CANT_OPEN_MINIVERSION_WITH_MODIFY_INTENT(0X00001A9B, "It is not possible to open a miniversion with modify   access."),

	ERROR_CANT_CREATE_MORE_STREAM_MINIVERSIONS(0X00001A9C, "It is not possible to create any more miniversions for   this stream."),

	ERROR_REMOTE_FILE_VERSION_MISMATCH(0X00001A9E, "The remote server sent mismatching version numbers or   FID for a file opened with transactions."),

	ERROR_HANDLE_NO_LONGER_VALID(0X00001A9F, "The handle has been invalidated by a transaction. The   most likely cause is the presence of memory mapping on a file, or an open   handle when the transaction ended or rolled back to savepoint."),

	ERROR_NO_TXF_METADATA(0X00001AA0, "There is no transaction metadata on the file."),

	ERROR_LOG_CORRUPTION_DETECTED(0X00001AA1, "The log data is corrupt."),

	ERROR_CANT_RECOVER_WITH_HANDLE_OPEN(0X00001AA2, "The file cannot be recovered because a handle is still   open on it."),

	ERROR_RM_DISCONNECTED(0X00001AA3, "The transaction outcome is unavailable because the   resource manager responsible for it is disconnected."),

	ERROR_ENLISTMENT_NOT_SUPERIOR(0X00001AA4, "The request was rejected because the enlistment in   question is not a superior enlistment."),

	ERROR_RECOVERY_NOT_NEEDED(0X00001AA5, "The transactional resource manager is already   consistent. Recovery is not needed."),

	ERROR_RM_ALREADY_STARTED(0X00001AA6, "The transactional resource manager has already been   started."),

	ERROR_FILE_IDENTITY_NOT_PERSISTENT(0X00001AA7, "The file cannot be opened in a transaction because its   identity depends on the outcome of an unresolved transaction."),

	ERROR_CANT_BREAK_TRANSACTIONAL_DEPENDENCY(0X00001AA8, "The operation cannot be performed because another   transaction is depending on the fact that this property will not change."),

	ERROR_CANT_CROSS_RM_BOUNDARY(0X00001AA9, "The operation would involve a single file with two   transactional resource managers and is therefore not allowed."),

	ERROR_TXF_DIR_NOT_EMPTY(0X00001AAA, "The $Txf directory must be empty for this operation to   succeed."),

	ERROR_INDOUBT_TRANSACTIONS_EXIST(0X00001AAB, "The operation would leave a transactional resource   manager in an inconsistent state and is, therefore, not allowed."),

	ERROR_TM_VOLATILE(0X00001AAC, "The operation could not be completed because the   transaction manager does not have a log."),

	ERROR_ROLLBACK_TIMER_EXPIRED(0X00001AAD, "A rollback could not be scheduled because a previously   scheduled rollback has already been executed or is queued for execution."),

	ERROR_TXF_ATTRIBUTE_CORRUPT(0X00001AAE, "The transactional metadata attribute on the file or   directory is corrupt and unreadable."),

	ERROR_EFS_NOT_ALLOWED_IN_TRANSACTION(0X00001AAF, "The encryption operation could not be completed   because a transaction is active."),

	ERROR_TRANSACTIONAL_OPEN_NOT_ALLOWED(0X00001AB0, "This object is not allowed to be opened in a   transaction."),

	ERROR_LOG_GROWTH_FAILED(0X00001AB1, "An attempt to create space in the transactional   resource manager's log failed. The failure status has been recorded in the   event log."),

	ERROR_TRANSACTED_MAPPING_UNSUPPORTED_REMOTE(0X00001AB2, "Memory mapping (creating a mapped section) to a remote   file under a transaction is not supported."),

	ERROR_TXF_METADATA_ALREADY_PRESENT(0X00001AB3, "Transaction metadata is already present on this file   and cannot be superseded."),

	ERROR_TRANSACTION_SCOPE_CALLBACKS_NOT_SET(0X00001AB4, "A transaction scope could not be entered because the   scope handler has not been initialized."),

	ERROR_TRANSACTION_REQUIRED_PROMOTION(0X00001AB5, "Promotion was required to allow the resource manager   to enlist, but the transaction was set to disallow it."),

	ERROR_CANNOT_EXECUTE_FILE_IN_TRANSACTION(0X00001AB6, "This file is open for modification in an unresolved   transaction and can be opened for execution only by a transacted reader."),

	ERROR_TRANSACTIONS_NOT_FROZEN(0X00001AB7, "The request to thaw frozen transactions was ignored   because transactions were not previously frozen."),

	ERROR_TRANSACTION_FREEZE_IN_PROGRESS(0X00001AB8, "Transactions cannot be frozen because a freeze is   already in progress."),

	ERROR_NOT_SNAPSHOT_VOLUME(0X00001AB9, "The target volume is not a snapshot volume. This   operation is only valid on a volume mounted as a snapshot."),

	ERROR_NO_SAVEPOINT_WITH_OPEN_FILES(0X00001ABA, "The savepoint operation failed because files are open   on the transaction. This is not permitted."),

	ERROR_DATA_LOST_REPAIR(0X00001ABB, "Windows has discovered corruption in a file, and that   file has since been repaired. Data loss might have occurred."),

	ERROR_SPARSE_NOT_ALLOWED_IN_TRANSACTION(0X00001ABC, "The sparse operation could not be completed because a   transaction is active on the file."),

	ERROR_TM_IDENTITY_MISMATCH(0X00001ABD, "The call to create a transaction manager object failed   because the Tm Identity stored in the logfile does not match the Tm Identity   that was passed in as an argument."),

	ERROR_FLOATED_SECTION(0X00001ABE, "I/O was attempted on a section object that has been   floated as a result of a transaction ending. There is no valid data."),

	ERROR_CANNOT_ACCEPT_TRANSACTED_WORK(0X00001ABF, "The transactional resource manager cannot currently   accept transacted work due to a transient condition, such as low resources."),

	ERROR_CANNOT_ABORT_TRANSACTIONS(0X00001AC0, "The transactional resource manager had too many   transactions outstanding that could not be aborted. The transactional   resource manager has been shut down."),

	ERROR_CTX_WINSTATION_NAME_INVALID(0X00001B59, "The specified session name is invalid."),

	ERROR_CTX_INVALID_PD(0X00001B5A, "The specified protocol driver is invalid."),

	ERROR_CTX_PD_NOT_FOUND(0X00001B5B, "The specified protocol driver was not found in the   system path."),

	ERROR_CTX_WD_NOT_FOUND(0X00001B5C, "The specified terminal connection driver was not found   in the system path."),

	ERROR_CTX_CANNOT_MAKE_EVENTLOG_ENTRY(0X00001B5D, "A registry key for event logging could not be created   for this session."),

	ERROR_CTX_SERVICE_NAME_COLLISION(0X00001B5E, "A service with the same name already exists on the   system."),

	ERROR_CTX_CLOSE_PENDING(0X00001B5F, "A close operation is pending on the session."),

	ERROR_CTX_NO_OUTBUF(0X00001B60, "There are no free output buffers available."),

	ERROR_CTX_MODEM_INF_NOT_FOUND(0X00001B61, "The MODEM.INF file was not found."),

	ERROR_CTX_INVALID_MODEMNAME(0X00001B62, "The modem name was not found in the MODEM.INF file."),

	ERROR_CTX_MODEM_RESPONSE_ERROR(0X00001B63, "The modem did not accept the command sent to it.   Verify that the configured modem name matches the attached modem."),

	ERROR_CTX_MODEM_RESPONSE_TIMEOUT(0X00001B64, "The modem did not respond to the command sent to it.   Verify that the modem is properly cabled and turned on."),

	ERROR_CTX_MODEM_RESPONSE_NO_CARRIER(0X00001B65, "Carrier detect has failed or carrier has been dropped   due to disconnect."),

	ERROR_CTX_MODEM_RESPONSE_NO_DIALTONE(0X00001B66, "Dial tone not detected within the required time.   Verify that the phone cable is properly attached and functional."),

	ERROR_CTX_MODEM_RESPONSE_BUSY(0X00001B67, "Busy signal detected at remote site on callback."),

	ERROR_CTX_MODEM_RESPONSE_VOICE(0X00001B68, "Voice detected at remote site on callback."),

	ERROR_CTX_TD_ERROR(0X00001B69, "Transport driver error."),

	ERROR_CTX_WINSTATION_NOT_FOUND(0X00001B6E, "The specified session cannot be found."),

	ERROR_CTX_WINSTATION_ALREADY_EXISTS(0X00001B6F, "The specified session name is already in use."),

	ERROR_CTX_WINSTATION_BUSY(0X00001B70, "The requested operation cannot be completed because   the terminal connection is currently busy processing a connect, disconnect,   reset, or delete operation."),

	ERROR_CTX_BAD_VIDEO_MODE(0X00001B71, "An attempt has been made to connect to a session whose   video mode is not supported by the current client."),

	ERROR_CTX_GRAPHICS_INVALID(0X00001B7B, "The application attempted to enable DOS graphics mode.   DOS graphics mode is not supported."),

	ERROR_CTX_LOGON_DISABLED(0X00001B7D, "Your interactive logon privilege has been disabled.   Contact your administrator."),

	ERROR_CTX_NOT_CONSOLE(0X00001B7E, "The requested operation can be performed only on the system   console. This is most often the result of a driver or system DLL requiring   direct console access."),

	ERROR_CTX_CLIENT_QUERY_TIMEOUT(0X00001B80, "The client failed to respond to the server connect   message."),

	ERROR_CTX_CONSOLE_DISCONNECT(0X00001B81, "Disconnecting the console session is not supported."),

	ERROR_CTX_CONSOLE_CONNECT(0X00001B82, "Reconnecting a disconnected session to the console is   not supported."),

	ERROR_CTX_SHADOW_DENIED(0X00001B84, "The request to control another session remotely was   denied."),

	ERROR_CTX_WINSTATION_ACCESS_DENIED(0X00001B85, "The requested session access is denied."),

	ERROR_CTX_INVALID_WD(0X00001B89, "The specified terminal connection driver is invalid."),

	ERROR_CTX_SHADOW_INVALID(0X00001B8A, "The requested session cannot be controlled remotely.   This might be because the session is disconnected or does not currently have   a user logged on."),

	ERROR_CTX_SHADOW_DISABLED(0X00001B8B, "The requested session is not configured to allow   remote control."),

	ERROR_CTX_CLIENT_LICENSE_IN_USE(0X00001B8C, "None"),

	ERROR_CTX_CLIENT_LICENSE_NOT_SET(0X00001B8D, "Your request to connect to this terminal server has   been rejected. Your terminal server client license number has not been   entered for this copy of the terminal server client. Contact your system   administrator."),

	ERROR_CTX_LICENSE_NOT_AVAILABLE(0X00001B8E, "The number of connections to this computer is limited   and all connections are in use right now. Try connecting later or contact   your system administrator."),

	ERROR_CTX_LICENSE_CLIENT_INVALID(0X00001B8F, "The client you are using is not licensed to use this   system. Your logon request is denied."),

	ERROR_CTX_LICENSE_EXPIRED(0X00001B90, "The system license has expired. Your logon request is   denied."),

	ERROR_CTX_SHADOW_NOT_RUNNING(0X00001B91, "Remote control could not be terminated because the   specified session is not currently being remotely controlled."),

	ERROR_CTX_SHADOW_ENDED_BY_MODE_CHANGE(0X00001B92, "The remote control of the console was terminated   because the display mode was changed. Changing the display mode in a remote   control session is not supported."),

	ERROR_ACTIVATION_COUNT_EXCEEDED(0X00001B93, "Activation has already been reset the maximum number   of times for this installation. Your activation timer will not be cleared."),

	ERROR_CTX_WINSTATIONS_DISABLED(0X00001B94, "Remote logons are currently disabled."),

	ERROR_CTX_ENCRYPTION_LEVEL_REQUIRED(0X00001B95, "You do not have the proper encryption level to access   this session."),

	ERROR_CTX_SESSION_IN_USE(0X00001B96, "The user %s\\%s is currently logged on to this   computer. Only the current user or an administrator can log on to this   computer."),

	ERROR_CTX_NO_FORCE_LOGOFF(0X00001B97, "The user %s\\%s is already logged on to the console of   this computer. You do not have permission to log in at this time. To resolve   this issue, contact %s\\%s and have them log off."),

	ERROR_CTX_ACCOUNT_RESTRICTION(0X00001B98, "Unable to log you on because of an account   restriction."),

	ERROR_RDP_PROTOCOL_ERROR(0X00001B99, "The RDP component %2 detected an error in the protocol   stream and has disconnected the client."),

	ERROR_CTX_CDM_CONNECT(0X00001B9A, "The Client Drive Mapping Service has connected on   terminal connection."),

	ERROR_CTX_CDM_DISCONNECT(0X00001B9B, "The Client Drive Mapping Service has disconnected on   terminal connection."),

	ERROR_CTX_SECURITY_LAYER_ERROR(0X00001B9C, "The terminal server security layer detected an error   in the protocol stream and has disconnected the client."),

	ERROR_TS_INCOMPATIBLE_SESSIONS(0X00001B9D, "The target session is incompatible with the current   session."),

	FRS_ERR_INVALID_API_SEQUENCE(0X00001F41, "The file replication service API was called   incorrectly."),

	FRS_ERR_STARTING_SERVICE(0X00001F42, "The file replication service cannot be started."),

	FRS_ERR_STOPPING_SERVICE(0X00001F43, "The file replication service cannot be stopped."),

	FRS_ERR_INTERNAL_API(0X00001F44, "The file replication service API terminated the   request. The event log might contain more information."),

	FRS_ERR_INTERNAL(0X00001F45, "The file replication service terminated the request.   The event log might contain more information."),

	FRS_ERR_SERVICE_COMM(0X00001F46, "The file replication service cannot be contacted. The   event log might contain more information."),

	FRS_ERR_INSUFFICIENT_PRIV(0X00001F47, "The file replication service cannot satisfy the request   because the user has insufficient privileges. The event log might contain   more information."),

	FRS_ERR_AUTHENTICATION(0X00001F48, "The file replication service cannot satisfy the   request because authenticated RPC is not available. The event log might contain   more information."),

	FRS_ERR_PARENT_INSUFFICIENT_PRIV(0X00001F49, "The file replication service cannot satisfy the   request because the user has insufficient privileges on the domain   controller. The event log might contain more information."),

	FRS_ERR_PARENT_AUTHENTICATION(0X00001F4A, "The file replication service cannot satisfy the   request because authenticated RPC is not available on the domain controller.   The event log might contain more information."),

	FRS_ERR_CHILD_TO_PARENT_COMM(0X00001F4B, "The file replication service cannot communicate with   the file replication service on the domain controller. The event log might   contain more information."),

	FRS_ERR_PARENT_TO_CHILD_COMM(0X00001F4C, "The file replication service on the domain controller   cannot communicate with the file replication service on this computer. The   event log might contain more information."),

	FRS_ERR_SYSVOL_POPULATE(0X00001F4D, "The file replication service cannot populate the   system volume because of an internal error. The event log might contain more   information."),

	FRS_ERR_SYSVOL_POPULATE_TIMEOUT(0X00001F4E, "The file replication service cannot populate the   system volume because of an internal time-out. The event log might contain   more information."),

	FRS_ERR_SYSVOL_IS_BUSY(0X00001F4F, "The file replication service cannot process the   request. The system volume is busy with a previous request."),

	FRS_ERR_SYSVOL_DEMOTE(0X00001F50, "The file replication service cannot stop replicating   the system volume because of an internal error. The event log might contain   more information."),

	FRS_ERR_INVALID_SERVICE_PARAMETER(0X00001F51, "The file replication service detected an invalid   parameter."),

	ERROR_DS_NOT_INSTALLED(0X00002008, "An error occurred while installing the directory   service. For more information, see the event log."),

	ERROR_DS_MEMBERSHIP_EVALUATED_LOCALLY(0X00002009, "The directory service evaluated group memberships   locally."),

	ERROR_DS_NO_ATTRIBUTE_OR_VALUE(0X0000200A, "The specified directory service attribute or value   does not exist."),

	ERROR_DS_INVALID_ATTRIBUTE_SYNTAX(0X0000200B, "The attribute syntax specified to the directory   service is invalid."),

	ERROR_DS_ATTRIBUTE_TYPE_UNDEFINED(0X0000200C, "The attribute type specified to the directory service   is not defined."),

	ERROR_DS_ATTRIBUTE_OR_VALUE_EXISTS(0X0000200D, "The specified directory service attribute or value   already exists."),

	ERROR_DS_BUSY(0X0000200E, "The directory service is busy."),

	ERROR_DS_UNAVAILABLE(0X0000200F, "The directory service is unavailable."),

	ERROR_DS_NO_RIDS_ALLOCATED(0X00002010, "The directory service was unable to allocate a   relative identifier."),

	ERROR_DS_NO_MORE_RIDS(0X00002011, "The directory service has exhausted the pool of   relative identifiers."),

	ERROR_DS_INCORRECT_ROLE_OWNER(0X00002012, "The requested operation could not be performed because   the directory service is not the master for that type of operation."),

	ERROR_DS_RIDMGR_INIT_ERROR(0X00002013, "The directory service was unable to initialize the   subsystem that allocates relative identifiers."),

	ERROR_DS_OBJ_CLASS_VIOLATION(0X00002014, "The requested operation did not satisfy one or more   constraints associated with the class of the object."),

	ERROR_DS_CANT_ON_NON_LEAF(0X00002015, "The directory service can perform the requested   operation only on a leaf object."),

	ERROR_DS_CANT_ON_RDN(0X00002016, "The directory service cannot perform the requested   operation on the relative distinguished name (RDN) attribute of an object."),

	ERROR_DS_CANT_MOD_OBJ_CLASS(0X00002017, "The directory service detected an attempt to modify   the object class of an object."),

	ERROR_DS_CROSS_DOM_MOVE_ERROR(0X00002018, "The requested cross-domain move operation could not be   performed."),

	ERROR_DS_GC_NOT_AVAILABLE(0X00002019, "Unable to contact the global catalog (GC) server."),

	ERROR_SHARED_POLICY(0X0000201A, "The policy object is shared and can only be modified   at the root."),

	ERROR_POLICY_OBJECT_NOT_FOUND(0X0000201B, "The policy object does not exist."),

	ERROR_POLICY_ONLY_IN_DS(0X0000201C, "The requested policy information is only in the   directory service."),

	ERROR_PROMOTION_ACTIVE(0X0000201D, "A domain controller promotion is currently active."),

	ERROR_NO_PROMOTION_ACTIVE(0X0000201E, "A domain controller promotion is not currently active."),

	ERROR_DS_OPERATIONS_ERROR(0X00002020, "An operations error occurred."),

	ERROR_DS_PROTOCOL_ERROR(0X00002021, "A protocol error occurred."),

	ERROR_DS_TIMELIMIT_EXCEEDED(0X00002022, "The time limit for this request was exceeded."),

	ERROR_DS_SIZELIMIT_EXCEEDED(0X00002023, "The size limit for this request was exceeded."),

	ERROR_DS_ADMIN_LIMIT_EXCEEDED(0X00002024, "The administrative limit for this request was   exceeded."),

	ERROR_DS_COMPARE_FALSE(0X00002025, "The compare response was false."),

	ERROR_DS_COMPARE_TRUE(0X00002026, "The compare response was true."),

	ERROR_DS_AUTH_METHOD_NOT_SUPPORTED(0X00002027, "The requested authentication method is not supported   by the server."),

	ERROR_DS_STRONG_AUTH_REQUIRED(0X00002028, "A more secure authentication method is required for   this server."),

	ERROR_DS_INAPPROPRIATE_AUTH(0X00002029, "Inappropriate authentication."),

	ERROR_DS_AUTH_UNKNOWN(0X0000202A, "The authentication mechanism is unknown."),

	ERROR_DS_REFERRAL(0X0000202B, "A referral was returned from the server."),

	ERROR_DS_UNAVAILABLE_CRIT_EXTENSION(0X0000202C, "The server does not support the requested critical   extension."),

	ERROR_DS_CONFIDENTIALITY_REQUIRED(0X0000202D, "This request requires a secure connection."),

	ERROR_DS_INAPPROPRIATE_MATCHING(0X0000202E, "Inappropriate matching."),

	ERROR_DS_CONSTRAINT_VIOLATION(0X0000202F, "A constraint violation occurred."),

	ERROR_DS_NO_SUCH_OBJECT(0X00002030, "There is no such object on the server."),

	ERROR_DS_ALIAS_PROBLEM(0X00002031, "There is an alias problem."),

	ERROR_DS_INVALID_DN_SYNTAX(0X00002032, "An invalid dn syntax has been specified."),

	ERROR_DS_IS_LEAF(0X00002033, "The object is a leaf object."),

	ERROR_DS_ALIAS_DEREF_PROBLEM(0X00002034, "There is an alias dereferencing problem."),

	ERROR_DS_UNWILLING_TO_PERFORM(0X00002035, "The server is unwilling to process the request."),

	ERROR_DS_LOOP_DETECT(0X00002036, "A loop has been detected."),

	ERROR_DS_NAMING_VIOLATION(0X00002037, "There is a naming violation."),

	ERROR_DS_OBJECT_RESULTS_TOO_LARGE(0X00002038, "The result set is too large."),

	ERROR_DS_AFFECTS_MULTIPLE_DSAS(0X00002039, "The operation affects multiple DSAs."),

	ERROR_DS_SERVER_DOWN(0X0000203A, "The server is not operational."),

	ERROR_DS_LOCAL_ERROR(0X0000203B, "A local error has occurred."),

	ERROR_DS_ENCODING_ERROR(0X0000203C, "An encoding error has occurred."),

	ERROR_DS_DECODING_ERROR(0X0000203D, "A decoding error has occurred."),

	ERROR_DS_FILTER_UNKNOWN(0X0000203E, "The search filter cannot be recognized."),

	ERROR_DS_PARAM_ERROR(0X0000203F, "One or more parameters are illegal."),

	ERROR_DS_NOT_SUPPORTED(0X00002040, "The specified method is not supported."),

	ERROR_DS_NO_RESULTS_RETURNED(0X00002041, "No results were returned."),

	ERROR_DS_CONTROL_NOT_FOUND(0X00002042, "The specified control is not supported by the server."),

	ERROR_DS_CLIENT_LOOP(0X00002043, "A referral loop was detected by the client."),

	ERROR_DS_REFERRAL_LIMIT_EXCEEDED(0X00002044, "The preset referral limit was exceeded."),

	ERROR_DS_SORT_CONTROL_MISSING(0X00002045, "The search requires a SORT control."),

	ERROR_DS_OFFSET_RANGE_ERROR(0X00002046, "The search results exceed the offset range specified."),

	ERROR_DS_ROOT_MUST_BE_NC(0X0000206D, "The root object must be the head of a naming context.   The root object cannot have an instantiated parent."),

	ERROR_DS_ADD_REPLICA_INHIBITED(0X0000206E, "The add replica operation cannot be performed. The   naming context must be writable to create the replica."),

	ERROR_DS_ATT_NOT_DEF_IN_SCHEMA(0X0000206F, "A reference to an attribute that is not defined in the   schema occurred."),

	ERROR_DS_MAX_OBJ_SIZE_EXCEEDED(0X00002070, "The maximum size of an object has been exceeded."),

	ERROR_DS_OBJ_STRING_NAME_EXISTS(0X00002071, "An attempt was made to add an object to the directory   with a name that is already in use."),

	ERROR_DS_NO_RDN_DEFINED_IN_SCHEMA(0X00002072, "An attempt was made to add an object of a class that   does not have an RDN defined in the schema."),

	ERROR_DS_RDN_DOESNT_MATCH_SCHEMA(0X00002073, "An attempt was made to add an object using an RDN that   is not the RDN defined in the schema."),

	ERROR_DS_NO_REQUESTED_ATTS_FOUND(0X00002074, "None of the requested attributes were found on the   objects."),

	ERROR_DS_USER_BUFFER_TO_SMALL(0X00002075, "The user buffer is too small."),

	ERROR_DS_ATT_IS_NOT_ON_OBJ(0X00002076, "The attribute specified in the operation is not   present on the object."),

	ERROR_DS_ILLEGAL_MOD_OPERATION(0X00002077, "Illegal modify operation. Some aspect of the   modification is not permitted."),

	ERROR_DS_OBJ_TOO_LARGE(0X00002078, "The specified object is too large."),

	ERROR_DS_BAD_INSTANCE_TYPE(0X00002079, "The specified instance type is not valid."),

	ERROR_DS_MASTERDSA_REQUIRED(0X0000207A, "The operation must be performed at a master DSA."),

	ERROR_DS_OBJECT_CLASS_REQUIRED(0X0000207B, "The object class attribute must be specified."),

	ERROR_DS_MISSING_REQUIRED_ATT(0X0000207C, "A required attribute is missing."),

	ERROR_DS_ATT_NOT_DEF_FOR_CLASS(0X0000207D, "An attempt was made to modify an object to include an   attribute that is not legal for its class."),

	ERROR_DS_ATT_ALREADY_EXISTS(0X0000207E, "The specified attribute is already present on the   object."),

	ERROR_DS_CANT_ADD_ATT_VALUES(0X00002080, "The specified attribute is not present, or has no   values."),

	ERROR_DS_SINGLE_VALUE_CONSTRAINT(0X00002081, "Multiple values were specified for an attribute that   can have only one value."),

	ERROR_DS_RANGE_CONSTRAINT(0X00002082, "A value for the attribute was not in the acceptable   range of values."),

	ERROR_DS_ATT_VAL_ALREADY_EXISTS(0X00002083, "The specified value already exists."),

	ERROR_DS_CANT_REM_MISSING_ATT(0X00002084, "The attribute cannot be removed because it is not   present on the object."),

	ERROR_DS_CANT_REM_MISSING_ATT_VAL(0X00002085, "The attribute value cannot be removed because it is   not present on the object."),

	ERROR_DS_ROOT_CANT_BE_SUBREF(0X00002086, "The specified root object cannot be a subreference."),

	ERROR_DS_NO_CHAINING(0X00002087, "Chaining is not permitted."),

	ERROR_DS_NO_CHAINED_EVAL(0X00002088, "Chained evaluation is not permitted."),

	ERROR_DS_NO_PARENT_OBJECT(0X00002089, "The operation could not be performed because the   object's parent is either uninstantiated or deleted."),

	ERROR_DS_PARENT_IS_AN_ALIAS(0X0000208A, "Having a parent that is an alias is not permitted.   Aliases are leaf objects."),

	ERROR_DS_CANT_MIX_MASTER_AND_REPS(0X0000208B, "The object and parent must be of the same type, either   both masters or both replicas."),

	ERROR_DS_CHILDREN_EXIST(0X0000208C, "The operation cannot be performed because child   objects exist. This operation can only be performed on a leaf object."),

	ERROR_DS_OBJ_NOT_FOUND(0X0000208D, "Directory object not found."),

	ERROR_DS_ALIASED_OBJ_MISSING(0X0000208E, "The aliased object is missing."),

	ERROR_DS_BAD_NAME_SYNTAX(0X0000208F, "The object name has bad syntax."),

	ERROR_DS_ALIAS_POINTS_TO_ALIAS(0X00002090, "An alias is not permitted to refer to another alias."),

	ERROR_DS_CANT_DEREF_ALIAS(0X00002091, "The alias cannot be dereferenced."),

	ERROR_DS_OUT_OF_SCOPE(0X00002092, "The operation is out of scope."),

	ERROR_DS_OBJECT_BEING_REMOVED(0X00002093, "The operation cannot continue because the object is in   the process of being removed."),

	ERROR_DS_CANT_DELETE_DSA_OBJ(0X00002094, "The DSA object cannot be deleted."),

	ERROR_DS_GENERIC_ERROR(0X00002095, "A directory service error has occurred."),

	ERROR_DS_DSA_MUST_BE_INT_MASTER(0X00002096, "The operation can only be performed on an internal   master DSA object."),

	ERROR_DS_CLASS_NOT_DSA(0X00002097, "The object must be of class DSA."),

	ERROR_DS_INSUFF_ACCESS_RIGHTS(0X00002098, "Insufficient access rights to perform the operation."),

	ERROR_DS_ILLEGAL_SUPERIOR(0X00002099, "The object cannot be added because the parent is not   on the list of possible superiors."),

	ERROR_DS_ATTRIBUTE_OWNED_BY_SAM(0X0000209A, "Access to the attribute is not permitted because the   attribute is owned by the SAM."),

	ERROR_DS_NAME_TOO_MANY_PARTS(0X0000209B, "The name has too many parts."),

	ERROR_DS_NAME_TOO_LONG(0X0000209C, "The name is too long."),

	ERROR_DS_NAME_VALUE_TOO_LONG(0X0000209D, "The name value is too long."),

	ERROR_DS_NAME_UNPARSEABLE(0X0000209E, "The directory service encountered an error parsing a   name."),

	ERROR_DS_NAME_TYPE_UNKNOWN(0X0000209F, "The directory service cannot get the attribute type   for a name."),

	ERROR_DS_NOT_AN_OBJECT(0X000020A0, "The name does not identify an object; the name   identifies a phantom."),

	ERROR_DS_SEC_DESC_TOO_SHORT(0X000020A1, "The security descriptor is too short."),

	ERROR_DS_SEC_DESC_INVALID(0X000020A2, "The security descriptor is invalid."),

	ERROR_DS_NO_DELETED_NAME(0X000020A3, "Failed to create name for deleted object."),

	ERROR_DS_SUBREF_MUST_HAVE_PARENT(0X000020A4, "The parent of a new subreference must exist."),

	ERROR_DS_NCNAME_MUST_BE_NC(0X000020A5, "The object must be a naming context."),

	ERROR_DS_CANT_ADD_SYSTEM_ONLY(0X000020A6, "It is not permitted to add an attribute that is owned   by the system."),

	ERROR_DS_CLASS_MUST_BE_CONCRETE(0X000020A7, "The class of the object must be structural; you cannot   instantiate an abstract class."),

	ERROR_DS_INVALID_DMD(0X000020A8, "The schema object could not be found."),

	ERROR_DS_OBJ_GUID_EXISTS(0X000020A9, "A local object with this GUID (dead or alive) already   exists."),

	ERROR_DS_NOT_ON_BACKLINK(0X000020AA, "The operation cannot be performed on a back link."),

	ERROR_DS_NO_CROSSREF_FOR_NC(0X000020AB, "The cross-reference for the specified naming context   could not be found."),

	ERROR_DS_SHUTTING_DOWN(0X000020AC, "The operation could not be performed because the   directory service is shutting down."),

	ERROR_DS_UNKNOWN_OPERATION(0X000020AD, "The directory service request is invalid."),

	ERROR_DS_INVALID_ROLE_OWNER(0X000020AE, "The role owner attribute could not be read."),

	ERROR_DS_COULDNT_CONTACT_FSMO(0X000020AF, "The requested Flexible Single Master Operations (FSMO)   operation failed. The current FSMO holder could not be contacted."),

	ERROR_DS_CROSS_NC_DN_RENAME(0X000020B0, "Modification of a distinguished name across a naming   context is not permitted."),

	ERROR_DS_CANT_MOD_SYSTEM_ONLY(0X000020B1, "The attribute cannot be modified because it is owned   by the system."),

	ERROR_DS_REPLICATOR_ONLY(0X000020B2, "Only the replicator can perform this function."),

	ERROR_DS_OBJ_CLASS_NOT_DEFINED(0X000020B3, "The specified class is not defined."),

	ERROR_DS_OBJ_CLASS_NOT_SUBCLASS(0X000020B4, "The specified class is not a subclass."),

	ERROR_DS_NAME_REFERENCE_INVALID(0X000020B5, "The name reference is invalid."),

	ERROR_DS_CROSS_REF_EXISTS(0X000020B6, "A cross-reference already exists."),

	ERROR_DS_CANT_DEL_MASTER_CROSSREF(0X000020B7, "It is not permitted to delete a master   cross-reference."),

	ERROR_DS_SUBTREE_NOTIFY_NOT_NC_HEAD(0X000020B8, "Subtree notifications are only supported on naming   context (NC) heads."),

	ERROR_DS_NOTIFY_FILTER_TOO_COMPLEX(0X000020B9, "Notification filter is too complex."),

	ERROR_DS_DUP_RDN(0X000020BA, "Schema update failed: Duplicate RDN."),

	ERROR_DS_DUP_OID(0X000020BB, "Schema update failed: Duplicate OID."),

	ERROR_DS_DUP_MAPI_ID(0X000020BC, "Schema update failed: Duplicate Message Application Programming   Interface (MAPI) identifier."),

	ERROR_DS_DUP_SCHEMA_ID_GUID(0X000020BD, "Schema update failed: Duplicate schema ID GUID."),

	ERROR_DS_DUP_LDAP_DISPLAY_NAME(0X000020BE, "Schema update failed: Duplicate LDAP display name."),

	ERROR_DS_SEMANTIC_ATT_TEST(0X000020BF, "Schema update failed: Range-Lower less than   Range-Upper."),

	ERROR_DS_SYNTAX_MISMATCH(0X000020C0, "Schema update failed: Syntax mismatch."),

	ERROR_DS_EXISTS_IN_MUST_HAVE(0X000020C1, "Schema deletion failed: Attribute is used in the   Must-Contain list."),

	ERROR_DS_EXISTS_IN_MAY_HAVE(0X000020C2, "Schema deletion failed: Attribute is used in the   May-Contain list."),

	ERROR_DS_NONEXISTENT_MAY_HAVE(0X000020C3, "Schema update failed: Attribute in May-Contain list   does not exist."),

	ERROR_DS_NONEXISTENT_MUST_HAVE(0X000020C4, "Schema update failed: Attribute in the Must-Contain   list does not exist."),

	ERROR_DS_AUX_CLS_TEST_FAIL(0X000020C5, "Schema update failed: Class in the Aux Class list does   not exist or is not an auxiliary class."),

	ERROR_DS_NONEXISTENT_POSS_SUP(0X000020C6, "Schema update failed: Class in the Poss-Superiors list   does not exist."),

	ERROR_DS_SUB_CLS_TEST_FAIL(0X000020C7, "Schema update failed: Class in the subclass of the   list does not exist or does not satisfy hierarchy rules."),

	ERROR_DS_BAD_RDN_ATT_ID_SYNTAX(0X000020C8, "Schema update failed: Rdn-Att-Id has wrong syntax."),

	ERROR_DS_EXISTS_IN_AUX_CLS(0X000020C9, "Schema deletion failed: Class is used as an auxiliary   class."),

	ERROR_DS_EXISTS_IN_SUB_CLS(0X000020CA, "Schema deletion failed: Class is used as a subclass."),

	ERROR_DS_EXISTS_IN_POSS_SUP(0X000020CB, "Schema deletion failed: Class is used as a   Poss-Superior."),

	ERROR_DS_RECALCSCHEMA_FAILED(0X000020CC, "Schema update failed in recalculating validation   cache."),

	ERROR_DS_TREE_DELETE_NOT_FINISHED(0X000020CD, "The tree deletion is not finished. The request must be   made again to continue deleting the tree."),

	ERROR_DS_CANT_DELETE(0X000020CE, "The requested delete operation could not be performed."),

	ERROR_DS_ATT_SCHEMA_REQ_ID(0X000020CF, "Cannot read the governs class identifier for the   schema record."),

	ERROR_DS_BAD_ATT_SCHEMA_SYNTAX(0X000020D0, "The attribute schema has bad syntax."),

	ERROR_DS_CANT_CACHE_ATT(0X000020D1, "The attribute could not be cached."),

	ERROR_DS_CANT_CACHE_CLASS(0X000020D2, "The class could not be cached."),

	ERROR_DS_CANT_REMOVE_ATT_CACHE(0X000020D3, "The attribute could not be removed from the cache."),

	ERROR_DS_CANT_REMOVE_CLASS_CACHE(0X000020D4, "The class could not be removed from the cache."),

	ERROR_DS_CANT_RETRIEVE_DN(0X000020D5, "The distinguished name attribute could not be read."),

	ERROR_DS_MISSING_SUPREF(0X000020D6, "No superior reference has been configured for the   directory service. The directory service is, therefore, unable to issue   referrals to objects outside this forest."),

	ERROR_DS_CANT_RETRIEVE_INSTANCE(0X000020D7, "The instance type attribute could not be retrieved."),

	ERROR_DS_CODE_INCONSISTENCY(0X000020D8, "An internal error has occurred."),

	ERROR_DS_DATABASE_ERROR(0X000020D9, "A database error has occurred."),

	ERROR_DS_GOVERNSID_MISSING(0X000020DA, "None"),

	ERROR_DS_MISSING_EXPECTED_ATT(0X000020DB, "An expected attribute is missing."),

	ERROR_DS_NCNAME_MISSING_CR_REF(0X000020DC, "The specified naming context is missing a   cross-reference."),

	ERROR_DS_SECURITY_CHECKING_ERROR(0X000020DD, "A security checking error has occurred."),

	ERROR_DS_SCHEMA_NOT_LOADED(0X000020DE, "The schema is not loaded."),

	ERROR_DS_SCHEMA_ALLOC_FAILED(0X000020DF, "Schema allocation failed. Check if the machine is   running low on memory."),

	ERROR_DS_ATT_SCHEMA_REQ_SYNTAX(0X000020E0, "Failed to obtain the required syntax for the attribute   schema."),

	ERROR_DS_GCVERIFY_ERROR(0X000020E1, "The GC verification failed. The GC is not available or   does not support the operation. Some part of the directory is currently not   available."),

	ERROR_DS_DRA_SCHEMA_MISMATCH(0X000020E2, "The replication operation failed because of a schema   mismatch between the servers involved."),

	ERROR_DS_CANT_FIND_DSA_OBJ(0X000020E3, "The DSA object could not be found."),

	ERROR_DS_CANT_FIND_EXPECTED_NC(0X000020E4, "The naming context could not be found."),

	ERROR_DS_CANT_FIND_NC_IN_CACHE(0X000020E5, "The naming context could not be found in the cache."),

	ERROR_DS_CANT_RETRIEVE_CHILD(0X000020E6, "The child object could not be retrieved."),

	ERROR_DS_SECURITY_ILLEGAL_MODIFY(0X000020E7, "The modification was not permitted for security   reasons."),

	ERROR_DS_CANT_REPLACE_HIDDEN_REC(0X000020E8, "The operation cannot replace the hidden record."),

	ERROR_DS_BAD_HIERARCHY_FILE(0X000020E9, "The hierarchy file is invalid."),

	ERROR_DS_BUILD_HIERARCHY_TABLE_FAILED(0X000020EA, "The attempt to build the hierarchy table failed."),

	ERROR_DS_CONFIG_PARAM_MISSING(0X000020EB, "The directory configuration parameter is missing from   the registry."),

	ERROR_DS_COUNTING_AB_INDICES_FAILED(0X000020EC, "The attempt to count the address book indices failed."),

	ERROR_DS_HIERARCHY_TABLE_MALLOC_FAILED(0X000020ED, "The allocation of the hierarchy table failed."),

	ERROR_DS_INTERNAL_FAILURE(0X000020EE, "The directory service encountered an internal failure."),

	ERROR_DS_UNKNOWN_ERROR(0X000020EF, "The directory service encountered an unknown failure."),

	ERROR_DS_ROOT_REQUIRES_CLASS_TOP(0X000020F0, "A root object requires a class of top."),

	ERROR_DS_REFUSING_FSMO_ROLES(0X000020F1, "This directory server is shutting down, and cannot   take ownership of new floating single-master operation roles."),

	ERROR_DS_MISSING_FSMO_SETTINGS(0X000020F2, "The directory service is missing mandatory   configuration information and is unable to determine the ownership of   floating single-master operation roles."),

	ERROR_DS_UNABLE_TO_SURRENDER_ROLES(0X000020F3, "The directory service was unable to transfer ownership   of one or more floating single-master operation roles to other servers."),

	ERROR_DS_DRA_GENERIC(0X000020F4, "The replication operation failed."),

	ERROR_DS_DRA_INVALID_PARAMETER(0X000020F5, "An invalid parameter was specified for this   replication operation."),

	ERROR_DS_DRA_BUSY(0X000020F6, "The directory service is too busy to complete the   replication operation at this time."),

	ERROR_DS_DRA_BAD_DN(0X000020F7, "The DN specified for this replication operation is   invalid."),

	ERROR_DS_DRA_BAD_NC(0X000020F8, "The naming context specified for this replication   operation is invalid."),

	ERROR_DS_DRA_DN_EXISTS(0X000020F9, "The DN specified for this replication operation   already exists."),

	ERROR_DS_DRA_INTERNAL_ERROR(0X000020FA, "The replication system encountered an internal error."),

	ERROR_DS_DRA_INCONSISTENT_DIT(0X000020FB, "The replication operation encountered a database   inconsistency."),

	ERROR_DS_DRA_CONNECTION_FAILED(0X000020FC, "The server specified for this replication operation   could not be contacted."),

	ERROR_DS_DRA_BAD_INSTANCE_TYPE(0X000020FD, "The replication operation encountered an object with   an invalid instance type."),

	ERROR_DS_DRA_OUT_OF_MEM(0X000020FE, "The replication operation failed to allocate memory."),

	ERROR_DS_DRA_MAIL_PROBLEM(0X000020FF, "The replication operation encountered an error with   the mail system."),

	ERROR_DS_DRA_REF_ALREADY_EXISTS(0X00002100, "The replication reference information for the target   server already exists."),

	ERROR_DS_DRA_REF_NOT_FOUND(0X00002101, "The replication reference information for the target   server does not exist."),

	ERROR_DS_DRA_OBJ_IS_REP_SOURCE(0X00002102, "The naming context cannot be removed because it is   replicated to another server."),

	ERROR_DS_DRA_DB_ERROR(0X00002103, "The replication operation encountered a database error."),

	ERROR_DS_DRA_NO_REPLICA(0X00002104, "The naming context is in the process of being removed   or is not replicated from the specified server."),

	ERROR_DS_DRA_ACCESS_DENIED(0X00002105, "Replication access was denied."),

	ERROR_DS_DRA_NOT_SUPPORTED(0X00002106, "The requested operation is not supported by this   version of the directory service."),

	ERROR_DS_DRA_RPC_CANCELLED(0X00002107, "The replication RPC was canceled."),

	ERROR_DS_DRA_SOURCE_DISABLED(0X00002108, "The source server is currently rejecting replication   requests."),

	ERROR_DS_DRA_SINK_DISABLED(0X00002109, "The destination server is currently rejecting   replication requests."),

	ERROR_DS_DRA_NAME_COLLISION(0X0000210A, "The replication operation failed due to a collision of   object names."),

	ERROR_DS_DRA_SOURCE_REINSTALLED(0X0000210B, "The replication source has been reinstalled."),

	ERROR_DS_DRA_MISSING_PARENT(0X0000210C, "The replication operation failed because a required   parent object is missing."),

	ERROR_DS_DRA_PREEMPTED(0X0000210D, "The replication operation was preempted."),

	ERROR_DS_DRA_ABANDON_SYNC(0X0000210E, "The replication synchronization attempt was abandoned   because of a lack of updates."),

	ERROR_DS_DRA_SHUTDOWN(0X0000210F, "The replication operation was terminated because the   system is shutting down."),

	ERROR_DS_DRA_INCOMPATIBLE_PARTIAL_SET(0X00002110, "A synchronization attempt failed because the   destination DC is currently waiting to synchronize new partial attributes   from the source. This condition is normal if a recent schema change modified   the partial attribute set. The destination partial attribute set is not a   subset of the source partial attribute set."),

	ERROR_DS_DRA_SOURCE_IS_PARTIAL_REPLICA(0X00002111, "The replication synchronization attempt failed because   a master replica attempted to sync from a partial replica."),

	ERROR_DS_DRA_EXTN_CONNECTION_FAILED(0X00002112, "The server specified for this replication operation   was contacted, but that server was unable to contact an additional server   needed to complete the operation."),

	ERROR_DS_INSTALL_SCHEMA_MISMATCH(0X00002113, "The version of the directory service schema of the   source forest is not compatible with the version of the directory service on   this computer."),

	ERROR_DS_DUP_LINK_ID(0X00002114, "Schema update failed: An attribute with the same link   identifier already exists."),

	ERROR_DS_NAME_ERROR_RESOLVING(0X00002115, "Name translation: Generic processing error."),

	ERROR_DS_NAME_ERROR_NOT_FOUND(0X00002116, "Name translation: Could not find the name or   insufficient right to see name."),

	ERROR_DS_NAME_ERROR_NOT_UNIQUE(0X00002117, "Name translation: Input name mapped to more than one   output name."),

	ERROR_DS_NAME_ERROR_NO_MAPPING(0X00002118, "Name translation: The input name was found but not the   associated output format."),

	ERROR_DS_NAME_ERROR_DOMAIN_ONLY(0X00002119, "Name translation: Unable to resolve completely, only   the domain was found."),

	ERROR_DS_NAME_ERROR_NO_SYNTACTICAL_MAPPING(0X0000211A, "Name translation: Unable to perform purely syntactical   mapping at the client without going out to the wire."),

	ERROR_DS_CONSTRUCTED_ATT_MOD(0X0000211B, "Modification of a constructed attribute is not   allowed."),

	ERROR_DS_WRONG_OM_OBJ_CLASS(0X0000211C, "The OM-Object-Class specified is incorrect for an   attribute with the specified syntax."),

	ERROR_DS_DRA_REPL_PENDING(0X0000211D, "The replication request has been posted; waiting for a   reply."),

	ERROR_DS_DS_REQUIRED(0X0000211E, "The requested operation requires a directory service,   and none was available."),

	ERROR_DS_INVALID_LDAP_DISPLAY_NAME(0X0000211F, "The LDAP display name of the class or attribute   contains non-ASCII characters."),

	ERROR_DS_NON_BASE_SEARCH(0X00002120, "The requested search operation is only supported for   base searches."),

	ERROR_DS_CANT_RETRIEVE_ATTS(0X00002121, "The search failed to retrieve attributes from the   database."),

	ERROR_DS_BACKLINK_WITHOUT_LINK(0X00002122, "The schema update operation tried to add a backward   link attribute that has no corresponding forward link."),

	ERROR_DS_EPOCH_MISMATCH(0X00002123, "The source and destination of a cross-domain move do   not agree on the object's epoch number. Either the source or the destination   does not have the latest version of the object."),

	ERROR_DS_SRC_NAME_MISMATCH(0X00002124, "The source and destination of a cross-domain move do   not agree on the object's current name. Either the source or the destination   does not have the latest version of the object."),

	ERROR_DS_SRC_AND_DST_NC_IDENTICAL(0X00002125, "The source and destination for the cross-domain move   operation are identical. The caller should use a local move operation instead   of a cross-domain move operation."),

	ERROR_DS_DST_NC_MISMATCH(0X00002126, "The source and destination for a cross-domain move do   not agree on the naming contexts in the forest. Either the source or the   destination does not have the latest version of the Partitions container."),

	ERROR_DS_NOT_AUTHORITIVE_FOR_DST_NC(0X00002127, "The destination of a cross-domain move is not   authoritative for the destination naming context."),

	ERROR_DS_SRC_GUID_MISMATCH(0X00002128, "The source and destination of a cross-domain move do   not agree on the identity of the source object. Either the source or the   destination does not have the latest version of the source object."),

	ERROR_DS_CANT_MOVE_DELETED_OBJECT(0X00002129, "The object being moved across domains is already known   to be deleted by the destination server. The source server does not have the   latest version of the source object."),

	ERROR_DS_PDC_OPERATION_IN_PROGRESS(0X0000212A, "Another operation that requires exclusive access to   the PDC FSMO is already in progress."),

	ERROR_DS_CROSS_DOMAIN_CLEANUP_REQD(0X0000212B, "A cross-domain move operation failed because two   versions of the moved object exist—one each in the source and destination   domains. The destination object needs to be removed to restore the system to   a consistent state."),

	ERROR_DS_ILLEGAL_XDOM_MOVE_OPERATION(0X0000212C, "This object cannot be moved across domain boundaries   either because cross-domain moves for this class are not allowed, or the   object has some special characteristics, for example, a trust account or a   restricted relative identifier (RID), that prevent its move."),

	ERROR_DS_CANT_WITH_ACCT_GROUP_MEMBERSHPS(0X0000212D, "Cannot move objects with memberships across domain   boundaries because, once moved, this violates the membership conditions of   the account group. Remove the object from any account group memberships and   retry."),

	ERROR_DS_NC_MUST_HAVE_NC_PARENT(0X0000212E, "A naming context head must be the immediate child of   another naming context head, not of an interior node."),

	ERROR_DS_CR_IMPOSSIBLE_TO_VALIDATE(0X0000212F, "The directory cannot validate the proposed naming   context name because it does not hold a replica of the naming context above   the proposed naming context. Ensure that the domain naming master role is   held by a server that is configured as a GC server, and that the server is   up-to-date with its replication partners. (Applies only to Windows 2000   operating system domain naming masters.)"),

	ERROR_DS_DST_DOMAIN_NOT_NATIVE(0X00002130, "Destination domain must be in native mode."),

	ERROR_DS_MISSING_INFRASTRUCTURE_CONTAINER(0X00002131, "The operation cannot be performed because the server   does not have an infrastructure container in the domain of interest."),

	ERROR_DS_CANT_MOVE_ACCOUNT_GROUP(0X00002132, "Cross-domain moves of nonempty account groups is not   allowed."),

	ERROR_DS_CANT_MOVE_RESOURCE_GROUP(0X00002133, "Cross-domain moves of nonempty resource groups is not   allowed."),

	ERROR_DS_INVALID_SEARCH_FLAG(0X00002134, "The search flags for the attribute are invalid. The   ambiguous name resolution (ANR) bit is valid only on attributes of Unicode or   Teletex strings."),

	ERROR_DS_NO_TREE_DELETE_ABOVE_NC(0X00002135, "Tree deletions starting at an object that has an NC   head as a descendant are not allowed."),

	ERROR_DS_COULDNT_LOCK_TREE_FOR_DELETE(0X00002136, "The directory service failed to lock a tree in   preparation for a tree deletion because the tree was in use."),

	ERROR_DS_COULDNT_IDENTIFY_OBJECTS_FOR_TREE_DELETE(0X00002137, "The directory service failed to identify the list of   objects to delete while attempting a tree deletion."),

	ERROR_DS_SAM_INIT_FAILURE(0X00002138, "SAM initialization failed because of the following   error: %1. Error Status: 0x%2. Click OK to shut down the system and reboot   into Directory Services Restore Mode. Check the event log for detailed   information."),

	ERROR_DS_SENSITIVE_GROUP_VIOLATION(0X00002139, "Only an administrator can modify the membership list   of an administrative group."),

	ERROR_DS_CANT_MOD_PRIMARYGROUPID(0X0000213A, "Cannot change the primary group ID of a domain   controller account."),

	ERROR_DS_ILLEGAL_BASE_SCHEMA_MOD(0X0000213B, "An attempt was made to modify the base schema."),

	ERROR_DS_NONSAFE_SCHEMA_CHANGE(0X0000213C, "Adding a new mandatory attribute to an existing class,   deleting a mandatory attribute from an existing class, or adding an optional   attribute to the special class Top that is not a backlink attribute (directly   or through inheritance, for example, by adding or deleting an auxiliary   class) is not allowed."),

	ERROR_DS_SCHEMA_UPDATE_DISALLOWED(0X0000213D, "Schema update is not allowed on this DC because the DC   is not the schema FSMO role owner."),

	ERROR_DS_CANT_CREATE_UNDER_SCHEMA(0X0000213E, "An object of this class cannot be created under the   schema container. You can only create Attribute-Schema and Class-Schema   objects under the schema container."),

	ERROR_DS_INSTALL_NO_SRC_SCH_VERSION(0X0000213F, "None"),

	ERROR_DS_INSTALL_NO_SCH_VERSION_IN_INIFILE(0X00002140, "None"),

	ERROR_DS_INVALID_GROUP_TYPE(0X00002141, "The specified group type is invalid."),

	ERROR_DS_NO_NEST_GLOBALGROUP_IN_MIXEDDOMAIN(0X00002142, "You cannot nest global groups in a mixed domain if the   group is security-enabled."),

	ERROR_DS_NO_NEST_LOCALGROUP_IN_MIXEDDOMAIN(0X00002143, "You cannot nest local groups in a mixed domain if the   group is security-enabled."),

	ERROR_DS_GLOBAL_CANT_HAVE_LOCAL_MEMBER(0X00002144, "A global group cannot have a local group as a member."),

	ERROR_DS_GLOBAL_CANT_HAVE_UNIVERSAL_MEMBER(0X00002145, "A global group cannot have a universal group as a   member."),

	ERROR_DS_UNIVERSAL_CANT_HAVE_LOCAL_MEMBER(0X00002146, "A universal group cannot have a local group as a   member."),

	ERROR_DS_GLOBAL_CANT_HAVE_CROSSDOMAIN_MEMBER(0X00002147, "A global group cannot have a cross-domain member."),

	ERROR_DS_LOCAL_CANT_HAVE_CROSSDOMAIN_LOCAL_MEMBER(0X00002148, "A local group cannot have another cross domain local   group as a member."),

	ERROR_DS_HAVE_PRIMARY_MEMBERS(0X00002149, "A group with primary members cannot change to a   security-disabled group."),

	ERROR_DS_STRING_SD_CONVERSION_FAILED(0X0000214A, "The schema cache load failed to convert the string   default security descriptor (SD) on a class-schema object."),

	ERROR_DS_NAMING_MASTER_GC(0X0000214B, "Only DSAs configured to be GC servers should be   allowed to hold the domain naming master FSMO role. (Applies only to Windows   2000 servers.)"),

	ERROR_DS_DNS_LOOKUP_FAILURE(0X0000214C, "The DSA operation is unable to proceed because of a   DNS lookup failure."),

	ERROR_DS_COULDNT_UPDATE_SPNS(0X0000214D, "While processing a change to the DNS host name for an   object, the SPN values could not be kept in sync."),

	ERROR_DS_CANT_RETRIEVE_SD(0X0000214E, "The Security Descriptor attribute could not be read."),

	ERROR_DS_KEY_NOT_UNIQUE(0X0000214F, "The object requested was not found, but an object with   that key was found."),

	ERROR_DS_WRONG_LINKED_ATT_SYNTAX(0X00002150, "The syntax of the linked attribute being added is   incorrect. Forward links can only have syntax 2.5.5.1, 2.5.5.7, and 2.5.5.14,   and backlinks can only have syntax 2.5.5.1."),

	ERROR_DS_SAM_NEED_BOOTKEY_PASSWORD(0X00002151, "SAM needs to get the boot password."),

	ERROR_DS_SAM_NEED_BOOTKEY_FLOPPY(0X00002152, "SAM needs to get the boot key from the floppy disk."),

	ERROR_DS_CANT_START(0X00002153, "Directory Service cannot start."),

	ERROR_DS_INIT_FAILURE(0X00002154, "Directory Services could not start."),

	ERROR_DS_NO_PKT_PRIVACY_ON_CONNECTION(0X00002155, "The connection between client and server requires   packet privacy or better."),

	ERROR_DS_SOURCE_DOMAIN_IN_FOREST(0X00002156, "The source domain cannot be in the same forest as the   destination."),

	ERROR_DS_DESTINATION_DOMAIN_NOT_IN_FOREST(0X00002157, "The destination domain MUST be in the forest."),

	ERROR_DS_DESTINATION_AUDITING_NOT_ENABLED(0X00002158, "The operation requires that destination domain   auditing be enabled."),

	ERROR_DS_CANT_FIND_DC_FOR_SRC_DOMAIN(0X00002159, "The operation could not locate a DC for the source   domain."),

	ERROR_DS_SRC_OBJ_NOT_GROUP_OR_USER(0X0000215A, "The source object must be a group or user."),

	ERROR_DS_SRC_SID_EXISTS_IN_FOREST(0X0000215B, "The source object's SID already exists in the   destination forest."),

	ERROR_DS_SRC_AND_DST_OBJECT_CLASS_MISMATCH(0X0000215C, "The source and destination object must be of the same   type."),

	ERROR_SAM_INIT_FAILURE(0X0000215D, "SAM initialization failed because of the following   error: %1. Error Status: 0x%2. Click OK to shut down the system and reboot   into Safe Mode. Check the event log for detailed information."),

	ERROR_DS_DRA_SCHEMA_INFO_SHIP(0X0000215E, "Schema information could not be included in the   replication request."),

	ERROR_DS_DRA_SCHEMA_CONFLICT(0X0000215F, "The replication operation could not be completed due   to a schema incompatibility."),

	ERROR_DS_DRA_EARLIER_SCHEMA_CONFLICT(0X00002160, "The replication operation could not be completed due   to a previous schema incompatibility."),

	ERROR_DS_DRA_OBJ_NC_MISMATCH(0X00002161, "The replication update could not be applied because   either the source or the destination has not yet received information   regarding a recent cross-domain move operation."),

	ERROR_DS_NC_STILL_HAS_DSAS(0X00002162, "The requested domain could not be deleted because   there exist domain controllers that still host this domain."),

	ERROR_DS_GC_REQUIRED(0X00002163, "The requested operation can be performed only on a GC   server."),

	ERROR_DS_LOCAL_MEMBER_OF_LOCAL_ONLY(0X00002164, "A local group can only be a member of other local   groups in the same domain."),

	ERROR_DS_NO_FPO_IN_UNIVERSAL_GROUPS(0X00002165, "Foreign security principals cannot be members of   universal groups."),

	ERROR_DS_CANT_ADD_TO_GC(0X00002166, "The attribute is not allowed to be replicated to the   GC because of security reasons."),

	ERROR_DS_NO_CHECKPOINT_WITH_PDC(0X00002167, "The checkpoint with the PDC could not be taken because   too many modifications are currently being processed."),

	ERROR_DS_SOURCE_AUDITING_NOT_ENABLED(0X00002168, "The operation requires that source domain auditing be   enabled."),

	ERROR_DS_CANT_CREATE_IN_NONDOMAIN_NC(0X00002169, "Security principal objects can only be created inside   domain naming contexts."),

	ERROR_DS_INVALID_NAME_FOR_SPN(0X0000216A, "An SPN could not be constructed because the provided   host name is not in the necessary format."),

	ERROR_DS_FILTER_USES_CONTRUCTED_ATTRS(0X0000216B, "A filter was passed that uses constructed attributes."),

	ERROR_DS_UNICODEPWD_NOT_IN_QUOTES(0X0000216C, "None"),

	ERROR_DS_MACHINE_ACCOUNT_QUOTA_EXCEEDED(0X0000216D, "Your computer could not be joined to the domain. You   have exceeded the maximum number of computer accounts you are allowed to   create in this domain. Contact your system administrator to have this limit   reset or increased."),

	ERROR_DS_MUST_BE_RUN_ON_DST_DC(0X0000216E, "For security reasons, the operation must be run on the   destination DC."),

	ERROR_DS_SRC_DC_MUST_BE_SP4_OR_GREATER(0X0000216F, "For security reasons, the source DC must be NT4SP4 or   greater."),

	ERROR_DS_CANT_TREE_DELETE_CRITICAL_OBJ(0X00002170, "Critical directory service system objects cannot be   deleted during tree deletion operations. The tree deletion might have been   partially performed."),

	ERROR_DS_INIT_FAILURE_CONSOLE(0X00002171, "Directory Services could not start because of the   following error: %1. Error Status: 0x%2. Click OK to shut down the system.   You can use the Recovery Console to further diagnose the system."),

	ERROR_DS_SAM_INIT_FAILURE_CONSOLE(0X00002172, "SAM initialization failed because of the following   error: %1. Error Status: 0x%2. Click OK to shut down the system. You can use   the Recovery Console to further diagnose the system."),

	ERROR_DS_FOREST_VERSION_TOO_HIGH(0X00002173, "The version of the operating system installed is   incompatible with the current forest functional level. You must upgrade to a   new version of the operating system before this server can become a domain   controller in this forest."),

	ERROR_DS_DOMAIN_VERSION_TOO_HIGH(0X00002174, "The version of the operating system installed is   incompatible with the current domain functional level. You must upgrade to a   new version of the operating system before this server can become a domain   controller in this domain."),

	ERROR_DS_FOREST_VERSION_TOO_LOW(0X00002175, "The version of the operating system installed on this   server no longer supports the current forest functional level. You must raise   the forest functional level before this server can become a domain controller   in this forest."),

	ERROR_DS_DOMAIN_VERSION_TOO_LOW(0X00002176, "The version of the operating system installed on this   server no longer supports the current domain functional level. You must raise   the domain functional level before this server can become a domain controller   in this domain."),

	ERROR_DS_INCOMPATIBLE_VERSION(0X00002177, "The version of the operating system installed on this   server is incompatible with the functional level of the domain or forest."),

	ERROR_DS_LOW_DSA_VERSION(0X00002178, "The functional level of the domain (or forest) cannot   be raised to the requested value because one or more domain controllers in   the domain (or forest) are at a lower, incompatible functional level."),

	ERROR_DS_NO_BEHAVIOR_VERSION_IN_MIXEDDOMAIN(0X00002179, "The forest functional level cannot be raised to the   requested value because one or more domains are still in mixed-domain mode.   All domains in the forest must be in native mode for you to raise the forest   functional level."),

	ERROR_DS_NOT_SUPPORTED_SORT_ORDER(0X0000217A, "The sort order requested is not supported."),

	ERROR_DS_NAME_NOT_UNIQUE(0X0000217B, "The requested name already exists as a unique   identifier."),

	ERROR_DS_MACHINE_ACCOUNT_CREATED_PRENT4(0X0000217C, "The machine account was created before Windows NT 4.0.   The account needs to be re-created."),

	ERROR_DS_OUT_OF_VERSION_STORE(0X0000217D, "The database is out of version store."),

	ERROR_DS_INCOMPATIBLE_CONTROLS_USED(0X0000217E, "Unable to continue operation because multiple   conflicting controls were used."),

	ERROR_DS_NO_REF_DOMAIN(0X0000217F, "Unable to find a valid security descriptor reference   domain for this partition."),

	ERROR_DS_RESERVED_LINK_ID(0X00002180, "Schema update failed: The link identifier is reserved."),

	ERROR_DS_LINK_ID_NOT_AVAILABLE(0X00002181, "Schema update failed: There are no link identifiers   available."),

	ERROR_DS_AG_CANT_HAVE_UNIVERSAL_MEMBER(0X00002182, "An account group cannot have a universal group as a   member."),

	ERROR_DS_MODIFYDN_DISALLOWED_BY_INSTANCE_TYPE(0X00002183, "Rename or move operations on naming context heads or   read-only objects are not allowed."),

	ERROR_DS_NO_OBJECT_MOVE_IN_SCHEMA_NC(0X00002184, "Move operations on objects in the schema naming   context are not allowed."),

	ERROR_DS_MODIFYDN_DISALLOWED_BY_FLAG(0X00002185, "A system flag has been set on the object that does not   allow the object to be moved or renamed."),

	ERROR_DS_MODIFYDN_WRONG_GRANDPARENT(0X00002186, "This object is not allowed to change its grandparent   container. Moves are not forbidden on this object, but are restricted to   sibling containers."),

	ERROR_DS_NAME_ERROR_TRUST_REFERRAL(0X00002187, "Unable to resolve completely; a referral to another   forest was generated."),

	ERROR_NOT_SUPPORTED_ON_STANDARD_SERVER(0X00002188, "The requested action is not supported on a standard   server."),

	ERROR_DS_CANT_ACCESS_REMOTE_PART_OF_AD(0X00002189, "Could not access a partition of the directory service   located on a remote server. Make sure at least one server is running for the   partition in question."),

	ERROR_DS_CR_IMPOSSIBLE_TO_VALIDATE_V2(0X0000218A, "The directory cannot validate the proposed naming   context (or partition) name because it does not hold a replica, nor can it   contact a replica of the naming context above the proposed naming context.   Ensure that the parent naming context is properly registered in the DNS, and   at least one replica of this naming context is reachable by the domain naming   master."),

	ERROR_DS_THREAD_LIMIT_EXCEEDED(0X0000218B, "The thread limit for this request was exceeded."),

	ERROR_DS_NOT_CLOSEST(0X0000218C, "The GC server is not in the closest site."),

	ERROR_DS_CANT_DERIVE_SPN_WITHOUT_SERVER_REF(0X0000218D, "None"),

	ERROR_DS_SINGLE_USER_MODE_FAILED(0X0000218E, "The directory service failed to enter single-user   mode."),

	ERROR_DS_NTDSCRIPT_SYNTAX_ERROR(0X0000218F, "The directory service cannot parse the script because   of a syntax error."),

	ERROR_DS_NTDSCRIPT_PROCESS_ERROR(0X00002190, "The directory service cannot process the script   because of an error."),

	ERROR_DS_DIFFERENT_REPL_EPOCHS(0X00002191, "The directory service cannot perform the requested   operation because the servers involved are of different replication epochs   (which is usually related to a domain rename that is in progress)."),

	ERROR_DS_DRS_EXTENSIONS_CHANGED(0X00002192, "The directory service binding must be renegotiated due   to a change in the server extensions information."),

	ERROR_DS_REPLICA_SET_CHANGE_NOT_ALLOWED_ON_DISABLED_CR(0X00002193, "The operation is not allowed on a disabled   cross-reference."),

	ERROR_DS_NO_MSDS_INTID(0X00002194, "Schema update failed: No values for msDS-IntId are   available."),

	ERROR_DS_DUP_MSDS_INTID(0X00002195, "Schema update failed: Duplicate msDS-IntId. Retry the   operation."),

	ERROR_DS_EXISTS_IN_RDNATTID(0X00002196, "Schema deletion failed: Attribute is used in rDNAttID."),

	ERROR_DS_AUTHORIZATION_FAILED(0X00002197, "The directory service failed to authorize the request."),

	ERROR_DS_INVALID_SCRIPT(0X00002198, "The directory service cannot process the script   because it is invalid."),

	ERROR_DS_REMOTE_CROSSREF_OP_FAILED(0X00002199, "The remote create cross-reference operation failed on   the domain naming master FSMO. The operation's error is in the extended data."),

	ERROR_DS_CROSS_REF_BUSY(0X0000219A, "A cross-reference is in use locally with the same   name."),

	ERROR_DS_CANT_DERIVE_SPN_FOR_DELETED_DOMAIN(0X0000219B, "The directory service cannot derive an SPN with which   to mutually authenticate the target server because the server's domain has   been deleted from the forest."),

	ERROR_DS_CANT_DEMOTE_WITH_WRITEABLE_NC(0X0000219C, "Writable NCs prevent this DC from demoting."),

	ERROR_DS_DUPLICATE_ID_FOUND(0X0000219D, "The requested object has a nonunique identifier and   cannot be retrieved."),

	ERROR_DS_INSUFFICIENT_ATTR_TO_CREATE_OBJECT(0X0000219E, "Insufficient attributes were given to create an   object. This object might not exist because it might have been deleted and   the garbage already collected."),

	ERROR_DS_GROUP_CONVERSION_ERROR(0X0000219F, "The group cannot be converted due to attribute   restrictions on the requested group type."),

	ERROR_DS_CANT_MOVE_APP_BASIC_GROUP(0X000021A0, "Cross-domain moves of nonempty basic application   groups is not allowed."),

	ERROR_DS_CANT_MOVE_APP_QUERY_GROUP(0X000021A1, "Cross-domain moves of nonempty query-based application   groups is not allowed."),

	ERROR_DS_ROLE_NOT_VERIFIED(0X000021A2, "The FSMO role ownership could not be verified because   its directory partition did not replicate successfully with at least one   replication partner."),

	ERROR_DS_WKO_CONTAINER_CANNOT_BE_SPECIAL(0X000021A3, "The target container for a redirection of a well-known   object container cannot already be a special container."),

	ERROR_DS_DOMAIN_RENAME_IN_PROGRESS(0X000021A4, "The directory service cannot perform the requested   operation because a domain rename operation is in progress."),

	ERROR_DS_EXISTING_AD_CHILD_NC(0X000021A5, "The directory service detected a child partition below   the requested partition name. The partition hierarchy must be created in a   top down method."),

	ERROR_DS_REPL_LIFETIME_EXCEEDED(0X000021A6, "The directory service cannot replicate with this   server because the time since the last replication with this server has   exceeded the tombstone lifetime."),

	ERROR_DS_DISALLOWED_IN_SYSTEM_CONTAINER(0X000021A7, "The requested operation is not allowed on an object   under the system container."),

	ERROR_DS_LDAP_SEND_QUEUE_FULL(0X000021A8, "The LDAP server's network send queue has filled up   because the client is not processing the results of its requests fast enough.   No more requests will be processed until the client catches up. If the client   does not catch up then it will be disconnected."),

	ERROR_DS_DRA_OUT_SCHEDULE_WINDOW(0X000021A9, "The scheduled replication did not take place because   the system was too busy to execute the request within the schedule window.   The replication queue is overloaded. Consider reducing the number of partners   or decreasing the scheduled replication frequency."),

	ERROR_DS_POLICY_NOT_KNOWN(0X000021AA, "At this time, it cannot be determined if the branch   replication policy is available on the hub domain controller. Retry at a   later time to account for replication latencies."),

	ERROR_NO_SITE_SETTINGS_OBJECT(0X000021AB, "The site settings object for the specified site does   not exist."),

	ERROR_NO_SECRETS(0X000021AC, "The local account store does not contain secret   material for the specified account."),

	ERROR_NO_WRITABLE_DC_FOUND(0X000021AD, "Could not find a writable domain controller in the   domain."),

	ERROR_DS_NO_SERVER_OBJECT(0X000021AE, "The server object for the domain controller does not   exist."),

	ERROR_DS_NO_NTDSA_OBJECT(0X000021AF, "The NTDS Settings object for the domain controller   does not exist."),

	ERROR_DS_NON_ASQ_SEARCH(0X000021B0, "The requested search operation is not supported for   attribute scoped query (ASQ) searches."),

	ERROR_DS_AUDIT_FAILURE(0X000021B1, "A required audit event could not be generated for the   operation."),

	ERROR_DS_INVALID_SEARCH_FLAG_SUBTREE(0X000021B2, "The search flags for the attribute are invalid. The   subtree index bit is valid only on single-valued attributes."),

	ERROR_DS_INVALID_SEARCH_FLAG_TUPLE(0X000021B3, "The search flags for the attribute are invalid. The   tuple index bit is valid only on attributes of Unicode strings."),

	ERROR_DS_DRA_RECYCLED_TARGET(0X000021BF, "The replication operation failed because the target object   referenced by a link value is recycled."),

	ERROR_DS_HIGH_DSA_VERSION(0X000021C2, "The functional level of the domain (or forest) cannot   be lowered to the requested value."),

	ERROR_DS_SPN_VALUE_NOT_UNIQUE_IN_FOREST(0X000021C7, "The operation failed because the SPN value provided   for addition/modification is not unique forest-wide."),

	ERROR_DS_UPN_VALUE_NOT_UNIQUE_IN_FOREST(0X000021C8, "The operation failed because the UPN value provided   for addition/modification is not unique forest-wide."),

	DNS_ERROR_RCODE_FORMAT_ERROR(0X00002329, "DNS server unable to interpret format."),

	DNS_ERROR_RCODE_SERVER_FAILURE(0X0000232A, "DNS server failure."),

	DNS_ERROR_RCODE_NAME_ERROR(0X0000232B, "DNS name does not exist."),

	DNS_ERROR_RCODE_NOT_IMPLEMENTED(0X0000232C, "DNS request not supported by name server."),

	DNS_ERROR_RCODE_REFUSED(0X0000232D, "DNS operation refused."),

	DNS_ERROR_RCODE_YXDOMAIN(0X0000232E, "DNS name that should not exist, does exist."),

	DNS_ERROR_RCODE_YXRRSET(0X0000232F, "DNS resource record (RR) set that should not exist, does   exist."),

	DNS_ERROR_RCODE_NXRRSET(0X00002330, "DNS RR set that should to exist, does not exist."),

	DNS_ERROR_RCODE_NOTAUTH(0X00002331, "DNS server not authoritative for zone."),

	DNS_ERROR_RCODE_NOTZONE(0X00002332, "DNS name in update or prereq is not in zone."),

	DNS_ERROR_RCODE_BADSIG(0X00002338, "DNS signature failed to verify."),

	DNS_ERROR_RCODE_BADKEY(0X00002339, "DNS bad key."),

	DNS_ERROR_RCODE_BADTIME(0X0000233A, "DNS signature validity expired."),

	DNS_INFO_NO_RECORDS(0X0000251D, "No records found for given DNS query."),

	DNS_ERROR_BAD_PACKET(0X0000251E, "Bad DNS packet."),

	DNS_ERROR_NO_PACKET(0X0000251F, "No DNS packet."),

	DNS_ERROR_RCODE(0X00002520, "DNS error, check rcode."),

	DNS_ERROR_UNSECURE_PACKET(0X00002521, "Unsecured DNS packet."),

	DNS_ERROR_INVALID_TYPE(0X0000254F, "Invalid DNS type."),

	DNS_ERROR_INVALID_IP_ADDRESS(0X00002550, "Invalid IP address."),

	DNS_ERROR_INVALID_PROPERTY(0X00002551, "Invalid property."),

	DNS_ERROR_TRY_AGAIN_LATER(0X00002552, "Try DNS operation again later."),

	DNS_ERROR_NOT_UNIQUE(0X00002553, "Record for given name and type is not unique."),

	DNS_ERROR_NON_RFC_NAME(0X00002554, "DNS name does not comply with RFC specifications."),

	DNS_STATUS_FQDN(0X00002555, "DNS name is a fully qualified DNS name."),

	DNS_STATUS_DOTTED_NAME(0X00002556, "DNS name is dotted (multilabel)."),

	DNS_STATUS_SINGLE_PART_NAME(0X00002557, "DNS name is a single-part name."),

	DNS_ERROR_INVALID_NAME_CHAR(0X00002558, "DNS name contains an invalid character."),

	DNS_ERROR_NUMERIC_NAME(0X00002559, "DNS name is entirely numeric."),

	DNS_ERROR_NOT_ALLOWED_ON_ROOT_SERVER(0X0000255A, "The operation requested is not permitted on a DNS root   server."),

	DNS_ERROR_NOT_ALLOWED_UNDER_DELEGATION(0X0000255B, "The record could not be created because this part of   the DNS namespace has been delegated to another server."),

	DNS_ERROR_CANNOT_FIND_ROOT_HINTS(0X0000255C, "The DNS server could not find a set of root hints."),

	DNS_ERROR_INCONSISTENT_ROOT_HINTS(0X0000255D, "The DNS server found root hints but they were not   consistent across all adapters."),

	DNS_ERROR_DWORD_VALUE_TOO_SMALL(0X0000255E, "The specified value is too small for this parameter."),

	DNS_ERROR_DWORD_VALUE_TOO_LARGE(0X0000255F, "The specified value is too large for this parameter."),

	DNS_ERROR_BACKGROUND_LOADING(0X00002560, "This operation is not allowed while the DNS server is   loading zones in the background. Try again later."),

	DNS_ERROR_NOT_ALLOWED_ON_RODC(0X00002561, "The operation requested is not permitted on against a   DNS server running on a read-only DC."),

	DNS_ERROR_ZONE_DOES_NOT_EXIST(0X00002581, "DNS zone does not exist."),

	DNS_ERROR_NO_ZONE_INFO(0X00002582, "DNS zone information not available."),

	DNS_ERROR_INVALID_ZONE_OPERATION(0X00002583, "Invalid operation for DNS zone."),

	DNS_ERROR_ZONE_CONFIGURATION_ERROR(0X00002584, "Invalid DNS zone configuration."),

	DNS_ERROR_ZONE_HAS_NO_SOA_RECORD(0X00002585, "DNS zone has no start of authority (SOA) record."),

	DNS_ERROR_ZONE_HAS_NO_NS_RECORDS(0X00002586, "DNS zone has no Name Server (NS) record."),

	DNS_ERROR_ZONE_LOCKED(0X00002587, "DNS zone is locked."),

	DNS_ERROR_ZONE_CREATION_FAILED(0X00002588, "DNS zone creation failed."),

	DNS_ERROR_ZONE_ALREADY_EXISTS(0X00002589, "DNS zone already exists."),

	DNS_ERROR_AUTOZONE_ALREADY_EXISTS(0X0000258A, "DNS automatic zone already exists."),

	DNS_ERROR_INVALID_ZONE_TYPE(0X0000258B, "Invalid DNS zone type."),

	DNS_ERROR_SECONDARY_REQUIRES_MASTER_IP(0X0000258C, "Secondary DNS zone requires master IP address."),

	DNS_ERROR_ZONE_NOT_SECONDARY(0X0000258D, "DNS zone not secondary."),

	DNS_ERROR_NEED_SECONDARY_ADDRESSES(0X0000258E, "Need secondary IP address."),

	DNS_ERROR_WINS_INIT_FAILED(0X0000258F, "WINS initialization failed."),

	DNS_ERROR_NEED_WINS_SERVERS(0X00002590, "Need WINS servers."),

	DNS_ERROR_NBSTAT_INIT_FAILED(0X00002591, "NBTSTAT initialization call failed."),

	DNS_ERROR_SOA_DELETE_INVALID(0X00002592, "Invalid delete of SOA."),

	DNS_ERROR_FORWARDER_ALREADY_EXISTS(0X00002593, "A conditional forwarding zone already exists for that   name."),

	DNS_ERROR_ZONE_REQUIRES_MASTER_IP(0X00002594, "This zone must be configured with one or more master   DNS server IP addresses."),

	DNS_ERROR_ZONE_IS_SHUTDOWN(0X00002595, "The operation cannot be performed because this zone is   shut down."),

	DNS_ERROR_PRIMARY_REQUIRES_DATAFILE(0X000025B3, "The primary DNS zone requires a data file."),

	DNS_ERROR_INVALID_DATAFILE_NAME(0X000025B4, "Invalid data file name for the DNS zone."),

	DNS_ERROR_DATAFILE_OPEN_FAILURE(0X000025B5, "Failed to open the data file for the DNS zone."),

	DNS_ERROR_FILE_WRITEBACK_FAILED(0X000025B6, "Failed to write the data file for the DNS zone."),

	DNS_ERROR_DATAFILE_PARSING(0X000025B7, "Failure while reading datafile for DNS zone."),

	DNS_ERROR_RECORD_DOES_NOT_EXIST(0X000025E5, "DNS record does not exist."),

	DNS_ERROR_RECORD_FORMAT(0X000025E6, "DNS record format error."),

	DNS_ERROR_NODE_CREATION_FAILED(0X000025E7, "Node creation failure in DNS."),

	DNS_ERROR_UNKNOWN_RECORD_TYPE(0X000025E8, "Unknown DNS record type."),

	DNS_ERROR_RECORD_TIMED_OUT(0X000025E9, "DNS record timed out."),

	DNS_ERROR_NAME_NOT_IN_ZONE(0X000025EA, "Name not in DNS zone."),

	DNS_ERROR_CNAME_LOOP(0X000025EB, "CNAME loop detected."),

	DNS_ERROR_NODE_IS_CNAME(0X000025EC, "Node is a CNAME DNS record."),

	DNS_ERROR_CNAME_COLLISION(0X000025ED, "A CNAME record already exists for the given name."),

	DNS_ERROR_RECORD_ONLY_AT_ZONE_ROOT(0X000025EE, "Record is only at DNS zone root."),

	DNS_ERROR_RECORD_ALREADY_EXISTS(0X000025EF, "DNS record already exists."),

	DNS_ERROR_SECONDARY_DATA(0X000025F0, "Secondary DNS zone data error."),

	DNS_ERROR_NO_CREATE_CACHE_DATA(0X000025F1, "Could not create DNS cache data."),

	DNS_ERROR_NAME_DOES_NOT_EXIST(0X000025F2, "DNS name does not exist."),

	DNS_WARNING_PTR_CREATE_FAILED(0X000025F3, "Could not create pointer (PTR) record."),

	DNS_WARNING_DOMAIN_UNDELETED(0X000025F4, "DNS domain was undeleted."),

	DNS_ERROR_DS_UNAVAILABLE(0X000025F5, "The directory service is unavailable."),

	DNS_ERROR_DS_ZONE_ALREADY_EXISTS(0X000025F6, "DNS zone already exists in the directory service."),

	DNS_ERROR_NO_BOOTFILE_IF_DS_ZONE(0X000025F7, "DNS server not creating or reading the boot file for   the directory service integrated DNS zone."),

	DNS_INFO_AXFR_COMPLETE(0X00002617, "DNS AXFR (zone transfer) complete."),

	DNS_ERROR_AXFR(0X00002618, "DNS zone transfer failed."),

	DNS_INFO_ADDED_LOCAL_WINS(0X00002619, "Added local WINS server."),

	DNS_STATUS_CONTINUE_NEEDED(0X00002649, "Secure update call needs to continue update request."),

	DNS_ERROR_NO_TCPIP(0X0000267B, "TCP/IP network protocol not installed."),

	DNS_ERROR_NO_DNS_SERVERS(0X0000267C, "No DNS servers configured for local system."),

	DNS_ERROR_DP_DOES_NOT_EXIST(0X000026AD, "The specified directory partition does not exist."),

	DNS_ERROR_DP_ALREADY_EXISTS(0X000026AE, "The specified directory partition already exists."),

	DNS_ERROR_DP_NOT_ENLISTED(0X000026AF, "This DNS server is not enlisted in the specified   directory partition."),

	DNS_ERROR_DP_ALREADY_ENLISTED(0X000026B0, "This DNS server is already enlisted in the specified   directory partition."),

	DNS_ERROR_DP_NOT_AVAILABLE(0X000026B1, "The directory partition is not available at this time.   Wait a few minutes and try again."),

	DNS_ERROR_DP_FSMO_ERROR(0X000026B2, "The application directory partition operation failed.   The domain controller holding the domain naming master role is down or unable   to service the request or is not running Windows Server 2003."),


	ERROR_EVT_INVALID_CHANNEL_PATH(0X00003A98, "The specified channel path is invalid."),

	ERROR_EVT_INVALID_QUERY(0X00003A99, "The specified query is invalid."),

	ERROR_EVT_PUBLISHER_METADATA_NOT_FOUND(0X00003A9A, "The publisher metadata cannot be found in the   resource."),

	ERROR_EVT_EVENT_TEMPLATE_NOT_FOUND(0X00003A9B, "The template for an event definition cannot be found   in the resource (error = %1)."),

	ERROR_EVT_INVALID_PUBLISHER_NAME(0X00003A9C, "The specified publisher name is invalid."),

	ERROR_EVT_INVALID_EVENT_DATA(0X00003A9D, "The event data raised by the publisher is not   compatible with the event template definition in the publisher's manifest."),

	ERROR_EVT_CHANNEL_NOT_FOUND(0X00003A9F, "The specified channel could not be found. Check   channel configuration."),

	ERROR_EVT_MALFORMED_XML_TEXT(0X00003AA0, "The specified XML text was not well-formed. See   extended error for more details."),

	ERROR_EVT_SUBSCRIPTION_TO_DIRECT_CHANNEL(0X00003AA1, "The caller is trying to subscribe to a direct channel   which is not allowed. The events for a direct channel go directly to a log   file and cannot be subscribed to."),

	ERROR_EVT_CONFIGURATION_ERROR(0X00003AA2, "Configuration error."),

	ERROR_EVT_QUERY_RESULT_STALE(0X00003AA3, "The query result is stale or invalid. This might be   due to the log being cleared or rolling over after the query result was   created. Users should handle this code by releasing the query result object   and reissuing the query."),

	ERROR_EVT_QUERY_RESULT_INVALID_POSITION(0X00003AA4, "Query result is currently at an invalid position."),

	ERROR_EVT_NON_VALIDATING_MSXML(0X00003AA5, "Registered Microsoft XML (MSXML) does not support   validation."),

	ERROR_EVT_FILTER_ALREADYSCOPED(0X00003AA6, "An expression can only be followed by a   change-of-scope operation if it itself evaluates to a node set and is not   already part of some other change-of-scope operation."),

	ERROR_EVT_FILTER_NOTELTSET(0X00003AA7, "Cannot perform a step operation from a term that does   not represent an element set."),

	ERROR_EVT_FILTER_INVARG(0X00003AA8, "Left side arguments to binary operators must be either   attributes, nodes, or variables and right side arguments must be constants."),

	ERROR_EVT_FILTER_INVTEST(0X00003AA9, "A step operation must involve either a node test or,   in the case of a predicate, an algebraic expression against which to test   each node in the node set identified by the preceding node set can be   evaluated."),

	ERROR_EVT_FILTER_INVTYPE(0X00003AAA, "This data type is currently unsupported."),

	ERROR_EVT_FILTER_PARSEERR(0X00003AAB, "A syntax error occurred at position %1!d!"),

	ERROR_EVT_FILTER_UNSUPPORTEDOP(0X00003AAC, "This operator is unsupported by this implementation of   the filter."),

	ERROR_EVT_FILTER_UNEXPECTEDTOKEN(0X00003AAD, "The token encountered was unexpected."),

	ERROR_EVT_INVALID_OPERATION_OVER_ENABLED_DIRECT_CHANNEL(0X00003AAE, "The requested operation cannot be performed over an   enabled direct channel. The channel must first be disabled before performing   the requested operation."),

	ERROR_EVT_INVALID_CHANNEL_PROPERTY_VALUE(0X00003AAF, "Channel property %1!s! contains an invalid value. The   value has an invalid type, is outside the valid range, cannot be updated, or   is not supported by this type of channel."),

	ERROR_EVT_INVALID_PUBLISHER_PROPERTY_VALUE(0X00003AB0, "Publisher property %1!s! contains an invalid value.   The value has an invalid type, is outside the valid range, cannot be updated,   or is not supported by this type of publisher."),

	ERROR_EVT_CHANNEL_CANNOT_ACTIVATE(0X00003AB1, "The channel fails to activate."),

	ERROR_EVT_FILTER_TOO_COMPLEX(0X00003AB2, "The xpath expression exceeded supported complexity.   Simplify it or split it into two or more simple expressions."),

	ERROR_EVT_MESSAGE_NOT_FOUND(0X00003AB3, "The message resource is present but the message is not   found in the string or message table."),

	ERROR_EVT_MESSAGE_ID_NOT_FOUND(0X00003AB4, "The message ID for the desired message could not be   found."),

	ERROR_EVT_UNRESOLVED_VALUE_INSERT(0X00003AB5, "The substitution string for the insert index (%1)   could not be found."),

	ERROR_EVT_UNRESOLVED_PARAMETER_INSERT(0X00003AB6, "The description string for the parameter reference   (%1) could not be found."),

	ERROR_EVT_MAX_INSERTS_REACHED(0X00003AB7, "The maximum number of replacements has been reached."),

	ERROR_EVT_EVENT_DEFINITION_NOT_OUND(0X00003AB8, "The event definition could not be found for the event   ID (%1)."),

	ERROR_EVT_MESSAGE_LOCALE_NOT_FOUND(0X00003AB9, "The locale-specific resource for the desired message   is not present."),

	ERROR_EVT_VERSION_TOO_OLD(0X00003ABA, "The resource is too old to be compatible."),

	ERROR_EVT_VERSION_TOO_NEW(0X00003ABB, "The resource is too new to be compatible."),

	ERROR_EVT_CANNOT_OPEN_CHANNEL_OF_QUERY(0X00003ABC, "The channel at index %1 of the query cannot be opened."),

	ERROR_EVT_PUBLISHER_DISABLED(0X00003ABD, "The publisher has been disabled and its resource is   not available. This usually occurs when the publisher is in the process of   being uninstalled or upgraded."),

	ERROR_EC_SUBSCRIPTION_CANNOT_ACTIVATE(0X00003AE8, "The subscription fails to activate."),

	ERROR_EC_LOG_DISABLED(0X00003AE9, "The log of the subscription is in a disabled state and   events cannot be forwarded to it. The log must first be enabled before the   subscription can be activated."),

	ERROR_MUI_FILE_NOT_FOUND(0X00003AFC, "The resource loader failed to find the Multilingual   User Interface (MUI) file."),

	ERROR_MUI_INVALID_FILE(0X00003AFD, "The resource loader failed to load the MUI file   because the file failed to pass validation."),

	ERROR_MUI_INVALID_RC_CONFIG(0X00003AFE, "The release candidate (RC) manifest is corrupted with   garbage data, is an unsupported version, or is missing a required item."),

	ERROR_MUI_INVALID_LOCALE_NAME(0X00003AFF, "The RC manifest has an invalid culture name."),

	ERROR_MUI_INVALID_ULTIMATEFALLBACK_NAME(0X00003B00, "The RC Manifest has an invalid ultimate fallback name."),

	ERROR_MUI_FILE_NOT_LOADED(0X00003B01, "The resource loader cache does not have a loaded MUI   entry."),

	ERROR_RESOURCE_ENUM_USER_STOP(0X00003B02, "The user stopped resource enumeration."),

	ERROR_MUI_INTLSETTINGS_UILANG_NOT_INSTALLED(0X00003B03, "User interface language installation failed."),

	ERROR_MUI_INTLSETTINGS_INVALID_LOCALE_NAME(0X00003B04, "Locale installation failed."),

	ERROR_MCA_INVALID_CAPABILITIES_STRING(0X00003B60, "The monitor returned a DDC/CI capabilities string that   did not comply with the ACCESS.bus 3.0, DDC/CI 1.1, or MCCS 2 Revision 1   specification."),

	ERROR_MCA_INVALID_VCP_VERSION(0X00003B61, "The monitor's VCP version (0xDF) VCP code returned an   invalid version value."),

	ERROR_MCA_MONITOR_VIOLATES_MCCS_SPECIFICATION(0X00003B62, "The monitor does not comply with the MCCS   specification it claims to support."),

	ERROR_MCA_MCCS_VERSION_MISMATCH(0X00003B63, "The MCCS version in a monitor's mccs_ver capability   does not match the MCCS version the monitor reports when the VCP version   (0xDF) VCP code is used."),

	ERROR_MCA_UNSUPPORTED_MCCS_VERSION(0X00003B64, "The monitor configuration API works only with monitors   that support the MCCS 1.0, MCCS 2.0, or MCCS 2.0 Revision 1 specifications."),

	ERROR_MCA_INTERNAL_ERROR(0X00003B65, "An internal monitor configuration API error occurred."),

	ERROR_MCA_INVALID_TECHNOLOGY_TYPE_RETURNED(0X00003B66, "The monitor returned an invalid monitor technology   type. CRT, plasma, and LCD (TFT) are examples of monitor technology types.   This error implies that the monitor violated the MCCS 2.0 or MCCS 2.0   Revision 1 specification."),

	ERROR_MCA_UNSUPPORTED_COLOR_TEMPERATURE(0X00003B67, "The SetMonitorColorTemperature() caller passed a color   temperature to it that the current monitor did not support. CRT, plasma, and   LCD (TFT) are examples of monitor technology types. This error implies that   the monitor violated the MCCS 2.0 or MCCS 2.0 Revision 1 specification."),

	ERROR_AMBIGUOUS_SYSTEM_DEVICE(0X00003B92, "The requested system device cannot be identified due   to multiple indistinguishable devices potentially matching the identification   criteria."),

	ERROR_SYSTEM_DEVICE_NOT_FOUND(0X00003BC3, "The requested system device cannot be found.");

	private final int mask;
	private final String description;

	Win32ErrorCode(final int mask, final String description) {
		this.mask = mask;
		this.description = description;
	}

	@Override
	public int getMask() {
		return mask;
	}

}
			