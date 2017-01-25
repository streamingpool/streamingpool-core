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

import static org.streamingpool.core.service.streamid.StreamingPoolHook.NEW_STREAM_HOOK;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.StreamingPoolHook;

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

    public <T> boolean synchronousPutIfAbsent(StreamId<T> id, Supplier<Publisher<T>> supplier) {
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