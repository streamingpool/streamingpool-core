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

package org.streamingpool.core.service;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.reactivestreams.Publisher;
import org.streamingpool.core.service.impl.LocalPool;
import org.streamingpool.core.testing.AbstractStreamTest;
import org.streamingpool.core.testing.StreamFactoryMock;

/**
 * Unit test for the case in which the factories discover streams using different threads, which is not allowed.
 */
public class MultiThreadLazyPoolTest extends AbstractStreamTest {

    private static final String ANY_VALUE = "ANY_STRING";

    @SuppressWarnings("unchecked")
    @Test(expected = RuntimeException.class)
    public void testFactoryRecursivlyDiscoverOnSpawnThread() {
        final StreamId<String> idA = mock(StreamId.class);
        final StreamId<String> idB = mock(StreamId.class);

        StreamFactory multiThreadFactory = StreamFactoryMock.newFactory(String.class)
                .withIdInvoke(idA, (streamId, recoursiveDiscoveryService) -> {
                    ExecutorService shouldNotDoAnExecutor = Executors.newSingleThreadExecutor();
                    Future<Publisher<String>> submission = shouldNotDoAnExecutor.submit(() -> {
                        return recoursiveDiscoveryService.discover(idB);
                    });

                    try {
                        return submission.get();
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                }).build();

        StreamFactory factoryForAnyValue = StreamFactoryMock.newFactory(String.class)
                .withIdProvideStreamWithValue(idB, ANY_VALUE).build();

        prepareDiscoveryService(multiThreadFactory, factoryForAnyValue).discover(idA);
    }

    private LocalPool prepareDiscoveryService(StreamFactory markerIdFactory, StreamFactory factoryForAnyValue) {
        return new LocalPool(Arrays.asList(markerIdFactory, factoryForAnyValue));
    }
}
