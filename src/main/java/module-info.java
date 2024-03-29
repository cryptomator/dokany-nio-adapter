import org.cryptomator.frontend.dokany.mount.DokanyMountProvider;

module org.cryptomator.frontend.dokany {
	requires org.cryptomator.integrations.api;
	requires org.slf4j;
	requires com.sun.jna.platform;
	requires com.sun.jna;
	requires commons.cli;
	requires com.google.common;// TODO try to remove

	opens org.cryptomator.frontend.dokany.internal.structure to com.sun.jna;
	opens org.cryptomator.frontend.dokany.internal.structure.filesecurity to com.sun.jna;

	provides org.cryptomator.integrations.mount.MountService with DokanyMountProvider;
}