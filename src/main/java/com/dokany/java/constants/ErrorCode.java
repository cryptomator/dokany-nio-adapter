package com.dokany.java.constants;

/**
 * Pseudo-Wrapper class for different NtStatus-Codes (e.g. ERROR_FILE_NOT_FOUND has the error code of {@link NtStatus#OBJECT_NAME_NOT_FOUND})
 * TODO: maybe we should change this to use directly NtStatus. And are all error codes correct?
 */
public enum ErrorCode {
    SUCCESS(0),
    ERROR_WRITE_FAULT(29),
    ERROR_READ_FAULT(30),
    ERROR_FILE_NOT_FOUND(-1073741772),
    OBJECT_NAME_COLLISION(-1073741771),
    ERROR_FILE_EXISTS(80),
    ERROR_ALREADY_EXISTS(183);

    private final int mask;

    ErrorCode(final int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return this.mask;
    }
}
