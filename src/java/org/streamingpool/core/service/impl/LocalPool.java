// @formatter:off
/**
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

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.ProvidingService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.TypedStreamFactory;

import com.google.common.collect.ImmutableList;

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
