/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static java.util.Optional.of;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.interval;

import java.util.Optional;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import rx.Observable;

public class InjectionFactory implements StreamFactory {

    @Override
    public <T> Optional<ReactiveStream<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!id.equals(InjectionIds.INJECTION_CONTROL_SYSTEM)) {
            return null;
        }

        Observable<InjectionDomainObject> rxStream = interval(1, SECONDS).map(num -> "Injection number " + num)
                .map(InjectionDomainObject::new);
        @SuppressWarnings("unchecked")
        ReactiveStream<T> reactiveStream = (ReactiveStream<T>) fromRx(rxStream);
        return of(reactiveStream);
    }

}
