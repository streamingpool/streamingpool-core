/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;

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
    private static final ReactiveStream<Object> STREAM_A = mock(ReactiveStream.class);
    private static final ReactiveStream<Object> STREAM_B = mock(ReactiveStream.class);

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
        ReactiveStream<Object> stream = pool.discover(ID_B);

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
        ReactiveStream<Object> stream = pool.discover(ID_A);

        assertEquals(STREAM_A, stream);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClear() {
        pool.discover(ID_NOT_PROVIDED);
    }
}
