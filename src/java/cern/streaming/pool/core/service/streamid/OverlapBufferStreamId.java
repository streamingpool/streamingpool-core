/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import java.time.Duration;
import java.util.List;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamfactory.OverlapBufferStreamFactory;

/**
 * Provide an overlapping buffer for the specified {@link #sourceId}. The buffer begins whenever {@link #startId} stream
 * emits an item and ends when the same item is emitted on the {@link #endId} stream. Optionally, it is possible to
 * specify a timeout for the closing stream in case it does not emit items. In the case of a timeout, the buffers will
 * be closed by whichever emit first, {@link #endId} or timout.
 * <p>
 * The behavior is very similar to the {@code  buffer(Observable<TOpening> bufferOpenings, Func1<TOpening, Observable
 * <TClosing>> bufferClosingSelector)} RxJava 1 operator.
 * 
 * @see OverlapBufferStreamFactory
 * @see <a href="http://reactivex.io/RxJava/javadoc/rx/Observable.html#buffer(rx.Observable,%20rx.functions.Func1)">
 *      RxJava 1 buffer documentation</a>
 * @author acalia
 * @param <T> the type of the data stream
 * @param <U> the type of the start and end stream
 */
public class OverlapBufferStreamId<T, U> implements StreamId<List<T>> {

    private static final Duration NO_TIMEOUT = Duration.ofSeconds(-1);

    private final StreamId<T> sourceId;
    private final StreamId<U> startId;
    private final StreamId<U> endId;
    private final Duration timeout;

    public static <T, U> OverlapBufferStreamId<T, U> of(StreamId<T> sourceId, StreamId<U> startId, StreamId<U> endId,
            Duration timeout) {
        return new OverlapBufferStreamId<>(sourceId, startId, endId, timeout);
    }

    public static <T, U> OverlapBufferStreamId<T, U> of(StreamId<T> sourceId, StreamId<U> startId, StreamId<U> endId) {
        return new OverlapBufferStreamId<>(sourceId, startId, endId, NO_TIMEOUT);
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((endId == null) ? 0 : endId.hashCode());
        result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
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
        OverlapBufferStreamId<?, ?> other = (OverlapBufferStreamId<?, ?>) obj;
        if (endId == null) {
            if (other.endId != null) {
                return false;
            }
        } else if (!endId.equals(other.endId)) {
            return false;
        }
        if (sourceId == null) {
            if (other.sourceId != null) {
                return false;
            }
        } else if (!sourceId.equals(other.sourceId)) {
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
        return "OverlapBufferStreamId [sourceId=" + sourceId + ", startId=" + startId + ", endId=" + endId
                + ", timeout=" + timeout + "]";
    }

}
