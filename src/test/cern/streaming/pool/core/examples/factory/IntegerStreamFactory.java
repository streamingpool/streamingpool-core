/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.factory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static rx.Observable.range;

import org.springframework.stereotype.Component;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.TypedStreamFactory;

@Component
public class IntegerStreamFactory implements TypedStreamFactory<Integer, IntegerRangeId> {

    @Override
    public ReactiveStream<Integer> createReactiveStream(IntegerRangeId id, DiscoveryService discoveryService) {
        int from = id.getFrom();
        int to = id.getTo();
        return fromRx(range(from, to - from));
    }
    
    @Override
    public Class<IntegerRangeId> streamIdClass() {
        return IntegerRangeId.class;
    }
}
