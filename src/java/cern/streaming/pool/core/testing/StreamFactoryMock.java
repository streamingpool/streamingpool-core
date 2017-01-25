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

package cern.streaming.pool.core.testing;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.TypedStreamFactory;
import io.reactivex.Flowable;

/**
 * Very simple StreamFactory mock builder to simplify the test code.
 */
public class StreamFactoryMock<T> {
    private final Multimap<StreamId<T>, StreamId<T>> withIdDiscover;
    private final Map<StreamId<T>, T> withIdProvideStreamWithValue;
    private final Map<StreamId<T>, BiFunction<StreamId<T>, DiscoveryService, Publisher<T>>> withIdInvoke;

    private StreamFactoryMock() {
        this.withIdDiscover = HashMultimap.create();
        this.withIdProvideStreamWithValue = new HashMap<>();
        this.withIdInvoke = new HashMap<>();
    }

    /**
     * Start the creation of a new {@link StreamFactoryMock}.
     * 
     * @param factoryOfType the type of the values that this factory will deal with. This is only useful for the
     *            compiler to be able to infer the correct type of the {@link StreamFactoryMock}
     */
    public static <T> StreamFactoryMock<T> newFactory(Class<T> factoryOfType) {
        return new StreamFactoryMock<>();
    }

    /**
     * When the factory is asked to create {@code id}, it will use the {@link DiscoveryService} to discover
     * {@code idToDiscover}.
     * 
     * @param id the id that triggers the discovery
     * @param idToDiscover the id that will be discovered
     */
    public StreamFactoryMock<T> withIdDiscoverAnother(StreamId<T> id, StreamId<T> idToDiscover) {
        withIdDiscover.put(id, idToDiscover);
        return this;
    }

    /**
     * When the factory is asked to create {@code id}, a {@link ReactiveStream} that contains the {@code value} will be
     * provided.
     * 
     * @param id the id that triggers the stream creation
     * @param value the value that the created stream will contain
     */
    public StreamFactoryMock<T> withIdProvideStreamWithValue(StreamId<T> id, T value) {
        withIdProvideStreamWithValue.put(id, value);
        return this;
    }

    /**
     * When the factory is asked to create {@code id}, it will invoke the specified {@link BiFunction}. This gives the
     * power to provide custom behavior in tests, the {@link BiFunction} will receive the {@link StreamId} and a
     * {@link DiscoveryService} and must produce a {@link ReactiveStream}.
     * 
     * @param id the id that triggers the bifuction invocation
     * @param bifunction the function that will be invoked
     */
    public StreamFactoryMock<T> withIdInvoke(StreamId<T> id,
            BiFunction<StreamId<T>, DiscoveryService, Publisher<T>> bifunction) {
        withIdInvoke.put(id, bifunction);
        return this;
    }

    /**
     * End method that will actually create the mocked {@link TypedStreamFactory}
     */
    public StreamFactory build() {
        final StreamFactory factoryMock = mock(StreamFactory.class);
        when(factoryMock.create(any(), any())).thenAnswer(args -> {
            @SuppressWarnings("unchecked")
            StreamId<T> streamId = args.getArgumentAt(0, StreamId.class);
            DiscoveryService discovery = args.getArgumentAt(1, DiscoveryService.class);

            if (withIdDiscover.containsKey(streamId)) {
                return Optional.of(Flowable.merge(
                        withIdDiscover.get(streamId).stream().map(discovery::discover).collect(Collectors.toList())));
            }

            if (withIdProvideStreamWithValue.containsKey(streamId)) {
                return Optional.of(Flowable.just(withIdProvideStreamWithValue.get(streamId)));
            }

            if (withIdInvoke.containsKey(streamId)) {
                return Optional.of(withIdInvoke.get(streamId).apply(streamId, discovery));
            }

            return Optional.empty();
        });
        return factoryMock;
    }
}