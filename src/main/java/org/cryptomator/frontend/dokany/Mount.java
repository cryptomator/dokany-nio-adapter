package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyDriver;

public class Mount implements AutoCloseable {

    private final DokanyDriver driver;

    public Mount(DokanyDriver driver) {
        this.driver = driver;
        driver.start();
    }

    @Override
    public void close() {
        driver.shutdown();
    }
}
