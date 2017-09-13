package org.streamingpool.core.conf;

import io.reactivex.Scheduler;

/**
 * @author mgabriel.
 */
public class PoolConfiguration {

    private final Scheduler scheduler;
    private final int observeOnCapacity;

    public PoolConfiguration(Scheduler scheduler) {
        this(scheduler, 128);
    }

    public PoolConfiguration(Scheduler scheduler, int observeOnCapacity) {
        this.scheduler = scheduler;
        this.observeOnCapacity = observeOnCapacity;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public int getObserveOnCapacity() {
        return observeOnCapacity;
    }
}
