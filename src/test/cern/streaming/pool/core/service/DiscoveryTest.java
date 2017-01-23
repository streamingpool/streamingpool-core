/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.impl.LocalPool;
import cern.streaming.pool.core.testing.StreamFactoryMock;
import io.reactivex.Flowable;

/**
 * Test for the discovery of Streams using {@link TypedStreamFactory} and {@link LocalPool}.
 * 
 * @author acalia
 */
public class DiscoveryTest {
    private static final String ANY_VALUE = "Z";

    @Test(expected = CycleInStreamDiscoveryDetectedException.class)
    public void testDiscoveryLoop() {
        StreamId<String> id = mockStreamId();
        StreamFactory factory = StreamFactoryMock.newFactory(String.class).withIdDiscoverAnother(id, id).build();
        prepareDiscoveryService(singletonList(factory)).discover(id);
    }

    @Test
    public void testTwoFactoriesProvideTheSameId() {
        StreamId<String> id = mockStreamId();

        StreamFactory factoryA = StreamFactoryMock.newFactory(String.class).withIdProvideStreamWithValue(id, ANY_VALUE)
                .build();
        StreamFactory factoryB = StreamFactoryMock.newFactory(String.class).withIdProvideStreamWithValue(id, ANY_VALUE)
                .build();

        Publisher<String> result = prepareDiscoveryService(Arrays.asList(factoryA, factoryB)).discover(id);

        assertThat(toList(result)).hasSize(1).contains(ANY_VALUE);
    }

    @Test
    public void testDiscoveryXYZWithFactoryBuilder() {
        StreamId<String> idX = mockStreamId();
        StreamId<String> idY = mockStreamId();
        StreamId<String> idZ = mockStreamId();

        StreamFactory factoryA = StreamFactoryMock.newFactory(String.class).withIdDiscoverAnother(idY, idZ).build();

        StreamFactory factoryB = StreamFactoryMock.newFactory(String.class).withIdDiscoverAnother(idX, idY)
                .withIdProvideStreamWithValue(idZ, ANY_VALUE).build();

        Publisher<String> result = prepareDiscoveryService(Arrays.asList(factoryA, factoryB)).discover(idX);

        assertThat(toList(result)).containsExactly(ANY_VALUE);
    }

    @Test
    public void testComplexFactory() {
        StreamId<String> idX = mockStreamId();
        StreamId<String> idY1 = mockStreamId();
        StreamId<String> idY2 = mockStreamId();
        StreamId<String> idZ = mockStreamId();

        StreamFactory factoryA = StreamFactoryMock.newFactory(String.class).withIdDiscoverAnother(idX, idY1)
                .withIdDiscoverAnother(idX, idY2).build();

        StreamFactory factoryB = StreamFactoryMock.newFactory(String.class)
                .withIdProvideStreamWithValue(idY1, ANY_VALUE).withIdDiscoverAnother(idY2, idZ).build();

        StreamFactory factoryC = StreamFactoryMock.newFactory(String.class).withIdProvideStreamWithValue(idZ, ANY_VALUE)
                .build();

        Publisher<String> result = prepareDiscoveryService(Arrays.asList(factoryA, factoryB, factoryC)).discover(idX);

        assertThat(toList(result)).hasSize(2).containsExactly(ANY_VALUE, ANY_VALUE);
    }

    @SuppressWarnings("unchecked")
    private <T> StreamId<T> mockStreamId() {
        return mock(StreamId.class);
    }

    private DiscoveryService prepareDiscoveryService(final List<StreamFactory> factories) {
        return new LocalPool(factories);
    }

    private List<String> toList(Publisher<String> result) {
        return Lists.newArrayList(Flowable.fromPublisher(result).blockingIterable());
    }
}
