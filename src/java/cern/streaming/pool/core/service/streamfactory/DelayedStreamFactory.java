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
public class DelayedStreamFactory <T> implements StreamFactory <T, DelayedStreamId<T>> {

    @Override
    public ReactiveStream<T> create(DelayedStreamId<T> id, DiscoveryService discoveryService) {
        Duration delay = id.getDelay();
        StreamId<T> target = id.getTarget();
        return fromRx(rxFrom(discoveryService.discover(target)).delay(delay.toMillis(), MILLISECONDS));
    }

    @Override
    public boolean canCreate(StreamId id) {
        return id instanceof DelayedStreamId;
    }
}
