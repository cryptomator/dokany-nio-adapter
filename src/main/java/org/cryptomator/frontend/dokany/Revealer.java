package org.cryptomator.frontend.dokany;

import java.nio.file.Path;

@FunctionalInterface
public interface Revealer {

	void reveal(Path path) throws Exception;

}
