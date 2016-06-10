/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.factory;

import static cern.streaming.pool.core.util.ReactStreams.fromRx;
import static rx.Observable.range;

import org.springframework.stereotype.Component;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;

@Component
public class IntegerStreamFactory implements StreamFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> ReactStream<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof IntegerId)) {
            return null;
        }
        
        IntegerId integerId = (IntegerId) id;
        int from = integerId.getFrom();
        int to = integerId.getTo();
  
        return (ReactStream<T>) fromRx(range(from, to - from));
    }

}
