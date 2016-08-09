/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamfactory.CombineWithLatestStreamIdStreamFactory;
import cern.streaming.pool.core.service.streamid.CombineWithLatestStreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;
import rx.Observable;

/**
 * Unit tests for {@link CombineWithLatestStreamIdStreamFactory}
 * 
 * @author acalia
 */
public class CombineWithLatestStreamIdStreamFactoryTest {

    private CombineWithLatestStreamIdStreamFactory factory;
    private BlockingTestSubscriber<Long> subscriber;

    @Before
    public void setUp() {
        factory = new CombineWithLatestStreamIdStreamFactory();
        subscriber = BlockingTestSubscriber.ofName("Subscriber");
    }

    /* @formatter:off
     * Trigger +--------------T--------------T--------------T--------------T--------------T-------------------->
     * Data    +---------0---------1---------2---------3---------4---------5---------6---------7---------8----->
     *
     * Result  +--------------0--------------2--------------3--------------5--------------6-------------------->
     * @formatter:on
     */
    @Test
    public void test1() {
        Observable<Long> trigger = Observable.interval(1500, MILLISECONDS).take(5);
        Observable<Long> data = Observable.interval(1000, MILLISECONDS);

        subscribeAndWait(data, trigger);

        assertThat(subscriber.getValues()).containsExactly(0L, 2L, 3L, 5L, 6L);
    }

    /* @formatter:off
     * Trigger +----T------------------------T--------------T--------------T-------------------T--------------->
     * Data    +---------0---------1---------2---------3---------4---------5---------6---------7---------8----->
     *
     * Result  +-----------------------------2--------------3--------------5-------------------7--------------->
     * @formatter:on
     */
    @Test
    public void test2() {
        Observable<Long> trigger = Observable.merge(delayed(500), delayed(3000), delayed(4500), delayed(6500),
                delayed(8000));
        Observable<Long> data = Observable.interval(1000, MILLISECONDS);

        subscribeAndWait(data, trigger);

        assertThat(subscriber.getValues()).containsExactly(2L, 3L, 5L, 7L);
    }

    /* @formatter:off
     * Trigger +----T-------->
     * Data    +---------0--->
     *
     * Result  +------------->
     * @formatter:on
     */
    @Test
    public void test3() {
        Observable<Long> trigger = delayed(500);
        Observable<Long> data = delayed(1000);

        subscribeAndWait(data, trigger);

        assertThat(subscriber.getValues()).isEmpty();
    }

    /* @formatter:off
     * Trigger +----T------------------------------------------------------------------------------------------>
     * Data    +---------0---------1---------2---------3---------4---------5---------6---------7---------8----->
     *
     * Result  +----------------------------------------------------------------------------------------------->
     * @formatter:on
     */
    @Test
    public void test4() {
        Observable<Long> trigger = delayed(500);
        Observable<Long> data = Observable.interval(1000, MILLISECONDS);

        subscribeAndWait(data, trigger);

        assertThat(subscriber.getValues()).isEmpty();
    }

    private void subscribeAndWait(Observable<Long> data, Observable<Long> trigger) {
        StreamId<Long> streamId = CombineWithLatestStreamId.of(fromRx(data), fromRx(trigger));
        ReactiveStreams.publisherFrom(factory.create(streamId, null)).subscribe(subscriber);
        subscriber.await();
    }

    private Observable<Long> delayed(int millis) {
        return Observable.just(-1L).delay(millis, MILLISECONDS);
    }

}
