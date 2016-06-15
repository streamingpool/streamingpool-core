/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;

import cern.streaming.pool.core.service.impl.LazyPool;
import cern.streaming.pool.core.testing.AbstractStreamTest;
import cern.streaming.pool.core.testing.StreamFactoryMock;

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
                    Future<ReactStream<String>> submission = shouldNotDoAnExecutor.submit(() -> {
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

    private LazyPool prepareDiscoveryService(StreamFactory markerIdFactory, StreamFactory factoryForAnyValue) {
        return new LazyPool(Arrays.asList(markerIdFactory, factoryForAnyValue));
    }
}
