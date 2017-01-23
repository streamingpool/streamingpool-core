/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static java.util.Optional.of;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Optional;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import io.reactivex.Flowable;

public class InjectionFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Publisher<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!id.equals(InjectionIds.INJECTION_CONTROL_SYSTEM)) {
            return null;
        }

        return of((Publisher<T>) Flowable.interval(1, SECONDS).map(num -> "Injection number " + num)
                .map(InjectionDomainObject::new));
    }

}
