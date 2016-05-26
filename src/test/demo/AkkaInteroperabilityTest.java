/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static stream.ReactStreams.publisherFrom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.DelayOverflowStrategy;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;
import conf.InProcessPoolConfiguration;
import demo.subscriber.BlockingTestSubscriber;
import rx.Observable;
import rx.schedulers.Schedulers;
import scala.concurrent.duration.Duration;
import stream.AkkaStreamSupport;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamProcessingSupport;
import stream.impl.NamedStreamId;

@RunWith(SpringJUnit4ClassRunner.class)
public class AkkaInteroperabilityTest extends AkkaStreamSupport {
    private static final NamedStreamId<Integer> SOURCE_ID = new NamedStreamId<>("SourceStream");
    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaInteroperabilityTest.class);
    private static final int SOURCE_STREAM_ELEMENTS = 20;
    private static final int SOURCE_INTERVAL_MS = 50;
    private static final int CONSUMING_DURATION_MS = 500;

    private static final Source<Integer, NotUsed> BUFFERED_DEMO_SOURCE = Source.range(1, SOURCE_STREAM_ELEMENTS)
            .delay(Duration.create(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS), DelayOverflowStrategy.backpressure())
            .async();

    private BlockingTestSubscriber<Integer> subscriber;

    @Before
    public void setUp() {
        subscriber = new BlockingTestSubscriber<Integer>("Test Subscriber", CONSUMING_DURATION_MS, true);
    }

    @Test
    public void provideAndDiscoverAkkaStream() {
        provide(BUFFERED_DEMO_SOURCE).as(SOURCE_ID);

        Publisher<Integer> discoveredPublisher = publisherFrom(discover(SOURCE_ID));
        discoveredPublisher.subscribe(subscriber);

        subscriber.await();
        assertThat(subscriber.getValues()).hasSize(SOURCE_STREAM_ELEMENTS);
    }

    private void provideIntervalStream() {
        ReactStream<Long> obs = ReactStreams.fromRx(Observable.interval(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS)
                .limit(SOURCE_STREAM_ELEMENTS).onBackpressureDrop(value -> LOGGER.info("Source dropped: {}", value))
                .doOnRequest(request -> LOGGER.info("Source requested: {}", request))
                .observeOn(Schedulers.newThread(), 1));
    }
}
