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

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.CombineWithLatestStreamFactory;
import org.streamingpool.core.service.streamid.CombineWithLatestStreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

/**
 * Unit tests for {@link CombineWithLatestStreamFactory}
 * 
 * @author acalia
 */
public class CombineWithLatestStreamIdStreamTest extends AbstractStreamTest implements RxStreamSupport {

    private TestSubscriber<Long> subscriber;

    @Before
    public void setUp() {
        subscriber = TestSubscriber.create();
    }

    /* @formatter:off
     * Trigger +--------------T---------T---------T---------T----------------------------------------------->
     * Data    +---------0---------1---------2---------3---------4---------5---------6---------7---------8-->
     *
     * Result  +--------------0---------1---------2--------3------------------------------------------------>
     * @formatter:on
     */
    @Test
    public void test1() {
        Flowable<Long> trigger = Flowable.interval(1000, MILLISECONDS).delay(500, MILLISECONDS).take(4);
        Flowable<Long> data = Flowable.interval(1000, MILLISECONDS);

        subscribeAndWait(data, trigger);

        assertThat(subscriber.values()).containsExactly(0L, 1L, 2L, 3L);
    }

    /* @formatter:off
     * Trigger +----T-----------------------------T---------T-------------------T-------------------T---------->
     * Data    +---------0---------1---------2---------3---------4---------5---------6---------7---------8----->
     *
     * Result  +----------------------------------2---------3-------------------5-------------------7---------->
     * @formatter:on
     */
    @Test
    public void test2() {
        Flowable<Long> trigger = Flowable
                .merge(asList(delayed(500), delayed(3500), delayed(4500), delayed(6500), delayed(8500)));
        Flowable<Long> data = Flowable.interval(1000, MILLISECONDS);

        subscribeAndWait(data, trigger);

        assertThat(subscriber.values()).containsExactly(2L, 3L, 5L, 7L);
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
        Flowable<Long> trigger = delayed(500);
        Flowable<Long> data = delayed(1000);

        subscribeAndWait(data, trigger);

        assertThat(subscriber.values()).isEmpty();
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
        Flowable<Long> trigger = delayed(500);
        Flowable<Long> data = Flowable.interval(1000, MILLISECONDS);
        subscribeAndWait(data, trigger);
        assertThat(subscriber.values()).isEmpty();
    }

    private void subscribeAndWait(Flowable<Long> data, Flowable<Long> trigger) {
        StreamId<Long> dataId = provide(data).withUniqueStreamId();
        StreamId<Long> triggerId = provide(trigger).withUniqueStreamId();
        StreamId<Long> streamId = CombineWithLatestStreamId.dataPropagated(triggerId, dataId);

        rxFrom(streamId).subscribe(subscriber);
        subscriber.awaitTerminalEvent();
    }

    private Flowable<Long> delayed(int millis) {
        return Flowable.just(-1L).delay(millis, MILLISECONDS);
    }

}
