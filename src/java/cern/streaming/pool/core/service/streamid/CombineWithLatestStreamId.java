/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.stream.CombineWithLatestStreamIdStreamTest;
import cern.streaming.pool.core.service.streamfactory.CombineWithLatestStreamFactory;

/**
 * Given a data stream and a stream of triggering events, the resulting stream emits the latest element of the data
 * stream at the moment of each triggering event
 * 
 * @see CombineWithLatestStreamFactory
 * @see CombineWithLatestStreamIdStreamTest
 * @author acalia, caguiler
 * @param <D> Type of the original data stream
 * @param <T> Type of the trigger (not really relevant)
 */
public class CombineWithLatestStreamId<D, T> implements StreamId<D> {

    private final ReactiveStream<T> trigger;
    private final ReactiveStream<D> data;

    public static <D, T> CombineWithLatestStreamId<D, T> of(ReactiveStream<D> data, ReactiveStream<T> trigger) {
        return new CombineWithLatestStreamId<>(data, trigger);
    }

    private CombineWithLatestStreamId(ReactiveStream<D> data, ReactiveStream<T> trigger) {
        this.data = requireNonNull(data, "data stream must not be null");
        this.trigger = requireNonNull(trigger, "trigger stream must not be null");
    }

    public ReactiveStream<D> dataStream() {
        return data;
    }

    public ReactiveStream<T> triggerStream() {
        return trigger;
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
        CombineWithLatestStreamId<?, ?> other = (CombineWithLatestStreamId<?, ?>) obj;
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
