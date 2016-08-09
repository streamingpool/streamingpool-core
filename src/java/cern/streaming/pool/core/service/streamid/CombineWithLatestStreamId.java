/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import java.util.Objects;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.CombineWithLatestStreamIdStreamFactoryTest;
import cern.streaming.pool.core.service.streamfactory.CombineWithLatestStreamIdStreamFactory;

/**
 * Given a data stream and a stream of triggering events, the resulting stream emits the latest element of the data
 * stream at the moment of each triggering event
 * 
 * @see CombineWithLatestStreamIdStreamFactory
 * @see CombineWithLatestStreamIdStreamFactoryTest
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
        super();
        this.trigger = Objects.requireNonNull(trigger);
        this.data = Objects.requireNonNull(data);
    }

    public ReactiveStream<D> dataStream() {
        return data;
    }

    public ReactiveStream<T> triggerStream() {
        return trigger;
    }
}
