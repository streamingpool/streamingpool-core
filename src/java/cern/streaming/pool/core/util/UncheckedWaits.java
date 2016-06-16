/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.util;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class UncheckedWaits {

    private UncheckedWaits() {
        /* Only static methods */
    }

    public static void waitFor(long duration, TimeUnit timeUnit) {
        Objects.requireNonNull(timeUnit, "timeUnit must not be null");
        try {
            timeUnit.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException("Error while waiting.", e);
        }
    }

}
