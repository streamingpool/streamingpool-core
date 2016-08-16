/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.publisherFrom;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static rx.Observable.interval;
import static rx.Observable.just;
import static rx.Observable.never;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.LocalPool;
import cern.streaming.pool.core.service.streamfactory.OverlapBufferStreamIdStreamFactory;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;
import rx.Observable;

public class OverlapBufferStreamIdStreamFactoryTest {

    private static final Observable<Object> EVERY_3_SEC_OBJECTS = interval(3, SECONDS).cast(Object.class);
    private static final Observable<Long> EVERY_SEC_INTERVAL_10_ELEMENTS = interval(1, SECONDS).take(10);
    private static final Duration NO_TIME_OUT = Duration.ofDays(5);

    private OverlapBufferStreamIdStreamFactory factory;
    private LocalPool pool;

    @Before
    public void setUp() {
        factory = new OverlapBufferStreamIdStreamFactory();
        pool = new LocalPool(Arrays.asList(factory));
    }

    @Test
    public void dataStreamEndsBeforeEndStreamEmits() {
        StreamId<Long> sourceId = registerRx(EVERY_SEC_INTERVAL_10_ELEMENTS);
        StreamId<Object> startId = registerRx(just(new Object()));
        StreamId<Object> endId = registerRx(never());

        Duration timeout = NO_TIME_OUT;

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, startId, endId, timeout));

        assertThat(values).isEmpty();
    }

    @Test
    public void dataStreamEndsBeforeStartStreamEmits() {
        StreamId<Long> sourceId = registerRx(EVERY_SEC_INTERVAL_10_ELEMENTS);
        StreamId<Object> startId = registerRx(never());
        StreamId<Object> endId = registerRx(never());

        Duration timeout = NO_TIME_OUT;

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, startId, endId, timeout));

        assertThat(values).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bufferEndsWithDelayedStart() {
        Observable<Object> startStream = shiftedBy500Ms(EVERY_3_SEC_OBJECTS);

        StreamId<Long> sourceId = registerRx(EVERY_SEC_INTERVAL_10_ELEMENTS);
        StreamId<Object> startId = registerRx(startStream);
        StreamId<Object> endId = registerRx(startStream.delay(3, SECONDS));

        Duration timeout = NO_TIME_OUT;

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, startId, endId, timeout));

        assertThat(values).contains(Arrays.asList(3L, 4L, 5L));
        assertThat(values).contains(Arrays.asList(6L, 7L, 8L));
        assertThat(values).contains(Arrays.asList(9L));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bufferEndsWithTimeout() {
        Observable<Object> startStream = shiftedBy500Ms(EVERY_3_SEC_OBJECTS);

        StreamId<Long> sourceId = registerRx(EVERY_SEC_INTERVAL_10_ELEMENTS);
        StreamId<Object> startId = registerRx(startStream);
        StreamId<Object> endId = registerRx(startStream.delay(10, SECONDS));

        Duration timeout = Duration.ofSeconds(5);

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, startId, endId, timeout));

        assertThat(values).contains(Arrays.asList(3L, 4L, 5L, 6L, 7L));
        assertThat(values).contains(Arrays.asList(6L, 7L, 8L, 9L));
        assertThat(values).contains(Arrays.asList(9L));
    }

    @Test
    public void bufferOverlap() {
        StreamId<Long> sourceId = registerRx(EVERY_SEC_INTERVAL_10_ELEMENTS);
        StreamId<Object> startId = registerRx(shiftedBy500Ms(just(new Object(), new Object()).delay(5, SECONDS)));
        StreamId<Object> endId = registerRx(never());

        Duration timeout = Duration.ofSeconds(5);

        List<List<Long>> values = subscribeAndWait(OverlapBufferStreamId.of(sourceId, startId, endId, timeout));

        assertThat(values).hasSize(2);
        assertThat(values.get(0)).containsExactlyElementsOf(values.get(1));
    }

    private List<List<Long>> subscribeAndWait(OverlapBufferStreamId<Long, Object> bufferId) {
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

}
