package org.cryptomator.frontend.dokany.mount;

@FunctionalInterface
public interface SafeUnmountCheck {

	boolean safeUnmountPossible();

}
