/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import java.time.Duration;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamfactory.DelayedStreamIdStreamFactory;

/**
 * Delay the items emitted by the stream created with the target {@link StreamId} by the specified {@link Duration}
 * 
 * @see DelayedStreamIdStreamFactory
 * @author acalia 
 * @param <T> type of the original data stream
 */
public class DelayedStreamId<T> implements StreamId<T> {

    private final StreamId<T> target;
    private final Duration delay;

    private DelayedStreamId(StreamId<T> target, Duration delay) {
        super();
        this.target = target;
        this.delay = delay;
    }

    public static <T> DelayedStreamId<T> of(StreamId<T> target, Duration delay) {
        return new DelayedStreamId<T>(target, delay);
    }

    public StreamId<T> getTarget() {
        return target;
    }

    public Duration getDelay() {
        return delay;
    }

}
