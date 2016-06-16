/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cern.streaming.pool.core.service.CreatorProvidingService;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;

public class CreatorStreamFactory implements CreatorProvidingService, StreamFactory {

    private final ConcurrentMap<StreamId<?>, StreamCreator<?>> suppliers = new ConcurrentHashMap<>();

    public CreatorStreamFactory(Iterable<IdentifiedStreamCreator<?>> identifiedCreators) {
        Objects.requireNonNull(identifiedCreators, "identifiedStreamCreators must not be null.");
        for (IdentifiedStreamCreator<?> identifiedCreator : identifiedCreators) {
            provide(identifiedCreator);
        }
    }

    private <T> void provide(IdentifiedStreamCreator<T> identifiedCreator) {
        suppliers.put(identifiedCreator.getId(), identifiedCreator.getCreator());
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

}
