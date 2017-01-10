/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamfactory.DelayedStreamFactory;
import cern.streaming.pool.core.service.streamid.DelayedStreamId;
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
        return factory.create(delayedId, mockDiscoveryService()).get();
    }

    private DiscoveryService mockDiscoveryService() {
        DiscoveryService discoveryService = mock(DiscoveryService.class);
        when(discoveryService.discover(Matchers.eq(SOURCE_STREAM_ID))).thenReturn(SOURCE_STREAM);
        return discoveryService;
    }

}
