/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.demo;

import static cern.streaming.pool.core.service.ReactStreams.namedId;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.NotUsed;
import akka.stream.DelayOverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.support.AkkaStreamSupport;
import cern.streaming.pool.core.service.support.RxStreamSupport;
import cern.streaming.pool.core.service.test.AbstractAkkaStreamTest;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;
import rx.Observable;
import scala.concurrent.duration.Duration;

public class AkkaInteroperabilityTest extends AbstractAkkaStreamTest implements AkkaStreamSupport, RxStreamSupport {

    private static final StreamId<Integer> SOURCE_ID = namedId("SourceStream");
    private static final StreamId<Integer> BUFFERED_ID = namedId("BufferedSourceStream");

    private static final int SOURCE_STREAM_ELEMENT_NUM = 20;
    private static final int SOURCE_INTERVAL_MS = 5;
    private static final int CONSUMING_DURATION_MS = 5;
    private static final List<Integer> ELEMENTS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20);
    private static final Source<Integer, NotUsed> RANGE_SOURCE_AKKA = createAkkaRangeSource();
    private static final Flow<Integer, Integer, NotUsed> DELAY_FLOW = createDelayFlow();
    private static final Observable<Integer> RANGE_SOURCE_RX = createRxRangeSource();

    private BlockingTestSubscriber<Integer> subscriber;

    @Before
    public void setUp() {
        subscriber = BlockingTestSubscriber.<Integer> ofName("Test Subscriber")
                .withConsumingdelayInMs(CONSUMING_DURATION_MS);
    }

    @After
    public void teardown() {
        unregisterAllStreams();
    }

    @Test
    public void provideAndDiscoverAkkaStream() {
        provide(RANGE_SOURCE_AKKA.via(DELAY_FLOW)).materialized().as(BUFFERED_ID);
        publisherFrom(BUFFERED_ID).subscribe(subscriber);

        subscriber.await();
        assertThat(subscriber.getValues()).hasSize(SOURCE_STREAM_ELEMENT_NUM);
        assertThat(subscriber.getValues()).containsExactlyElementsOf(ELEMENTS);
    }

    @Test
    public void provideAsAkkaAndDiscoverAsRx() {
        provide(RANGE_SOURCE_AKKA).materialized().as(SOURCE_ID);

        List<Integer> streamValues = rxFrom(SOURCE_ID).toList().toBlocking().single();

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

    private static Observable<Integer> createRxRangeSource() {
        return Observable.range(1, SOURCE_STREAM_ELEMENT_NUM);
    }
}
