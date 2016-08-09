/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.CombineWithLatestStreamIdStreamFactoryTest;
import cern.streaming.pool.core.service.streamid.CombineWithLatestStreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;

/**
 * Factory for {@link CombineWithLatestStreamId}
 * 
 * @see CombineWithLatestStreamId
 * @see CombineWithLatestStreamIdStreamFactoryTest
 * @author acalia
 */
public class CombineWithLatestStreamIdStreamFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <D> ReactiveStream<D> create(StreamId<D> id, DiscoveryService discoveryService) {
        if (!(id instanceof CombineWithLatestStreamId)) {
            return null;
        }

        return combineWithLatestStream((CombineWithLatestStreamId<D, ?>) id);
    }

    private <D, T> ReactiveStream<D> combineWithLatestStream(CombineWithLatestStreamId<D, T> streamId) {
        Observable<D> data = ReactiveStreams.rxFrom(streamId.dataStream());
        Observable<T> trigger = ReactiveStreams.rxFrom(streamId.triggerStream());
        /*
         * The resulting Observable from withLatestFrom seems to not be compatible with rxjava-reactive-streams
         * adapters. Introducing an "useless" buffer in order to be able to transform to ReactiveStream interfaces with
         * the least minimum side effects. FIXME: the withLatestFrom operator is marked as @Experimental. Wait for the
         * promotion to stable
         */
        Observable<D> combinedWithLatest = trigger.withLatestFrom(data, (t, d) -> d).buffer(1)
                .flatMap(Observable::from);
        return ReactiveStreams.fromRx(combinedWithLatest);
    }

}
