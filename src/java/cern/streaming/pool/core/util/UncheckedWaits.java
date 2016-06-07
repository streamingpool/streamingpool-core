/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UncheckedWaits {

    private UncheckedWaits() {
        /* Only static methods */
    }

    public static final void waitFor(long duration, TimeUnit timeUnit) {
        Objects.requireNonNull("timeUnit must not be null");
        try {
            timeUnit.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while waiting.", e);
        }
    }

}
