/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

import cern.streaming.pool.core.service.StreamId;

public class BufferSpecification {

    private StreamId<?> startId;
    private StreamId<?> endId;
    private Duration timeout;

    private static final Duration NO_TIMEOUT = Duration.ofSeconds(-1);

    private BufferSpecification(StreamId<?> startStreamId, StreamId<?> endStreamId, Duration timeout) {
        this.startId = requireNonNull(startStreamId, "startStreamId must not be null.");
        this.endId = requireNonNull(endStreamId, "endStreamId must not be null.");
        this.timeout = requireNonNull(timeout, "timeout must not be null");
    }

    public static BufferSpecification ofStartEndTimeout(StreamId<?> startStreamId, StreamId<?> endStreamId,
            Duration timeout) {
        return new BufferSpecification(startStreamId, endStreamId, timeout);
    }

    public static BufferSpecification ofStartAndEnd(StreamId<?> startStreamId, StreamId<?> endStreamId) {
        return new BufferSpecification(startStreamId, endStreamId, NO_TIMEOUT);
    }

    public StreamId<?> startId() {
        return startId;
    }

    public StreamId<?> endId() {
        return endId;
    }

    public Duration timeout() {
        return timeout;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endId == null) ? 0 : endId.hashCode());
        result = prime * result + ((startId == null) ? 0 : startId.hashCode());
        result = prime * result + ((timeout == null) ? 0 : timeout.hashCode());
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
        BufferSpecification other = (BufferSpecification) obj;
        if (endId == null) {
            if (other.endId != null) {
                return false;
            }
        } else if (!endId.equals(other.endId)) {
            return false;
        }
        if (startId == null) {
            if (other.startId != null) {
                return false;
            }
        } else if (!startId.equals(other.startId)) {
            return false;
        }
        if (timeout == null) {
            if (other.timeout != null) {
                return false;
            }
        } else if (!timeout.equals(other.timeout)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BufferSpecification [startId=" + startId + ", endId=" + endId + ", timeout=" + timeout + "]";
    }

}