/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamfactory.DelayedStreamFactory;

/**
 * Delay the items emitted by the stream created with the target {@link StreamId} by the specified {@link Duration}
 * 
 * @see DelayedStreamFactory
 * @author acalia
 * @param <T> type of the original data stream
 */
public class DelayedStreamId<T> implements StreamId<T> {

    private final StreamId<T> target;
    private final Duration delay;

    public static <T> DelayedStreamId<T> delayBy(StreamId<T> target, Duration delay) {
        return new DelayedStreamId<>(target, delay);
    }

    public DelayedStreamId(StreamId<T> target, Duration delay) {
        this.target = requireNonNull(target, "target of the delay must not be null");
        this.delay = requireNonNull(delay, "delay must not be null");
    }

    public StreamId<T> getTarget() {
        return target;
    }

    public Duration getDelay() {
        return delay;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delay == null) ? 0 : delay.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DelayedStreamId<?> other = (DelayedStreamId<?>) obj;
        if (delay == null) {
            if (other.delay != null) {
                return false;
            }
        } else if (!delay.equals(other.delay)) {
            return false;
        }
        if (target == null) {
            if (other.target != null) {
                return false;
            }
        } else if (!target.equals(other.target)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DelayedStreamId [target=" + target + ", delay=" + delay + "]";
    }

}
