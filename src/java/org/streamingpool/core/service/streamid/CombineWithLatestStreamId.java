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

import java.util.function.BiFunction;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.CombineWithLatestStreamFactory;

/**
 * Given a data stream and a stream of triggering events, the resulting stream emits as soon as the trigger stream
 * emits. The emitted value is determined by the comining function, and can thus be computed from the emitted value of
 * the triggered stream and the latest emitted item of the data stream. stream at the moment of each triggering event
 * 
 * @see CombineWithLatestStreamFactory
 * @author acalia, caguiler
 * @param <T> Type of the stream which will trigger the emitting of a new element
 * @param <D> Type of the original data stream
 * @param <R> Type of the returned value (= type of the resulting stream)
 */
public class CombineWithLatestStreamId<T, D, R> implements StreamId<R> {

    private final StreamId<T> trigger;
    private final StreamId<D> data;
    private final BiFunction<T, D, R> combiner;

    private CombineWithLatestStreamId(StreamId<T> trigger, StreamId<D> data, BiFunction<T, D, R> combiner) {
        this.data = requireNonNull(data, "data stream must not be null");
        this.trigger = requireNonNull(trigger, "trigger stream must not be null");
        this.combiner = requireNonNull(combiner, "combiner must not be null");
    }

    public static <T, D> CombineWithLatestStreamId<T, D, D> dataPropagated(StreamId<T> trigger, StreamId<D> data) {
        return combine(trigger, data, (t, d) -> d);
    }

    public static <T, D, R> CombineWithLatestStreamId<T, D, R> combine(StreamId<T> trigger, StreamId<D> data,
            BiFunction<T, D, R> combiner) {
        return new CombineWithLatestStreamId<>(trigger, data, combiner);
    }

    public StreamId<D> dataStream() {
        return data;
    }

    public StreamId<T> triggerStream() {
        return trigger;
    }

    public BiFunction<T, D, R> combiner() {
        return combiner;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        result = prime * result + ((trigger == null) ? 0 : trigger.hashCode());
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
        CombineWithLatestStreamId<?, ?, ?> other = (CombineWithLatestStreamId<?, ?, ?>) obj;
        if (data == null) {
            if (other.data != null) {
                return false;
            }
        } else if (!data.equals(other.data)) {
            return false;
        }
        if (trigger == null) {
            if (other.trigger != null) {
                return false;
            }
        } else if (!trigger.equals(other.trigger)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CombineWithLatestStreamId [trigger=" + trigger + ", data=" + data + "]";
    }

}
