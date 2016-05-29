/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.util;

import java.util.concurrent.CountDownLatch;

public class Latches {

    private Latches() {
        /* Only static methods */
    }

    public static final void awaitUnchecked(CountDownLatch finished) {
        try {
            finished.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error when awaiting latch.", e);
        }
    }

}
