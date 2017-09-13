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

import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;
import org.streamingpool.core.conf.PoolConfiguration;
import org.streamingpool.core.service.StreamId;

/**
 * Standard unit tests covering {@link LocalPool}
 * 
 * @author mgalilee
 */
@SuppressWarnings("unchecked")
public class LocalPoolTest {

    private static final StreamId<Object> ID_A = mock(StreamId.class);
    private static final StreamId<Object> ID_B = mock(StreamId.class);
    private static final StreamId<Object> ID_NOT_PROVIDED = mock(StreamId.class);
    private static final Publisher<Object> STREAM_A = mock(Publisher.class);

    private LocalPool pool;

    @Before
    public void setUp() {
        pool = new LocalPool(Collections.emptyList(), new PoolConfiguration(Schedulers.from(Executors.newSingleThreadExecutor())));
        pool.provide(ID_A, STREAM_A);
    }

    @Test(expected = NullPointerException.class)
    public void provideWithNullId() {
        pool.provide(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void provideWithNullStream() {
        pool.provide(ID_A, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void provideDuplicateSupplier() {
        pool.provide(ID_A, STREAM_A);
    }


    @Test(expected = NullPointerException.class)
    public void discoverNullId() {
        pool.discover(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void discoverUnavailableStream() {
        pool.discover(ID_B);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClear() {
        pool.discover(ID_NOT_PROVIDED);
    }
}
