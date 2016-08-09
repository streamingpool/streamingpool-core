/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static rx.Observable.just;

import java.time.Duration;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;

public class DelayedStreamIdStreamFactoryTest {
    private static final int SOURCE_VALUE = 1;
    @SuppressWarnings("unchecked")
    private static final StreamId<Integer> SOURCE_STREAM_ID = mock(StreamId.class);
    private static final ReactiveStream<Integer> SOURCE_STREAM = fromRx(just(SOURCE_VALUE));

    private DelayedStreamIdStreamFactory factory;
    private BlockingTestSubscriber<Integer> subscriber;

    @Before
    public void setUp() {
        factory = new DelayedStreamIdStreamFactory();
        subscriber = BlockingTestSubscriber.ofName("Subscriber");
    }
    
    @Test
    public void testThatTheStreamIsDelayedAtLeastByTheSpecfiedTime() {
        long delay = 2000;
        long deltaDelay = 500;

        StreamId<Integer> delayedId = DelayedStreamId.of(SOURCE_STREAM_ID, Duration.ofMillis(delay));
        publisherFrom(delayedId).subscribe(subscriber);

        Instant before = Instant.now();
        subscriber.await();
        Instant after = Instant.now();

        assertThat(subscriber.getValues()).containsOnly(SOURCE_VALUE);
        assertThat(Duration.between(before, after).toMillis()).isBetween(delay - deltaDelay, delay + deltaDelay);
    }

    private Publisher<Integer> publisherFrom(StreamId<Integer> delayedId) {
        return ReactiveStreams.publisherFrom(factory.create(delayedId, mockDiscoveryService()));
    }

    private DiscoveryService mockDiscoveryService() {
        DiscoveryService discoveryService = mock(DiscoveryService.class);
        when(discoveryService.discover(Matchers.eq(SOURCE_STREAM_ID))).thenReturn(SOURCE_STREAM);
        return discoveryService;
    }

}
