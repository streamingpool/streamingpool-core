/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import java.util.Optional;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.CombineWithLatestStreamId;
import io.reactivex.Flowable;

/**
 * Factory for {@link CombineWithLatestStreamId}
 * 
 * @see CombineWithLatestStreamId
 * @author acalia
 */
public class CombineWithLatestStreamFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <Y> Optional<Publisher<Y>> create(StreamId<Y> id, DiscoveryService discoveryService) {
        if (!(id instanceof CombineWithLatestStreamId)) {
            return Optional.empty();
        }

        return Optional.of(combineWithLatestStream((CombineWithLatestStreamId<?, ?, Y>) id, discoveryService));
    }

    private <T, D, Y> Publisher<Y> combineWithLatestStream(CombineWithLatestStreamId<T, D, Y> streamId,
            DiscoveryService discoveryService) {
        Flowable<D> data = Flowable.fromPublisher(discoveryService.discover(streamId.dataStream()));
        Flowable<T> trigger = Flowable.fromPublisher(discoveryService.discover(streamId.triggerStream()));

        return trigger.withLatestFrom(data, streamId.combiner()::apply);
    }

}
