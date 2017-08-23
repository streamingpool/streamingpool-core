// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
*
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
// @formatter:on

package org.streamingpool.core.service.streamid;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.DelayedStreamFactory;

/**
 * Delay the items emitted by the stream created with the target {@link StreamId} by the specified {@link Duration}
 *
 * @see DelayedStreamFactory
 * @author acalia
 * @param <T> type of the original data stream
 */
public class DelayedStreamId<T> implements StreamId<T>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final long DEFAULT_BUFFER_CAPACITY = 128;

    private final StreamId<T> target;
    private final Duration delay;
    private final long bufferCapacity;

    public static <T> DelayedStreamId<T> delayBy(StreamId<T> target, Duration delay) {
        return new DelayedStreamId<>(target, delay);
    }

    public DelayedStreamId(StreamId<T> target, Duration delay) {
        this(target, delay, DEFAULT_BUFFER_CAPACITY);
    }

    public DelayedStreamId(StreamId<T> target, Duration delay, long bufferCapacity) {
        this.target = requireNonNull(target, "target of the delay must not be null");
        this.delay = requireNonNull(delay, "delay must not be null");
        this.bufferCapacity = bufferCapacity;
    }

    public StreamId<T> getTarget() {
        return target;
    }

    public Duration getDelay() {
        return delay;
    }

    public long getBufferCapacity() {
        return bufferCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DelayedStreamId<?> that = (DelayedStreamId<?>) o;
        return bufferCapacity == that.bufferCapacity &&
                Objects.equals(target, that.target) &&
                Objects.equals(delay, that.delay);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, delay, bufferCapacity);
    }

    @Override
    public String toString() {
        return "DelayedStreamId{" +
                "target=" + target +
                ", delay=" + delay +
                ", bufferCapacity=" + bufferCapacity +
                '}';
    }
}
