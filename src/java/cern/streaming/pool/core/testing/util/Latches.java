/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.testing.util;

import java.util.concurrent.CountDownLatch;

/**
 * Utility class when dealing with Latches-like objects.
 */
public final class Latches {

    private Latches() {
        /* Only static methods */
    }

    public static void awaitUnchecked(CountDownLatch finished) {
        try {
            finished.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error when awaiting latch.", e);
        }
    }

}
