/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.stream;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.publisherFrom;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static rx.Observable.interval;
import static rx.Observable.just;
import static rx.Observable.merge;
import static rx.Observable.never;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.LocalPool;
import cern.streaming.pool.core.service.streamfactory.OverlapBufferStreamFactory;
import cern.streaming.pool.core.service.streamid.BufferSpecification;
import cern.streaming.pool.core.service.streamid.BufferSpecification.EndStreamMatcher;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;
import rx.Observable;
import rx.observables.ConnectableObservable;

public class OverlapBufferStreamTest {

    private OverlapBufferStreamFactory factory;
    private LocalPool pool;

    @Before
    public void setUp() {
        factory = new OverlapBufferStreamFactory();
        pool = new LocalPool(Arrays.asList(factory));
    }

    @Test
    public void ifStartEmitsOnlyOnceBeforeDataStreamNeverEnds() throws InterruptedException {
        CountDownLatch sync = new CountDownLatch(1);

        ConnectableObservable<?> sourceStream = just(0L).publish();
        ConnectableObservable<?> startStream = just(new Object()).publish();

        sourceStream.buffer(startStream, opening -> never()).doOnTerminate(sync::countDown)
                .subscribe(System.out::println);

        sourceStream.connect();
        startStream.connect();

        sync.await(5, SECONDS);

        assertThat(sync.getCount()).isEqualTo(0L);
    }

    @Test
    public void dataStreamEndsBeforeEndStreamEmitsShouldBufferEverything() {
        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(5));
        StreamId<Object> startId = registerRx(merge(just(new Object()).delay(2, SECONDS), never()));
        StreamId<Object> endId = registerRx(never());

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartAndEnd(startId, Collections.singleton(EndStreamMatcher.alwaysEndingOn(endId)))));

        assertThat(values).hasSize(1);
        assertThat(values.get(0)).containsExactly(2L, 3L, 4L);
    }

    @Test
    public void dataStreamEndsBeforeStartStreamEmitsShouldNotEmitEnything() {
        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(10));
        StreamId<Object> startId = registerRx(never());
        StreamId<Object> endId = registerRx(never());

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartAndEnd(startId, Collections.singleton(EndStreamMatcher.alwaysEndingOn(endId)))));

        assertThat(values).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bufferEndsStreamUsingDelayedStart() {
        Observable<Object> startStream = shiftedBy500Ms(ofObject(interval(3, SECONDS)));

        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(10));
        StreamId<Object> startId = registerRx(startStream);
        StreamId<Object> endId = registerRx(startStream.delay(3, SECONDS));

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartAndEnd(startId, Collections.singleton(EndStreamMatcher.alwaysEndingOn(endId)))));

        assertThat(values).contains(Arrays.asList(3L, 4L, 5L));
        assertThat(values).contains(Arrays.asList(6L, 7L, 8L));
        assertThat(values).contains(Arrays.asList(9L));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bufferEndsWithTimeout() {
        Observable<Object> startStream = shiftedBy500Ms(ofObject(interval(3, SECONDS)));

        StreamId<Long> sourceId = registerRx(oneSecondIntervalOfLength(10));
        StreamId<Object> startId = registerRx(startStream);
        StreamId<Object> endId = registerRx(never());

        Duration timeout = Duration.ofSeconds(5);

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, BufferSpecification
                .ofStartEndTimeout(startId, Collections.singleton(EndStreamMatcher.alwaysEndingOn(endId)), timeout)));

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
                .ofStartEndTimeout(startId, Collections.singleton(EndStreamMatcher.alwaysEndingOn(endId)), timeout)));

        assertThat(values).hasSize(2);
        assertThat(values.get(0)).containsExactlyElementsOf(values.get(1));
    }

    private Observable<Long> oneSecondIntervalOfLength(int length) {
        return interval(1, SECONDS).take(length);
    }

    private List<List<Long>> subscribeAndWait(OverlapBufferStreamId<Long> bufferId) {
        BlockingTestSubscriber<List<Long>> subscriber = BlockingTestSubscriber.ofName("subscriber");
        CountDownLatch sync = new CountDownLatch(1);
        publisherFrom(rxFrom(pool.discover(bufferId)).doOnTerminate(sync::countDown)).subscribe(subscriber);
        try {
            sync.await();
        } catch (InterruptedException e) {
            /* Tests.. */
        }
        return subscriber.getValues();
    }

    private <T> StreamId<T> registerRx(Observable<T> stream) {
        @SuppressWarnings("unchecked")
        StreamId<T> id = mock(StreamId.class);
        pool.provide(id, fromRx(stream));
        return id;
    }

    /**
     * This is used to have the start and stop stream to not conflict with the data stream. Setting a 500 delay shift
     * should be ok for tests.
     */
    private <T> Observable<T> shiftedBy500Ms(Observable<T> source) {
        return source.delay(500, MILLISECONDS);
    }

    private Observable<Object> ofObject(Observable<?> source) {
        return source.cast(Object.class);
    }

}
