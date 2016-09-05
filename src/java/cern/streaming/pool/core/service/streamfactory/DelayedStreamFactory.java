/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.Optional;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
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
    public <Y> Optional<ReactiveStream<Y>> create(StreamId<Y> id, DiscoveryService discoveryService) {
        if(!(id instanceof DelayedStreamId)) {
            return Optional.empty();
        }
        DelayedStreamId<Y> delayedId = (DelayedStreamId<Y>) id;
        Duration delay = delayedId.getDelay();
        StreamId<Y> target = delayedId.getTarget();
        return Optional.of(fromRx(rxFrom(discoveryService.discover(target)).delay(delay.toMillis(), MILLISECONDS)));
    }

}
