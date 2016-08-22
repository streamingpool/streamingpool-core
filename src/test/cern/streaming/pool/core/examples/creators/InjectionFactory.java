/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static java.util.concurrent.TimeUnit.SECONDS;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;

public class InjectionFactory <T> implements StreamFactory <T, StreamId<T>> {

    @Override
    public ReactiveStream<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!id.equals(InjectionIds.INJECTION_CONTROL_SYSTEM)) {
            return null;
        }

        return (ReactiveStream<T>) ReactiveStreams.fromRx(
                Observable.interval(1, SECONDS).map(num -> new InjectionDomainObject("Injection number " + num)));
    }

    @Override
    public boolean canCreate(StreamId<?> id) {
        return id != null;
    }

}
