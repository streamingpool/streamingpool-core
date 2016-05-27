/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo;

import static org.assertj.core.api.Assertions.assertThat;
import static stream.ReactStreams.namedId;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import akka.NotUsed;
import akka.stream.DelayOverflowStrategy;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import demo.subscriber.BlockingTestSubscriber;
import rx.Observable;
import scala.concurrent.duration.Duration;
import stream.AkkaStreamSupport;
import stream.ReactStreams;
import stream.StreamId;

@RunWith(SpringJUnit4ClassRunner.class)
public class AkkaInteroperabilityTest extends AkkaStreamSupport {
    private static final StreamId<Integer> SOURCE_ID = namedId("SourceStream");
    private static final StreamId<Integer> BUFFERED_ID = namedId("BufferedSourceStream");

    private static final int SOURCE_STREAM_ELEMENT_NUM = 20;
    private static final int SOURCE_INTERVAL_MS = 5;
    private static final int CONSUMING_DURATION_MS = 5;
    private static final List<Integer> ELEMENTS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20);

    private static final Source<Integer, NotUsed> RANGE_SOURCE_AKKA = Source.range(1, SOURCE_STREAM_ELEMENT_NUM);
    private static final Flow<Integer, Integer, NotUsed> DELAY_FLOW = Flow.of(Integer.class)
            .delay(Duration.create(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS), DelayOverflowStrategy.backpressure())
            .async();
    private static final Observable<Integer> RANGE_SOURCE_RX = Observable.range(1, SOURCE_STREAM_ELEMENT_NUM);

    private BlockingTestSubscriber<Integer> subscriber;

    @Before
    public void setUp() {
        subscriber = new BlockingTestSubscriber<Integer>("Test Subscriber", CONSUMING_DURATION_MS);
    }

    @After
    public void teardown() {
        unregisterAllStreams();
    }

    @Test
    public void provideAndDiscoverAkkaStream() {
        provide(RANGE_SOURCE_AKKA).as(SOURCE_ID);
        provide(RANGE_SOURCE_AKKA.via(DELAY_FLOW)).as(BUFFERED_ID);

        publisherFrom(BUFFERED_ID).subscribe(subscriber);

        subscriber.await();
        assertThat(subscriber.getValues()).hasSize(SOURCE_STREAM_ELEMENT_NUM);
        assertThat(subscriber.getValues()).containsExactlyElementsOf(ELEMENTS);
    }

    @Test
    public void provideAsAkkaAndDiscoverAsRx() {
        provide(RANGE_SOURCE_AKKA).as(SOURCE_ID);

        List<Integer> streamValues = rxFrom(SOURCE_ID).toList().toBlocking().single();

        assertThat(streamValues).hasSameSizeAs(ELEMENTS);
        assertThat(streamValues).containsExactlyElementsOf(ELEMENTS);
    }

    @Test
    public void provideAsRxAndDiscoverAsAkka() {
        provide(ReactStreams.fromRx(RANGE_SOURCE_RX)).as(SOURCE_ID);

        sourceFrom(SOURCE_ID)
                .toMat(Sink.asPublisher(AsPublisher.WITHOUT_FANOUT), Keep.right())
                .run(materializer())
                .subscribe(subscriber);

        subscriber.await();

        assertThat(subscriber.getValues()).hasSize(SOURCE_STREAM_ELEMENT_NUM);
        assertThat(subscriber.getValues()).containsExactlyElementsOf(ELEMENTS);
    }
}
