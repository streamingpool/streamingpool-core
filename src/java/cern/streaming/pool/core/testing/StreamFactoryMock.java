/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.testing;

import static cern.streaming.pool.core.service.ReactStreams.fromRx;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactStreams;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import rx.Observable;

/**
 * Very simple StreamFactory mock builder to simplify the test code.
 */
public class StreamFactoryMock<T> {
    private final Multimap<StreamId<T>, StreamId<T>> withIdDiscover = HashMultimap.create();
    private final Map<StreamId<T>, T> withIdProvideStreamWithValue = new HashMap<>();

    private StreamFactoryMock() {
    }

    public static <T> StreamFactoryMock<T> newFactory() {
        return new StreamFactoryMock<>();
    }

    public StreamFactoryMock<T> withIdDiscoverAnother(StreamId<T> id, StreamId<T> idToDiscover) {
        withIdDiscover.put(id, idToDiscover);
        return this;
    }

    public StreamFactoryMock<T> withIdProvideStreamWithValue(StreamId<T> id, T value) {
        withIdProvideStreamWithValue.put(id, value);
        return this;
    }

    public StreamFactory build() {
        final StreamFactory factoryMock = mock(StreamFactory.class);
        when(factoryMock.create(any(), any())).thenAnswer((args) -> {
            @SuppressWarnings("unchecked")
            StreamId<T> streamId = args.getArgumentAt(0, StreamId.class);
            DiscoveryService discovery = args.getArgumentAt(1, DiscoveryService.class);

            if (withIdDiscover.containsKey(streamId)) {
                return fromRx(Observable.merge(withIdDiscover.get(streamId).stream().map(discovery::discover)
                        .map(ReactStreams::rxFrom).collect(Collectors.toList())));
            }

            if (withIdProvideStreamWithValue.containsKey(streamId)) {
                return fromRx(Observable.just(withIdProvideStreamWithValue.get(streamId)));
            }

            return null;
        });
        return factoryMock;
    }
}