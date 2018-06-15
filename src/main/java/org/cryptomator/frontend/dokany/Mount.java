package org.cryptomator.frontend.dokany;

import com.dokany.java.DokanyDriver;

import java.util.concurrent.CompletableFuture;

public class Mount implements AutoCloseable {

    private final DokanyDriver driver;
    private final  CompletableFuture<Void> driverJob;

    public Mount(DokanyDriver driver) {
        this.driver = driver;
		this.driverJob = CompletableFuture.runAsync(driver::start);
    }

    @Override
    public void close() {
        try {
            driver.shutdown();
        } catch (Exception e) {
        	e.printStackTrace();
		}
		// TODO: force quit?
		// driverJob.cancel(true);
    }
}
