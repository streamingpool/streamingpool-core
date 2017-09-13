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

package org.streamingpool.core.service.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.junit.Test;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;
import org.streamingpool.core.conf.PoolConfiguration;
import org.streamingpool.core.service.CycleInStreamDiscoveryDetectedException;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.impl.IdentifiedStreamCreator;
import org.streamingpool.core.service.impl.ImmutableIdentifiedStreamCreator;
import org.streamingpool.core.service.impl.LocalPool;
import org.streamingpool.core.service.streamfactory.CreatorStreamFactory;
import org.streamingpool.core.testing.NamedStreamId;

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
    protected static final PoolConfiguration POOL_CONFIGURATION = new PoolConfiguration(Schedulers.from(Executors.newSingleThreadExecutor()));

    private final IdentifiedStreamCreator<Object> creator = ImmutableIdentifiedStreamCreator.of(ID_A,
            discovery -> STREAM_A);
    private final CreatorStreamFactory factory = new CreatorStreamFactory(Arrays.asList(creator));
    private final DiscoveryService discoveryService = new LocalPool(Arrays.asList(factory),
            POOL_CONFIGURATION);

    @Test(expected = CycleInStreamDiscoveryDetectedException.class)
    public void testCycleLoopDetectedUsingStreamCreators() {
        DiscoveryService loopingDiscoveryService = new LocalPool(Arrays.asList(createLoopCreatorStreamFactory()), POOL_CONFIGURATION);

        loopingDiscoveryService.discover(ID_A);
    }

    @Test
    public void createUnavailableStream() {
        assertFalse(factory.create(new NamedStreamId<>("mysterystream"), discoveryService).isPresent());
    }

    @Test
    public void createAvailableStream() {
        Publisher<?> stream = factory.create(ID_A, discoveryService).data();

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
        Publisher<?> stream = factory.create(ID_B, discoveryService).data();

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
