/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;

import java.util.Optional;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.stream.CombineWithLatestStreamIdStreamTest;
import cern.streaming.pool.core.service.streamid.CombineWithLatestStreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;

/**
 * Factory for {@link CombineWithLatestStreamId}
 * 
 * @see CombineWithLatestStreamId
 * @see CombineWithLatestStreamIdStreamTest
 * @author acalia
 */
public class CombineWithLatestStreamFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <Y> Optional<ReactiveStream<Y>> create(StreamId<Y> id, DiscoveryService discoveryService) {
        if (!(id instanceof CombineWithLatestStreamId)) {
            return Optional.empty();
        }

        return Optional.of(combineWithLatestStream((CombineWithLatestStreamId<Y, ?>) id, discoveryService));
    }

    private <Y, T> ReactiveStream<Y> combineWithLatestStream(CombineWithLatestStreamId<Y, T> streamId,
            DiscoveryService discoveryService) {
        Observable<Y> data = rxFrom(discoveryService.discover(streamId.dataStream()));
        Observable<T> trigger = rxFrom(discoveryService.discover(streamId.triggerStream()));
        /*
         * The resulting Observable from withLatestFrom seems to not be compatible with rxjava-reactive-streams
         * adapters. Introducing an "useless" buffer in order to be able to transform to ReactiveStream interfaces with
         * the least minimum side effects. FIXME: the withLatestFrom operator is marked as @Experimental. Wait for the
         * promotion to stable
         */
        Observable<Y> combinedWithLatest = trigger.withLatestFrom(data, (t, d) -> d).buffer(1)
                .flatMap(Observable::from);
        return ReactiveStreams.fromRx(combinedWithLatest);
    }

}
