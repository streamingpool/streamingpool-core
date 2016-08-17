/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;

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
    public <T> ReactiveStream<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof DelayedStreamId)) {
            return null;
        }

        DelayedStreamId<T> delayedId = (DelayedStreamId<T>) id;
        Duration delay = delayedId.getDelay();
        StreamId<T> target = delayedId.getTarget();

        return fromRx(rxFrom(discoveryService.discover(target)).delay(delay.toMillis(), MILLISECONDS));
    }

}
