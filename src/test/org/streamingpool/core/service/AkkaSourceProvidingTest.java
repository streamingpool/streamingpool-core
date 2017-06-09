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

package org.streamingpool.core.service;

import static akka.stream.ThrottleMode.shaping;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.core.testing.util.UncheckedWaits.waitFor;

import org.junit.Ignore;
import org.junit.Test;
import org.streamingpool.core.support.StreamCollectingSupport;
import org.streamingpool.core.testing.AbstractAkkaStreamTest;
import org.streamingpool.core.testing.NamedStreamId;

import akka.NotUsed;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.Duration;

public class AkkaSourceProvidingTest extends AbstractAkkaStreamTest implements StreamCollectingSupport {

    private static final Source<Integer, NotUsed> COUNTER_50_HZ = Source.range(1, 100)
            .throttle(50, Duration.create(1, SECONDS), 1, shaping()).buffer(1, OverflowStrategy.dropBuffer());

    private static final StreamId<Integer> STREAM_ID = NamedStreamId.ofName("ticker");

    @Ignore
    @Test
    public void provideMaterializedIsRunningFromTheBeginning() {
        provide(COUNTER_50_HZ).materialized().as(STREAM_ID);

        waitFor(1500, MILLISECONDS);

        /*
         * The first emitted observable is always 1; It seems that this comes from the input buffer of the Sink when
         * transformed to a publisher.
         */
        assertThat(firstEmittedItem()).isEqualTo(1);

        /*
         * The second observed value should be higher than 50 after the wait, because the source starts ticking on
         * registration (=materialization)
         */
        assertThat(secondEmittedItem()).isGreaterThanOrEqualTo(50);
    }

    @Ignore
    @Test
    public void provideUnmaterializedStartsRunningOnLookup() {
        provide(COUNTER_50_HZ).as(STREAM_ID);

        waitFor(1500, MILLISECONDS);

        /*
         * The first emitted observable is always 1; It seems that this comes from the input buffer of the Sink when
         * transformed to a publisher.
         */
        assertThat(firstEmittedItem()).isEqualTo(1);

        /*
         * The second observed value should be quite small (we assume <5) even after a wait, because the source starts
         * ticking on first lookup.
         */
        assertThat(secondEmittedItem()).isLessThan(5);
    }

    @Test
    public void subscribeTwiceOnMaterializedProvidedIsPossible() {
        provide(COUNTER_50_HZ).materialized().as(STREAM_ID);
        assertSubscribeTwiceIsPossible();
    }

    @Test
    public void subscribeTwiceOnUnmaterializedProvidedIsPossible() {
        provide(COUNTER_50_HZ).as(STREAM_ID);
        assertSubscribeTwiceIsPossible();
    }

    private Integer secondEmittedItem() {
        return from(STREAM_ID).skip(1).and().awaitNext();
    }

    private Integer firstEmittedItem() {
        return from(STREAM_ID).awaitNext();
    }

    private void assertSubscribeTwiceIsPossible() {
        rxFrom(STREAM_ID).subscribe((val) -> System.out.println("A:" + val));
        /*
         * This call would throw, if the source would not have been materialized with a sink with fanout
         */
        rxFrom(STREAM_ID).blockingFirst();
    }

}
