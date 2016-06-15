/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Test;

import cern.streaming.pool.core.exception.CycleInStreamDiscoveryDetectedException;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;

public class CreatorStreamFactoryTest {

    @SuppressWarnings("unchecked")
    private static final StreamId<Integer> ID_A = mock(StreamId.class);

    @Test(expected = CycleInStreamDiscoveryDetectedException.class)
    public void testCycleLoopDetectedUsingStreamCreators() {
        DiscoveryService discoveryService = prepareDiscoveryServiceWithFactories(createLoopCreatorStreamFactory());
        discoveryService.discover(ID_A);
    }

    private LazyPool prepareDiscoveryServiceWithFactories(StreamFactory... factories) {
        return new LazyPool(Arrays.asList(factories));
    }

    private CreatorStreamFactory createLoopCreatorStreamFactory() {
        IdentifiedStreamCreator<Integer> streamCreatorWithLoop = IdentifiedStreamCreator.of(ID_A, (discovery) -> {
            discovery.discover(ID_A);
            throw new RuntimeException();
        });
        return new CreatorStreamFactory(Arrays.asList(streamCreatorWithLoop));
    }
}
