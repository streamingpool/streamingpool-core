/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import stream.LazyProvidingService;
import stream.ReactStream;
import stream.StreamId;

public class LazyPool extends SimplePool implements LazyProvidingService {

    private ConcurrentMap<StreamId<?>, Supplier<? extends ReactStream<?>>> suppliers = new ConcurrentHashMap<>();

    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {

        /* This cast is safe, because we only allow to add the right types into the map */
        @SuppressWarnings("unchecked")
        ReactStream<T> activeStream = (ReactStream<T>) activeStreams().computeIfAbsent(id,
                (newId) -> suppliers.get(newId).get());

        if (activeStream == null) {
            throw new IllegalArgumentException(
                    "The stream for id " + id + "is neither present nor can it be created by any supplier.");
        }
        return activeStream;
    }

    @Override
    public <T> void provide(StreamId<T> id, Supplier<ReactStream<T>> streamSupplier) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(streamSupplier, "stream suplier must not be null!");

        Supplier<?> existingSupplier = suppliers.putIfAbsent(id, streamSupplier);
        if (existingSupplier != null) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

}
