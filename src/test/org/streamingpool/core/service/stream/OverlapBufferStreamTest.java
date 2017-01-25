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

import static io.reactivex.Flowable.interval;
import static io.reactivex.Flowable.just;
import static io.reactivex.Flowable.merge;
import static io.reactivex.Flowable.never;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.impl.LocalPool;
import org.streamingpool.core.service.streamfactory.DelayedStreamFactory;
import org.streamingpool.core.service.streamfactory.OverlapBufferStreamFactory;
import org.streamingpool.core.service.streamid.BufferSpecification;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;
import org.streamingpool.core.service.streamid.BufferSpecification.EndStreamMatcher;

import com.google.common.collect.ImmutableSet;

import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.subscribers.TestSubscriber;

public class OverlapBufferStreamTest {

    private OverlapBufferStreamFactory factory;
    private LocalPool pool;

    @Before
    public void setUp() {
        factory = new OverlapBufferStreamFactory();
        pool = new LocalPool(Arrays.asList(factory, new DelayedStreamFactory()));
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
    public void dataStreamEndsBeforeEndStreamEmitsShouldBufferEverything() throws InterruptedException {
        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(5));
        StreamId<Object> startId = registerRx(merge(just(new Object()).delay(2, SECONDS), never()));
        StreamId<Object> endId = registerRx(never());

        OverlapBufferStreamId<Long> bufferId = OverlapBufferStreamId.of(sourceId,
                BufferSpecification.ofStartEnd(startId, Collections.singleton(EndStreamMatcher.endingOnEvery(endId))));

        CountDownLatch sync = new CountDownLatch(1);
        Flowable.fromPublisher(pool.discover(bufferId)).doOnNext(v -> sync.countDown()).subscribe();
        if (sync.await(1, SECONDS)) {
            fail("OnNext event should not happen if the end trigger does not yield any value");
        }
    }

    @Test
    public void dataStreamEndsBeforeStartStreamEmitsShouldNotEmitEnything() {
        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(10));
        StreamId<Object> startId = registerRx(never());
        StreamId<Object> endId = registerRx(never());

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId,
                BufferSpecification.ofStartEnd(startId, Collections.singleton(EndStreamMatcher.endingOnEvery(endId)))));

        assertThat(values).isEmpty();
    }

    @Test
    public void bufferEndsStreamUsingDelayedStart() {
        Flowable<Object> startStream = shiftedBy500Ms(ofObject(interval(3, SECONDS)));

        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(10));
        StreamId<Object> startId = registerRx(startStream);
        StreamId<Object> endId = registerRx(startStream.delay(3, SECONDS));

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartEnd(startId, Collections.singleton(EndStreamMatcher.endingOnMatch(endId, Objects::equals)))));

        assertThat(values).contains(Arrays.asList(3L, 4L, 5L));
        assertThat(values).contains(Arrays.asList(6L, 7L, 8L));
        assertThat(values).contains(Arrays.asList(9L));
    }

    @Test
    public void bufferEndsWithTimeout() {
        Flowable<Object> startStream = shiftedBy500Ms(ofObject(interval(3, SECONDS)).take(3));

        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(10));
        StreamId<Object> startId = registerRx(startStream);
        StreamId<Object> endId = registerRx(never());

        Duration timeout = Duration.ofSeconds(5);

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartEndTimeout(startId, ImmutableSet.of(EndStreamMatcher.endingOnEvery(endId)), timeout)));

        assertThat(values).contains(Arrays.asList(3L, 4L, 5L, 6L, 7L));
        assertThat(values).contains(Arrays.asList(6L, 7L, 8L, 9L));
        assertThat(values).contains(Arrays.asList(9L));
    }

    @Test
    public void bufferCompletelyOverlap() {
        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(10));
        StreamId<Object> startId = registerRx(shiftedBy500Ms(just(new Object(), new Object()).delay(5, SECONDS)));
        StreamId<Object> endId = registerRx(never());

        Duration timeout = Duration.ofSeconds(5);

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartEndTimeout(startId, ImmutableSet.of(EndStreamMatcher.endingOnEvery(endId)), timeout)));

        assertThat(values).hasSize(2);
        assertThat(values.get(0)).containsExactlyElementsOf(values.get(1));
    }

    private Flowable<Long> oneSecondIntervalOfLength(int length) {
        return interval(1, SECONDS).take(length);
    }

    private List<List<Long>> subscribeAndWait(OverlapBufferStreamId<Long> bufferId) {
        TestSubscriber<List<Long>> subscriber = TestSubscriber.create();
        CountDownLatch sync = new CountDownLatch(1);
        Flowable.fromPublisher(pool.discover(bufferId)).doOnTerminate(sync::countDown).subscribe(subscriber);
        try {
            sync.await();
        } catch (InterruptedException e) {
            /* Tests.. */
        }
        return subscriber.values();
    }

    private <T> StreamId<T> registerRx(Flowable<T> stream) {
        @SuppressWarnings("unchecked")
        StreamId<T> id = mock(StreamId.class);
        pool.provide(id, stream);
        return id;
    }

    /**
     * This is used to have the start and stop stream to not conflict with the data stream. Setting a 500 delay shift
     * should be ok for tests.
     */
    private <T> Flowable<T> shiftedBy500Ms(Flowable<T> source) {
        return source.delay(500, MILLISECONDS);
    }

    private Flowable<Object> ofObject(Flowable<?> source) {
        return source.cast(Object.class);
    }

}
