/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ProvidingService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.TypedStreamFactory;

/**
 * Local pool for providing and discovery of {@link Publisher}s. (this class is both a {@link DiscoveryService} and a
 * {@link ProvidingService}). The most important feature of the {@link LocalPool} is that it supports the lazy creation
 * of the streams, specifically, they are created when discovered using {@link StreamFactory}s. When a {@link StreamId}
 * is discovered, the discovery is delegated to a new instance of {@link TrackKeepingDiscoveryService}. The
 * {@link TrackKeepingDiscoveryService} then tries to create the stream using the provided {@link TypedStreamFactory}s
 * if no matching {@link StreamId} has already been provided.
 */
public class LocalPool implements DiscoveryService, ProvidingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalPool.class);

    private final List<StreamFactory> factories;
    private final PoolContent content = new PoolContent();

    public LocalPool() {
        this(ImmutableList.of());
    }

    public LocalPool(List<StreamFactory> factories) {
        this.factories = ImmutableList.copyOf(factories);
        LOGGER.info("Available Stream Factories: " + factories);
    }

    @Override
    public <T> void provide(StreamId<T> id, Publisher<T> obs) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(obs, "stream must not be null!");

        boolean inserted = content.synchronousPutIfAbsent(id, () -> obs);
        if (!inserted) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

    @Override
    public <T> Publisher<T> discover(StreamId<T> id) {
        requireNonNull(id, "Cannot discover a null id");
        return new TrackKeepingDiscoveryService(factories, content).discover(id);
    }

}
