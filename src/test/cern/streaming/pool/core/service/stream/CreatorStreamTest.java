/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Test;
import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.CycleInStreamDiscoveryDetectedException;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.IdentifiedStreamCreator;
import cern.streaming.pool.core.service.impl.ImmutableIdentifiedStreamCreator;
import cern.streaming.pool.core.service.impl.LocalPool;
import cern.streaming.pool.core.service.streamfactory.CreatorStreamFactory;
import cern.streaming.pool.core.testing.NamedStreamId;
import io.reactivex.Flowable;

@SuppressWarnings("unchecked")
/**
 * Standard unit tests covering {@link CreatorStreamFactory}
 * 
 * @author mgalilee
 */
public class CreatorStreamTest {

    private static final StreamId<Object> ID_A = mock(StreamId.class);
    private static final StreamId<Object> ID_B = mock(StreamId.class);
    private static final Publisher<Object> STREAM_A = mock(Flowable.class);
    private static final Publisher<Object> STREAM_B = mock(Flowable.class);

    private final IdentifiedStreamCreator<Object> creator = ImmutableIdentifiedStreamCreator.of(ID_A,
            discovery -> STREAM_A);
    private final CreatorStreamFactory factory = new CreatorStreamFactory(Arrays.asList(creator));
    private final DiscoveryService discoveryService = new LocalPool(Arrays.asList(factory));

    @Test(expected = CycleInStreamDiscoveryDetectedException.class)
    public void testCycleLoopDetectedUsingStreamCreators() {
        DiscoveryService loopingDiscoveryService = new LocalPool(Arrays.asList(createLoopCreatorStreamFactory()));

        loopingDiscoveryService.discover(ID_A);
    }

    @Test
    public void createUnavailableStream() {
        assertFalse(factory.create(new NamedStreamId<>("mysterystream"), discoveryService).isPresent());
    }

    @Test
    public void createAvailableStream() {
        Publisher<?> stream = factory.create(ID_A, discoveryService).get();

        assertEquals(STREAM_A, stream);
    }

    @Test(expected = NullPointerException.class)
    public void provideWithNullId() {
        factory.provide(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void provideWithNullSupplier() {
        factory.provide(ID_A, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void provideDuplicateSupplier() {
        factory.provide(ID_A, creator.getCreator());
    }

    @Test
    public void provideNewSupplier() {
        factory.provide(ID_B, discovery -> STREAM_B);
        Publisher<?> stream = factory.create(ID_B, discoveryService).get();

        assertEquals(STREAM_B, stream);
    }

    private CreatorStreamFactory createLoopCreatorStreamFactory() {
        IdentifiedStreamCreator<Object> streamCreatorWithLoop = ImmutableIdentifiedStreamCreator.of(ID_A, discovery -> {
            discovery.discover(ID_A);
            throw new RuntimeException("The test failed, a loop should be detected!");
        });
        return new CreatorStreamFactory(Arrays.asList(streamCreatorWithLoop));
    }
}
