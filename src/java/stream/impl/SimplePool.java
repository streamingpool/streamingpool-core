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

    private final ConcurrentMap<StreamId<?>, ReactStream<?>> activeStreams = new ConcurrentHashMap<>();

    @Override
    public <T> void provide(StreamId<T> id, ReactStream<T> obs) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(obs, "stream must not be null!");

        ReactStream<?> oldValue = activeStreams.putIfAbsent(id, obs);
        if (oldValue != null) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {
        requireNonNull(id, "id must not be null");

        ReactStream<T> stream = (ReactStream<T>) activeStreams.get(id);
        if (stream == null) {
            throw new IllegalArgumentException("Stream of id '" + id + "' does not exist.");
        }
        return stream;
    }

    public void clearPool() {
        activeStreams().clear();
    }

    protected ConcurrentMap<StreamId<?>, ReactStream<?>> activeStreams() {
        return activeStreams;
    }
}
