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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.impl.LocalPool;

/**
 * Standard unit tests covering {@link SimplePool}
 * 
 * @author mgalilee
 */
@SuppressWarnings("unchecked")
public class LocalPoolTest {

    private static final StreamId<Object> ID_A = mock(StreamId.class);
    private static final StreamId<Object> ID_B = mock(StreamId.class);
    private static final StreamId<Object> ID_NOT_PROVIDED = mock(StreamId.class);
    private static final Publisher<Object> STREAM_A = mock(Publisher.class);
    private static final Publisher<Object> STREAM_B = mock(Publisher.class);

    private LocalPool pool;

    @Before
    public void setUp() {
        pool = new LocalPool();
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

    @Test
    public void provideNewSupplier() {
        pool.provide(ID_B, STREAM_B);
        Publisher<Object> stream = pool.discover(ID_B);

        assertEquals(STREAM_B, stream);
    }

    @Test(expected = NullPointerException.class)
    public void discoverNullId() {
        pool.discover(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void discoverUnavailableStream() {
        pool.discover(ID_B);
    }

    @Test
    public void discoverAvailableStream() {
        Publisher<Object> stream = pool.discover(ID_A);

        assertEquals(STREAM_A, stream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClear() {
        pool.discover(ID_NOT_PROVIDED);
    }
}
