/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.service.streamid;

import java.util.concurrent.TimeUnit;

import org.streamingpool.core.service.StreamId;

/**
 * A stream id, which emits values periodically.
 * 
 * @author mhruska, mpocwier
 */
public class IntervalStreamId implements StreamId<Long> {
    private static final long serialVersionUID = 1L;

    private final long period;
    private final TimeUnit periodTimeUnit;
    private final long initialDelay;
    private final TimeUnit initialDelayTimeUnit;

    private IntervalStreamId(long initialDelay, TimeUnit initialDelayTimeUnit, long period, TimeUnit periodTimeUnit) {
        this.periodTimeUnit = periodTimeUnit;
        this.initialDelayTimeUnit = initialDelayTimeUnit;
        this.period = period;
        this.initialDelay = initialDelay;
    }

    /**
     * Creates stream id that emits sequentially increasing number with specified interval. First value is emitted
     * immediately.
     * 
     * @param period Specifies how often values should be emitted.
     * @param timeUnit Time unit for period.
     * @return IntervalStreamId
     */
    public static final IntervalStreamId every(long period, TimeUnit timeUnit) {
        return new IntervalStreamId(0, TimeUnit.SECONDS, period, timeUnit);
    }

    /**
     * Creates stream id with the same period as the current one, but delayed by specific time.
     * 
     * @param newInitialDelay Time by which to delay the stream
     * @param timeUnit Time unit for newInitialDelay
     * @return IntervalStreamId
     */
    public final IntervalStreamId delayedBy(long newInitialDelay, TimeUnit timeUnit) {
        return new IntervalStreamId(newInitialDelay, timeUnit, period, periodTimeUnit);
    }

    public TimeUnit getPeriodTimeUnit() {
        return periodTimeUnit;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public long getPeriod() {
        return period;
    }

    public TimeUnit getInitialDelayTimeUnit() {
        return initialDelayTimeUnit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (initialDelay ^ (initialDelay >>> 32));
        result = prime * result + (int) (period ^ (period >>> 32));
        result = prime * result + ((periodTimeUnit == null) ? 0 : periodTimeUnit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IntervalStreamId other = (IntervalStreamId) obj;
        if (initialDelay != other.initialDelay)
            return false;
        if (period != other.period)
            return false;
        if (periodTimeUnit != other.periodTimeUnit)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IntervalStreamId [period=" + period + ", periodTimeUnit=" + periodTimeUnit + ", initialDelay="
                + initialDelay + ", initialDelayTimeUnit=" + initialDelayTimeUnit + "]";
    }
}
