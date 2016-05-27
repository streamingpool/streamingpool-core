/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import stream.DiscoveryService;
import stream.ProvidingService;
import stream.ReactStream;
import stream.StreamId;

public class SimplePool implements DiscoveryService, ProvidingService {

    ConcurrentMap<StreamId<?>, ReactStream<?>> map = new ConcurrentHashMap<>();

    @Override
    public <T> void provide(StreamId<T> id, ReactStream<T> obs) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(obs, "stream must not be null!");

        ReactStream<?> oldValue = map.putIfAbsent(id, obs);
        if (oldValue != null) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {
        return (ReactStream<T>) map.get(id);
    }
    
    public void clearPool() {
        map.clear();
    }
}
