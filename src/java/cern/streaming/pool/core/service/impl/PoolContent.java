/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static cern.streaming.pool.core.service.streamid.StreamingPoolHook.NEW_STREAM_HOOK;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.StreamingPoolHook;
import io.reactivex.processors.PublishProcessor;

/**
 * Encapsulate the state of a streaming pool.
 * 
 * @author acalia, kfuchsbe, mihostet
 */
public class PoolContent {

    private final ConcurrentMap<StreamId<?>, Publisher<?>> activeStreams = new ConcurrentHashMap<>();
    private final PublishProcessor<StreamId<?>> newStreamHook = PublishProcessor.create();
    private final ExecutorService hookExecutor = Executors.newSingleThreadExecutor();

    public PoolContent() {
        addStreamHooks();
    }

    public <T> boolean synchronousPut(StreamId<T> id, Supplier<Publisher<T>> supplier) {
        if (!activeStreams.containsKey(id)) {
            synchronized (activeStreams) {
                if (!activeStreams.containsKey(id)) {
                    Publisher<T> reactStream = supplier.get();
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
    public <T> Publisher<T> get(StreamId<T> id) {
        /* This cast is safe, because we only allow to add the right types into the map */
        return (Publisher<T>) activeStreams.get(id);
    }

    /**
     * Directly add the {@link StreamingPoolHook}s as active streams (without triggering any hook)
     */
    private void addStreamHooks() {
        activeStreams.put(NEW_STREAM_HOOK, newStreamHook);
    }
}