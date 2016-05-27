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
import stream.StreamFactory;
import stream.StreamId;

public class LazyStreamFactory implements LazyProvidingService, StreamFactory {

    private ConcurrentMap<StreamId<?>, Supplier<? extends ReactStream<?>>> suppliers = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> ReactStream<T> create(StreamId<T> newId) {
        return (ReactStream<T>) suppliers.get(newId).get();
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
