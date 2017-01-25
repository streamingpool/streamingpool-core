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

package org.streamingpool.core.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.support.AkkaStreamSupport;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractAkkaStreamTest;
import org.streamingpool.core.testing.NamedStreamId;

import akka.NotUsed;
import akka.stream.DelayOverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import scala.concurrent.duration.Duration;

public class AkkaInteroperabilityTest extends AbstractAkkaStreamTest implements AkkaStreamSupport, RxStreamSupport {

    private static final StreamId<Integer> SOURCE_ID = NamedStreamId.ofName("SourceStream");
    private static final StreamId<Integer> BUFFERED_ID = NamedStreamId.ofName("BufferedSourceStream");

    private static final int SOURCE_STREAM_ELEMENT_NUM = 20;
    private static final int SOURCE_INTERVAL_MS = 5;
    private static final List<Integer> ELEMENTS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20);
    private static final Source<Integer, NotUsed> RANGE_SOURCE_AKKA = createAkkaRangeSource();
    private static final Flow<Integer, Integer, NotUsed> DELAY_FLOW = createDelayFlow();
    private static final Flowable<Integer> RANGE_SOURCE_RX = createRxRangeSource();

    private TestSubscriber<Integer> subscriber;

    @Before
    public void setUp() {
        subscriber = TestSubscriber.create();
    }

    @Test
    public void provideAndDiscoverAkkaStream() throws InterruptedException {
        provide(RANGE_SOURCE_AKKA.via(DELAY_FLOW)).materialized().as(BUFFERED_ID);
        discover(BUFFERED_ID).subscribe(subscriber);

        subscriber.await();
        assertThat(subscriber.values()).hasSize(SOURCE_STREAM_ELEMENT_NUM);
        assertThat(subscriber.values()).containsExactlyElementsOf(ELEMENTS);
    }

    @Test
    public void provideAsAkkaAndDiscoverAsRx() {
        provide(RANGE_SOURCE_AKKA).materialized().as(SOURCE_ID);

        List<Integer> streamValues = rxFrom(SOURCE_ID).toList().blockingGet();

        assertThat(streamValues).hasSameSizeAs(ELEMENTS);
        assertThat(streamValues).containsExactlyElementsOf(ELEMENTS);
    }

    @Test
    public void provideAsRxAndDiscoverAsAkka() throws InterruptedException, ExecutionException {
        provide(RANGE_SOURCE_RX).as(SOURCE_ID);

        List<Integer> values = sourceFrom(SOURCE_ID).toMat(Sink.seq(), Keep.right()).run(materializer())
                .toCompletableFuture().get();

        assertThat(values).hasSize(SOURCE_STREAM_ELEMENT_NUM);
        assertThat(values).containsExactlyElementsOf(ELEMENTS);
    }

    private static Flow<Integer, Integer, NotUsed> createDelayFlow() {
        return Flow.of(Integer.class)
                .delay(Duration.create(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS), DelayOverflowStrategy.backpressure())
                .async();
    }

    private static Source<Integer, NotUsed> createAkkaRangeSource() {
        return Source.range(1, SOURCE_STREAM_ELEMENT_NUM);
    }

    private static Flowable<Integer> createRxRangeSource() {
        return Flowable.range(1, SOURCE_STREAM_ELEMENT_NUM);
    }
}
