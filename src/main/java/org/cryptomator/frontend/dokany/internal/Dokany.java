package org.cryptomator.frontend.dokany.internal;

/**
 * Guard class to access Dokan-API
 */
public class Dokany {

	public static boolean isInstalled() {
		try {
			NativeMethods.DokanVersion();
			return true;
		} catch (UnsatisfiedLinkError err) {
			return false;
		}
	}

	/**
	 * Get the version of the Dokan API.
	 * @return The api version or {@code -1}, if the library cannot be accessed
	 */
	public static long apiVersion() {
		try {
			return NativeMethods.DokanVersion();
		} catch (UnsatisfiedLinkError err) {
			return -1L;
		}
	}

	/**
	 * Get the version of the Dokan driver.
	 * @return The driver version or {@code -1}, if the library cannot be accessed
	 */
	public static long driverVersion() {
		try {
			return NativeMethods.DokanDriverVersion();
		} catch (UnsatisfiedLinkError err) {
			return -1L;
		}

	}

}
