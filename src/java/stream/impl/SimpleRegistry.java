/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import stream.ReactStream;
import stream.DiscoveryService;
import stream.ProvidingService;
import stream.StreamId;

public class SimpleRegistry implements DiscoveryService, ProvidingService {

    ConcurrentMap<StreamId<?>, ReactStream<?>> map = new ConcurrentHashMap<>();

    @Override
    public <T> void provide(StreamId<T> id, ReactStream<T> obs) {
        if (map.containsKey(id)) {
            throw new IllegalArgumentException("ID already in the map");
        }
        map.put(id, obs);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {
        return (ReactStream<T>) map.get(id);
    }
}
