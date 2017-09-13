// @formatter:off
/*
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package org.streamingpool.core.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.reactivestreams.Publisher;
import org.streamingpool.core.conf.PoolConfiguration;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.ProvidingService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamFactoryRegistry;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.TypedStreamFactory;

/**
 * Local pool for providing and discovery of {@link Publisher}s. (this class is both a {@link DiscoveryService} and a
 * {@link ProvidingService}). The most important feature of the {@link LocalPool} is that it supports the lazy creation
 * of the streams, specifically, they are created when discovered using {@link StreamFactory}s. When a {@link StreamId}
 * is discovered, the discovery is delegated to a new instance of {@link TrackKeepingDiscoveryService}. The
 * {@link TrackKeepingDiscoveryService} then tries to create the stream using the provided {@link TypedStreamFactory}s
 * if no matching {@link StreamId} has already been provided.
 */
public class LocalPool implements DiscoveryService, ProvidingService, StreamFactoryRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalPool.class);

    private final PoolConfiguration poolConfiguration;
    private final List<StreamFactory> factories;
    private final PoolContent content = new PoolContent();

    public LocalPool(List<StreamFactory> factories, PoolConfiguration poolConfiguration) {
        requireNonNull(factories,"Factories can not be null");
        this.factories = new CopyOnWriteArrayList<>(factories);
        LOGGER.info("Available Stream Factories: {}", factories);
        this.poolConfiguration = poolConfiguration;
    }

    @Override
    public <T> void provide(StreamId<T> id, Publisher<T> obs) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(obs, "stream must not be null!");

        boolean inserted = content.synchronousPutIfAbsent(id, () -> ErrorStreamPair.ofData(obs));
        if (!inserted) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

    @Override
    public <T> Publisher<T> discover(StreamId<T> id) {
        requireNonNull(id, "Cannot discover a null id");
        return new TrackKeepingDiscoveryService(factories, content, poolConfiguration).discover(id);
    }

    @Override
    public void addIntercept(StreamFactory interceptFactory) {
        factories.add(0, interceptFactory);
        LOGGER.info("Intercept {} has been added to the factories", interceptFactory);
    }

    @Override
    public void addFallback(StreamFactory fallbackFactory) {
        factories.add(factories.size(), fallbackFactory);
        LOGGER.info("Fallback {} has been added to the factories", fallbackFactory);
    }

}
