/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static cern.streaming.pool.core.util.ReactStreams.fromRx;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.interval;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.util.ReactStreams;
import rx.Observable;

public class InjectionFactory implements StreamFactory {

    @Override
    public <T> ReactStream<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if(!id.equals(InjectionIds.INJECTION_CONTROL_SYSTEM)) {
            return null;
        }
        
        return (ReactStream<T>) ReactStreams.fromRx(Observable.interval(1, SECONDS).map(num -> new InjectionDomainObject("Injection number " + num)));
    }

}
