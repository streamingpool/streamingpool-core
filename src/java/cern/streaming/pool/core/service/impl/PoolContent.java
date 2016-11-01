/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.subjects.PublishSubject;

public class PoolContent {

    private final ConcurrentMap<StreamId<?>, ReactiveStream<?>> activeStreams = new ConcurrentHashMap<>();

    private final PublishSubject<StreamId<?>> newStreamHook = PublishSubject.create();
    private final ExecutorService hookExecutor = Executors.newSingleThreadExecutor();

    public <T> boolean synchronousPut(StreamId<T> id, Supplier<ReactiveStream<T>> supplier) {
        if (!activeStreams.containsKey(id)) {
            synchronized (activeStreams) {
                if (!activeStreams.containsKey(id)) {
                    ReactiveStream<T> reactStream = supplier.get();
                    if (reactStream != null) {
                        activeStreams.put(id, reactStream);
                        hookExecutor.submit(() -> newStreamHook.onNext(id));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> ReactiveStream<T> get(StreamId<T> id) {
        /* This cast is safe, because we only allow to add the right types into the map */
        return (ReactiveStream<T>) activeStreams.get(id);
    }

    public ReactiveStream<StreamId<?>> newStreamHook() {
        return ReactiveStreams.fromRx(newStreamHook);
    }
}