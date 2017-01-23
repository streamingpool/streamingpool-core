/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static io.reactivex.Flowable.interval;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamCreator;

public class InjectionStreamCreator implements StreamCreator<InjectionDomainObject> {

    @Override
    public Publisher<InjectionDomainObject> createWith(DiscoveryService discoveryService) {
        return interval(1, SECONDS).map(num -> new InjectionDomainObject("Injection number " + num));
    }

}
