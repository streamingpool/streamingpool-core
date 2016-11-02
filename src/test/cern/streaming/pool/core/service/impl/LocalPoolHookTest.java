/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static cern.streaming.pool.core.service.streamid.StreamingPoolHook.NEW_STREAM_HOOK;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.StreamingPoolHook;
import rx.observers.TestSubscriber;

/**
 * Testing the behavior of new {@link StreamingPoolHook} hooks.
 */
public class LocalPoolHookTest {

    private LocalPool pool;

    @Before
    public void setUp() {
        this.pool = new LocalPool();
    }

    @Test
    public void newStreamHookExists() {
        assertThat(newStreamHook()).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void registeringAStreamEmitsId() {
        StreamId<?> anyStreamId = mock(StreamId.class);
        TestSubscriber<StreamId<?>> subscriber = new TestSubscriber<>();
        rxFrom(newStreamHook()).take(1).subscribe(subscriber);

        pool.provide(anyStreamId, mock(ReactiveStream.class));

        subscriber.awaitTerminalEvent(2, SECONDS);
        subscriber.assertValues(anyStreamId);
    }

    @Test
    public void noStreamIdEmittedIfNoStreamIsProvided() {
        TestSubscriber<StreamId<?>> subscriber = new TestSubscriber<>();
        rxFrom(newStreamHook()).subscribe(subscriber);

        subscriber.awaitTerminalEvent(1, SECONDS);
        subscriber.assertNoValues();
    }

    private ReactiveStream<StreamId<?>> newStreamHook() {
        return pool.discover(NEW_STREAM_HOOK);
    }

}
