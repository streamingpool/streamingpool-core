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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.DelayedStreamFactory;
import org.streamingpool.core.service.streamid.DelayedStreamId;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

public class DelayedStreamIdStreamTest {
    private static final int SOURCE_VALUE = 1;
    @SuppressWarnings("unchecked")
    private static final StreamId<Integer> SOURCE_STREAM_ID = mock(StreamId.class);
    private static final Publisher<Integer> SOURCE_STREAM = Flowable.just(SOURCE_VALUE);

    private DelayedStreamFactory factory;
    private TestSubscriber<Integer> subscriber;

    @Before
    public void setUp() {
        factory = new DelayedStreamFactory();
        subscriber = TestSubscriber.create();
    }

    @Test
    public void testThatTheStreamIsDelayedAtLeastByTheSpecfiedTime() throws InterruptedException {
        long delay = 2000;
        long deltaDelay = 500;

        DelayedStreamId<Integer> delayedId = DelayedStreamId.delayBy(SOURCE_STREAM_ID, Duration.ofMillis(delay));
        publisherFrom(delayedId).subscribe(subscriber);

        Instant before = Instant.now();
        subscriber.await();
        Instant after = Instant.now();

        assertThat(subscriber.values()).containsOnly(SOURCE_VALUE);
        assertThat(Duration.between(before, after).toMillis()).isBetween(delay - deltaDelay, delay + deltaDelay);
    }

    private Publisher<Integer> publisherFrom(DelayedStreamId<Integer> delayedId) {
        return factory.create(delayedId, mockDiscoveryService()).data();
    }

    private DiscoveryService mockDiscoveryService() {
        DiscoveryService discoveryService = mock(DiscoveryService.class);
        when(discoveryService.discover(Mockito.eq(SOURCE_STREAM_ID))).thenReturn(SOURCE_STREAM);
        return discoveryService;
    }

}
