/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.factory;

import static io.reactivex.Flowable.range;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.TypedStreamFactory;

@Component
public class IntegerStreamFactory implements TypedStreamFactory<Integer, IntegerRangeId> {

    @Override
    public Publisher<Integer> createReactiveStream(IntegerRangeId id, DiscoveryService discoveryService) {
        int from = id.getFrom();
        int to = id.getTo();
        return range(from, to - from);
    }

    @Override
    public Class<IntegerRangeId> streamIdClass() {
        return IntegerRangeId.class;
    }
}
