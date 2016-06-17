/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableList;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ProvidingService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;

/**
 * Local poll for the providing and discovery of {@link ReactiveStream} (this class is both a {@link DiscoveryService}
 * and a {@link ProvidingService}). The most important feature of the {@link LocalPool} is that it supports the lazy
 * creation of the streams, specifically, they are created when discovered using {@link StreamFactory}s. When a
 * {@link StreamId} is discovered, the discovery is delegated to a new instance of {@link TrackKeepingDiscoveryService}.
 * The {@link TrackKeepingDiscoveryService} then tries to create the stream using the provided {@link StreamFactory}s if
 * no matching {@link StreamId} has already been provided.
 */
public class LocalPool implements DiscoveryService, ProvidingService {

    private final List<StreamFactory> factories;
    private final ConcurrentMap<StreamId<?>, ReactiveStream<?>> activeStreams = new ConcurrentHashMap<>();

    public LocalPool() {
        this(ImmutableList.of());
    }

    public LocalPool(List<StreamFactory> factories) {
        this.factories = ImmutableList.copyOf(factories);
    }

    @Override
    public <T> void provide(StreamId<T> id, ReactiveStream<T> obs) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(obs, "stream must not be null!");

        ReactiveStream<?> oldValue = activeStreams.putIfAbsent(id, obs);
        if (oldValue != null) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

    @Override
    public <T> ReactiveStream<T> discover(StreamId<T> id) {
        return new TrackKeepingDiscoveryService(factories, activeStreams).discover(id);
    }

}
