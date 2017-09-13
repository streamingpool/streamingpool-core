// @formatter:off
/*
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

import static io.reactivex.Flowable.interval;
import static io.reactivex.Flowable.just;
import static io.reactivex.Flowable.merge;
import static io.reactivex.Flowable.never;
import static io.reactivex.Flowable.timer;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;
import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;
import org.streamingpool.core.conf.PoolConfiguration;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.impl.LocalPool;
import org.streamingpool.core.service.streamfactory.DelayedStreamFactory;
import org.streamingpool.core.service.streamfactory.OverlapBufferStreamFactory;
import org.streamingpool.core.service.streamid.BufferSpecification;
import org.streamingpool.core.service.streamid.BufferSpecification.EndStreamMatcher;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;

public class OverlapBufferStreamTest {

    private LocalPool pool;
    private TestSubscriber<List<Long>> testSubscriber;
    private PoolConfiguration poolConfiguration;
    private  TestScheduler testScheduler;

    @Before
    public void setUp() {
        OverlapBufferStreamFactory factory = new OverlapBufferStreamFactory();
        testScheduler = new TestScheduler();
        poolConfiguration = new PoolConfiguration(testScheduler);
        pool = new LocalPool(asList(factory, new DelayedStreamFactory()), poolConfiguration);
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void ifStartEmitsOnlyOnceBeforeDataStreamNeverEnds() throws InterruptedException {
        CountDownLatch sync = new CountDownLatch(1);

        ConnectableFlowable<?> sourceStream = just(0L).publish();
        ConnectableFlowable<?> startStream = just(new Object()).publish();

        sourceStream.buffer(startStream, opening -> never()).doOnTerminate(sync::countDown)
                .subscribe(System.out::println);

        sourceStream.connect();
        startStream.connect();

        sync.await(5, SECONDS);

        assertThat(sync.getCount()).isEqualTo(0L);
    }

    @Test
    public void dataStreamEndsBeforeEndStreamEmitsShouldBufferEverything() {
        StreamId<Long> sourceId = registerRx(interval(1, SECONDS, testScheduler).take(5));
        StreamId<Object> startId = registerRx(merge(just(new Object()).delay(2, SECONDS, testScheduler), never()));
        StreamId<Object> endId = registerRx(never());

        subscribe(OverlapBufferStreamId.of(sourceId,
                BufferSpecification.ofStartEnd(startId, Collections.singleton(EndStreamMatcher.endingOnEvery(endId)))));

        testScheduler.advanceTimeBy(10, DAYS);
        //OnNext event should not happen if the end trigger does not yield any value (and the start stream does not complete)
        testSubscriber.assertValueCount(0);

    }

    @Test
    public void dataStreamEndsBeforeStartStreamEmitsShouldNotEmitAnything() {
        StreamId<Long> sourceId = registerRx(interval(1, SECONDS, testScheduler).take(10));
        StreamId<Object> startId = registerRx(never());
        StreamId<Object> endId = registerRx(never());

        OverlapBufferStreamId<Long> buffer = OverlapBufferStreamId.of(sourceId,
                BufferSpecification.ofStartEnd(startId, Collections.singleton(EndStreamMatcher.endingOnEvery(endId))));

        subscribe(buffer);
        testScheduler.advanceTimeBy(365, TimeUnit.DAYS);
        testSubscriber.assertEmpty();
    }


    @Test
    public void bufferEndsStreamUsingDelayedStart() {
        Flowable<Object> startStream = interval(0,3, SECONDS, testScheduler).cast(Object.class);

        StreamId<Long> sourceId = registerRx(interval(1,1000, MILLISECONDS, testScheduler).take(10));
        StreamId<Object> startId = registerRx(startStream);
        StreamId<Object> endId = registerRx(startStream.delay(3, SECONDS, testScheduler));

        subscribe(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartEnd(startId, Collections.singleton(EndStreamMatcher.endingOnMatch(endId, Objects::equals)))));

        testScheduler.advanceTimeBy(3, SECONDS);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValueAt(0, v -> asList(0L, 1L, 2L).equals(v));
        testScheduler.advanceTimeBy(3, SECONDS);
        testSubscriber.assertValueCount(2);
        testSubscriber.assertValueAt(1,v -> asList(3L, 4L, 5L).equals(v));
        testScheduler.advanceTimeBy(3, SECONDS);
        testSubscriber.assertValueCount(3);
        testSubscriber.assertValueAt(2, v -> asList(6L, 7L, 8L).equals(v));
        testScheduler.advanceTimeBy(3, SECONDS);
        testSubscriber.assertValueCount(4);
        testSubscriber.assertValueAt(3, v -> asList(9L).equals(v));

    }


    @Test
    public void bufferEndsWithTimeout() {
        Flowable<Object> startStream = interval(0, 3, SECONDS, testScheduler).take(3).cast(Object.class);

        StreamId<Long> sourceId = registerRx(interval(0,1, SECONDS, testScheduler).take(10));
        StreamId<Object> startId = registerRx(startStream);
        StreamId<Object> endId = registerRx(never());

        Flowable<?> timeout = timer(5200, MILLISECONDS, testScheduler);

        subscribe(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartEndTimeout(startId, ImmutableSet.of(EndStreamMatcher.endingOnEvery(endId)), timeout)));

        testScheduler.advanceTimeBy(5300L, MILLISECONDS);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValueAt(0, v -> asList(0L, 1L, 2L, 3L, 4L, 5L).equals(v));

        testScheduler.advanceTimeBy(3100L, MILLISECONDS);
        testSubscriber.assertValueCount(2);
        testSubscriber.assertValueAt(1, v -> asList(3L, 4L, 5L, 6L, 7L, 8L).equals(v));

        testScheduler.advanceTimeBy(3100L, MILLISECONDS);
        testSubscriber.assertValueCount(3);
        testSubscriber.assertValueAt(2, v -> asList(6L, 7L, 8L, 9L).equals(v));

    }

    @Test
    public void bufferCompletelyOverlap() {
        StreamId<Long> sourceId = registerRx(interval(1, SECONDS, testScheduler).take(10));
        StreamId<Object> startId = registerRx(just(new Object(), new Object()).delay(5500, MILLISECONDS, testScheduler).onBackpressureBuffer());
        StreamId<Object> endId = registerRx(never());

        Flowable<?> timeout = timer(5200, MILLISECONDS, testScheduler);

        subscribe(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartEndTimeout(startId, ImmutableSet.of(EndStreamMatcher.endingOnEvery(endId)), timeout)));

        testScheduler.advanceTimeBy(11, SECONDS);
        testSubscriber.awaitCount(2);
        testSubscriber.assertValueCount(2);
       assertThat( testSubscriber.values().get(0)).containsExactlyElementsOf( testSubscriber.values().get(1));
    }

    private void subscribe(OverlapBufferStreamId<Long> bufferId) {
        Flowable.fromPublisher(pool.discover(bufferId)).subscribe(testSubscriber);
    }

    private <T> StreamId<T> registerRx(Flowable<T> stream) {
        @SuppressWarnings("unchecked")
        StreamId<T> id = mock(StreamId.class);
        pool.provide(id, stream);
        return id;
    }

}
