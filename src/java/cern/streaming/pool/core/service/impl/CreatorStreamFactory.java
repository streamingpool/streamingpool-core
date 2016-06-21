/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cern.streaming.pool.core.service.CreatorProvidingService;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;

/**
 * {@link StreamFactory} specifically designed to create {@link ReactiveStream}s using {@link StreamCreator}s. In order
 * to use the right {@link StreamCreator} for creating the {@link ReactiveStream}, it uses
 * {@link ImmutableIdentifiedStreamCreator} to map a specific {@link StreamId} to the correspondent {@link StreamCreator}.
 * 
 * @see StreamCreator
 * @see ImmutableIdentifiedStreamCreator
 */
public class CreatorStreamFactory implements CreatorProvidingService, StreamFactory {

    private final ConcurrentMap<StreamId<?>, StreamCreator<?>> suppliers = new ConcurrentHashMap<>();

    public CreatorStreamFactory(Iterable<IdentifiedStreamCreator<?>> identifiedCreators) {
        requireNonNull(identifiedCreators, "identifiedStreamCreators must not be null.");

        identifiedCreators.forEach(this::register);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ReactiveStream<T> create(StreamId<T> newId, DiscoveryService discoveryService) {
        StreamCreator<?> streamCreator = suppliers.get(newId);
        if (streamCreator == null) {
            return null;
        }
        return (ReactiveStream<T>) streamCreator.createWith(discoveryService);
    }

    @Override
    public <T> void provide(StreamId<T> id, StreamCreator<T> streamSupplier) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(streamSupplier, "stream suplier must not be null!");

        StreamCreator<?> existingCreator = suppliers.putIfAbsent(id, streamSupplier);
        if (existingCreator != null) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

    private <T> void register(IdentifiedStreamCreator<T> identifiedCreator) {
        suppliers.put(identifiedCreator.getId(), identifiedCreator.getCreator());
    }
}
