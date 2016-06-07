/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static stream.ReactStreams.rxFrom;

import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import stream.impl.CycleInStreamDiscoveryDetectedException;
import stream.impl.LazyPool;
import testing.StreamFactoryMock;

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
}
