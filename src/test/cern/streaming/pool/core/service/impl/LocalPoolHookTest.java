/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static cern.streaming.pool.core.service.streamid.StreamingPoolHook.NEW_STREAM_HOOK;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.observers.TestSubscriber;

public class LocalPoolHookTest {

    private LocalPool pool;

    @Before
    public void setUp() {
        this.pool = new LocalPool();
    }

    @Test
    public void newStreamHookExists() {
        assertThat(streamHook()).isNotNull();
    }

    
    @Test
    public void registeringAStreamEmitsId() {
        StreamId<?> mockedStreamId = mock(StreamId.class);
        TestSubscriber<StreamId<?>> subscriber = new TestSubscriber<>();
        ReactiveStreams.rxFrom(streamHook()).take(1).subscribe(subscriber);
        
        pool.provide(mockedStreamId, mock(ReactiveStream.class));
        
        subscriber.awaitTerminalEvent(2, SECONDS);
        subscriber.assertValues(mockedStreamId );
    }
    
    
    private ReactiveStream<StreamId<?>> streamHook() {
        return pool.discover(NEW_STREAM_HOOK);
    }
    

}
