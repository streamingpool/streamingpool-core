/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import java.time.Duration;
import java.util.List;

import cern.streaming.pool.core.service.StreamId;

public class OverlapBufferStreamId<T, U> implements StreamId<List<T>> {

    private final StreamId<T> sourceId;
    private final StreamId<U> startId;
    private final StreamId<U> endId;
    private final Duration timeout;

    public static <T, U> OverlapBufferStreamId<T, U> of(StreamId<T> sourceId, StreamId<U> startId, StreamId<U> endId,
            Duration timeout) {
        return new OverlapBufferStreamId<T, U>(sourceId, startId, endId, timeout);
    }

    private OverlapBufferStreamId(StreamId<T> sourceId, StreamId<U> startId, StreamId<U> endId, Duration timeout) {
        this.sourceId = sourceId;
        this.startId = startId;
        this.endId = endId;
        this.timeout = timeout;
    }

    public StreamId<T> sourceId() {
        return sourceId;
    }

    public StreamId<U> startId() {
        return startId;
    }

    public StreamId<U> endId() {
        return endId;
    }

    public Duration timeout() {
        return timeout;
    }

}
