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

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.OverlapBufferStreamFactory;

/**
 * Provide an overlapping buffer for the specified source id. The buffer begins whenever {@link BufferSpecification#startId()} stream
 * emits an item and ends when one of the {@link BufferSpecification#endStreamMatchers()} } matches an end. Optionally, it is possible to
 * specify a timeout for the closing stream in case it does not emit items.
 * <p>
 * The behavior is very similar to the {@code  buffer(Observable<TOpening> bufferOpenings, Func1<TOpening, Observable
 * <TClosing>> bufferClosingSelector)} RxJava 1 operator.
 *
 * @see OverlapBufferStreamFactory
 * @see <a href="http://reactivex.io/RxJava/javadoc/rx/Observable.html#buffer(rx.Observable,%20rx.functions.Func1)">
 *      RxJava 1 buffer documentation</a>
 * @author acalia
 * @param <T> the type of the data stream
 */
public class OverlapBufferStreamId<T> implements StreamId<List<T>>, Serializable {
    private static final long serialVersionUID = 1L;
    private static final long DEFAULT_BUFFER_CAPACITY = 128;

    private final BufferSpecification bufferSpecification;
    private final StreamId<T> sourceId;
    private final long backpressureBufferCapacity;

    public static <T> OverlapBufferStreamId<T> of(StreamId<T> sourceId, BufferSpecification bufferSpecification) {
        return new OverlapBufferStreamId<>(sourceId, bufferSpecification, DEFAULT_BUFFER_CAPACITY);
    }

    public static <T> OverlapBufferStreamId<T> of(StreamId<T> sourceId, BufferSpecification bufferSpecification, long backpressureBufferCapacity) {
        return new OverlapBufferStreamId<>(sourceId, bufferSpecification, backpressureBufferCapacity);
    }

    private OverlapBufferStreamId(StreamId<T> sourceId, BufferSpecification bufferSpecification,
            long backpressureBufferCapacity) {
        this.bufferSpecification = bufferSpecification;
        this.sourceId = sourceId;
        this.backpressureBufferCapacity = backpressureBufferCapacity;
    }

    public StreamId<T> sourceId() {
        return sourceId;
    }

    public BufferSpecification bufferSpecification() {
        return bufferSpecification;
    }

    public long getBackpressureBufferCapacity() {
        return backpressureBufferCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OverlapBufferStreamId<?> that = (OverlapBufferStreamId<?>) o;
        return backpressureBufferCapacity == that.backpressureBufferCapacity &&
                Objects.equals(bufferSpecification, that.bufferSpecification) &&
                Objects.equals(sourceId, that.sourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bufferSpecification, sourceId, backpressureBufferCapacity);
    }

    @Override
    public String toString() {
        return "OverlapBufferStreamId{" +
                "bufferSpecification=" + bufferSpecification +
                ", sourceId=" + sourceId +
                ", backpressureBufferCapacity=" + backpressureBufferCapacity +
                '}';
    }
}
