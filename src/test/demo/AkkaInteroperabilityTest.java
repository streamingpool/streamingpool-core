/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo;

import static org.assertj.core.api.Assertions.assertThat;
import static stream.ReactStreams.namedId;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import akka.NotUsed;
import akka.stream.DelayOverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import demo.subscriber.BlockingTestSubscriber;
import scala.concurrent.duration.Duration;
import stream.AkkaStreamSupport;
import stream.StreamId;

@RunWith(SpringJUnit4ClassRunner.class)
public class AkkaInteroperabilityTest extends AkkaStreamSupport {
    private static final StreamId<Integer> SOURCE_ID = namedId("SourceStream");
    private static final StreamId<Integer> BUFFERED_ID = namedId("BufferedSourceStream");

    private static final int SOURCE_STREAM_ELEMENTS = 20;
    private static final int SOURCE_INTERVAL_MS = 5;
    private static final int CONSUMING_DURATION_MS = 5;

    private static final Source<Integer, NotUsed> RANGE_SOURCE = Source.range(1, SOURCE_STREAM_ELEMENTS);

    private static final Flow<Integer, Integer, NotUsed> FLOW = Flow.of(Integer.class)
            .delay(Duration.create(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS), DelayOverflowStrategy.backpressure())
            .async();

    private BlockingTestSubscriber<Integer> subscriber;

    @Before
    public void setUp() {
        subscriber = new BlockingTestSubscriber<Integer>("Test Subscriber", CONSUMING_DURATION_MS, true);
    }

    @Test
    public void provideAndDiscoverAkkaStream() {
        provide(RANGE_SOURCE).as(SOURCE_ID);
        provide(RANGE_SOURCE.via(FLOW)).as(BUFFERED_ID);

        publisherFrom(BUFFERED_ID).subscribe(subscriber);

        subscriber.await();
        assertThat(subscriber.getValues()).hasSize(SOURCE_STREAM_ELEMENTS);
    }
}
