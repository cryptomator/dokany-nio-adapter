package org.cryptomator.frontend.dokany;

import java.util.function.Supplier;

/**
 * Class to check if it is safe to unmount a filesystem.
 * <p>
 * Call {@link #safeUnmountPossible()} to probe if it is safe to unmount. There is no guarantee, that the result is valid for a time period.
 * <p>
 * To create an instance, call {@link OpenHandleCheckBuilder} to accquire a builder which can be given the targeted filesystem.
 * The filesystem creates a function (with all the internal wiring to know when it's save to unmount), gives it the builder and the builder can then build an actual instance.
 */
public class OpenHandleCheck implements SafeUnmountCheck {

	private final Supplier<Boolean> handleCheck;

	private OpenHandleCheck(Supplier<Boolean> handleCheck) {
		this.handleCheck = handleCheck;
	}


	@Override
	public boolean safeUnmountPossible() {
		return handleCheck.get();
	}


	public static OpenHandleCheckBuilder getBuilder() {
		return new OpenHandleCheckBuilder();
	}

	static class OpenHandleCheckBuilder {

		private Supplier<Boolean> function;

		public void setFunction(Supplier<Boolean> function) {
			this.function = function;
		}

		public OpenHandleCheck build() {
			return new OpenHandleCheck(function);
		}
	}

}
