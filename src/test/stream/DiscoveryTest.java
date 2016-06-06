/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static stream.ReactStreams.fromRx;
import static stream.ReactStreams.rxFrom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import rx.Observable;
import stream.impl.CycleInStreamDiscoveryDetectedException;
import stream.impl.LazyPool;

/**
 * Test for the discovery of Streams using {@link StreamFactory} and {@link LazyPool}.
 * 
 * @author acalia
 */
public class DiscoveryTest {
    private static final String ANY_VALUE = "Z";

    @Test(expected = CycleInStreamDiscoveryDetectedException.class)
    public void testDiscoveryLoop() {
        StreamId<String> id = mockStreamId();

        StreamFactory factory = StreamFactoryMock.<String> newFactory().withIdDiscoverAnother(id, id).build();

        prepareDiscoveryService(singletonList(factory)).discover(id);
    }

    @Test
    public void testTwoFactoriesProvideTheSameId() {
        StreamId<String> id = mockStreamId();

        StreamFactory factoryA = StreamFactoryMock.<String> newFactory()
                .withIdProvideStreamWithValue(id, ANY_VALUE)
                .build();
        StreamFactory factoryB = StreamFactoryMock.<String> newFactory()
                .withIdProvideStreamWithValue(id, ANY_VALUE)
                .build();

        ReactStream<String> result = prepareDiscoveryService(Arrays.asList(factoryA, factoryB)).discover(id);

        assertThat(toList(result)).hasSize(1).contains(ANY_VALUE);
    }

    @Test
    public void testDiscoveryXYZWithFactoryBuilder() {
        StreamId<String> idX = mockStreamId();
        StreamId<String> idY = mockStreamId();
        StreamId<String> idZ = mockStreamId();

        StreamFactory factoryA = StreamFactoryMock.<String> newFactory()
                .withIdDiscoverAnother(idY, idZ).build();

        StreamFactory factoryB = StreamFactoryMock.<String> newFactory()
                .withIdDiscoverAnother(idX, idY)
                .withIdProvideStreamWithValue(idZ, ANY_VALUE).build();

        ReactStream<String> result = prepareDiscoveryService(Arrays.asList(factoryA, factoryB)).discover(idX);

        assertThat(toList(result)).containsExactly(ANY_VALUE);
    }

    @Test
    public void testComplexFactory() {
        StreamId<String> idX = mockStreamId();
        StreamId<String> idY1 = mockStreamId();
        StreamId<String> idY2 = mockStreamId();
        StreamId<String> idZ = mockStreamId();

        StreamFactory factoryA = StreamFactoryMock.<String> newFactory()
                .withIdDiscoverAnother(idX, idY1)
                .withIdDiscoverAnother(idX, idY2).build();

        StreamFactory factoryB = StreamFactoryMock.<String> newFactory()
                .withIdProvideStreamWithValue(idY1, ANY_VALUE)
                .withIdDiscoverAnother(idY2, idZ).build();

        StreamFactory factoryC = StreamFactoryMock.<String> newFactory()
                .withIdProvideStreamWithValue(idZ, ANY_VALUE)
                .build();

        ReactStream<String> result = prepareDiscoveryService(Arrays.asList(factoryA, factoryB, factoryC)).discover(idX);

        assertThat(toList(result)).hasSize(2).containsExactly(ANY_VALUE, ANY_VALUE);
    }

    @SuppressWarnings("unchecked")
    private <T> StreamId<T> mockStreamId() {
        return mock(StreamId.class);
    }

    private DiscoveryService prepareDiscoveryService(final List<StreamFactory> factories) {
        return new LazyPool(factories);
    }

    private List<String> toList(ReactStream<String> result) {
        return rxFrom(result).toList().toBlocking().single();
    }

    /**
     * Very simple StreamFactory mock builder to simplify the test code.
     */
    private static class StreamFactoryMock<T> {
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
}
