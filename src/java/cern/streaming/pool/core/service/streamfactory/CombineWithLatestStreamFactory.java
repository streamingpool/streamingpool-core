/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
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
public class CombineWithLatestStreamFactory <D> implements StreamFactory<D, CombineWithLatestStreamId<D, ?>> {

    @Override
    public ReactiveStream<D> create(CombineWithLatestStreamId<D, ?> id, DiscoveryService discoveryService) {
        return combineWithLatestStream(id);
    }

    private ReactiveStream<D> combineWithLatestStream(CombineWithLatestStreamId<D, ?> streamId) {
        Observable<D> data = ReactiveStreams.rxFrom(streamId.dataStream());
        Observable<?> trigger = ReactiveStreams.rxFrom(streamId.triggerStream());
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
