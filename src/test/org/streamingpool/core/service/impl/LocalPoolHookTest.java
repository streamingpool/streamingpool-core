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

package org.streamingpool.core.service.impl;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.streamingpool.core.service.streamid.StreamingPoolHook.NEW_STREAM_HOOK;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.StreamingPoolHook;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

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
        Flowable.fromPublisher(newStreamHook()).take(1).subscribe(subscriber);

        pool.provide(anyStreamId, mock(Publisher.class));

        subscriber.awaitTerminalEvent(2, SECONDS);
        subscriber.assertValues(anyStreamId);
    }

    @Test
    public void noStreamIdEmittedIfNoStreamIsProvided() {
        TestSubscriber<StreamId<?>> subscriber = new TestSubscriber<>();
        Flowable.fromPublisher(newStreamHook()).subscribe(subscriber);

        subscriber.awaitTerminalEvent(1, SECONDS);
        subscriber.assertNoValues();
    }

    private Publisher<StreamId<?>> newStreamHook() {
        return pool.discover(NEW_STREAM_HOOK);
    }

}
