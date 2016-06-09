/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static cern.streaming.pool.core.util.ReactStreams.fromRx;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.interval;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamCreator;

public class InjectionStreamCreator implements StreamCreator<InjectionDomainObject> {

    @Override
    public ReactStream<InjectionDomainObject> createWith(DiscoveryService discoveryService) {
        return fromRx(interval(1, SECONDS).map(num -> new InjectionDomainObject("Injection number " + num)));
    }

}
