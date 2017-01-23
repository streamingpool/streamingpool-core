/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static io.reactivex.Flowable.fromPublisher;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.Optional;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.DelayedStreamId;

/**
 * Factory for {@link DelayedStreamId}
 * 
 * @see DelayedStreamId
 * @author acalia
 */
public class DelayedStreamFactory implements StreamFactory {

    @Override
    public <Y> Optional<Publisher<Y>> create(StreamId<Y> id, DiscoveryService discoveryService) {
        if (!(id instanceof DelayedStreamId)) {
            return Optional.empty();
        }
        DelayedStreamId<Y> delayedId = (DelayedStreamId<Y>) id;
        Duration delay = delayedId.getDelay();
        StreamId<Y> target = delayedId.getTarget();
        return Optional.of(fromPublisher(discoveryService.discover(target)).delay(delay.toMillis(), MILLISECONDS));
    }

}
