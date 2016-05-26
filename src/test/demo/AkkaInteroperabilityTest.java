/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import conf.SpringContext;
import demo.subscriber.BlockingTestSubscriber;
import rx.Observable;
import rx.schedulers.Schedulers;
import scala.concurrent.duration.Duration;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamProcessingSupport;
import stream.impl.NamedStreamId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContext.class, loader = AnnotationConfigContextLoader.class)
public class AkkaInteroperabilityTest extends StreamProcessingSupport {
    private static final NamedStreamId<Integer> SOURCE_ID = new NamedStreamId<>("SourceStream");
    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaInteroperabilityTest.class);
    private static final int SOURCE_STREAM_ELEMENTS = 20;
    private static final int SOURCE_INTERVAL_MS = 50;

    private ActorMaterializer actorMaterializer;
    private ActorSystem actorSystem;

    @Test
    public void provideAndDiscoverAkkaStream() {
        final int consumingDurationMs = 500;
        BlockingTestSubscriber<Integer> subscriber = new BlockingTestSubscriber<Integer>("Test Subscriber", consumingDurationMs, true);
        
        Source<Integer, NotUsed> akkaSource = createIntervalAkkaSourceBuffer();
        ReactStream<Integer> reactStream = ReactStreams.streamFrom(akkaSource, actorMaterializer);

        provide(reactStream).as(SOURCE_ID);
        
        ReactStreams.publisherFrom(discover(SOURCE_ID)).subscribe(subscriber);
        subscriber.await();
        
        assertThat(subscriber.getValues()).hasSize(SOURCE_STREAM_ELEMENTS);
    }

    @Before
    public void setup() {
        actorSystem = ActorSystem.create("sys");
        actorMaterializer = ActorMaterializer.create(actorSystem);
    }

    @After
    public void teardown() {
        actorSystem.terminate();
        actorMaterializer.shutdown();
    }

    private Source<Integer, NotUsed> createIntervalAkkaSourceBuffer() {
        return Source.range(1, SOURCE_STREAM_ELEMENTS)
                .delay(Duration.create(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS), DelayOverflowStrategy.backpressure())
                .async();
    }

    private void provideIntervalStream() {
        ReactStream<Long> obs = ReactStreams.fromRx(Observable.interval(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS)
                .limit(SOURCE_STREAM_ELEMENTS).onBackpressureDrop(value -> LOGGER.info("Source dropped: {}", value))
                .doOnRequest(request -> LOGGER.info("Source requested: {}", request))
                .observeOn(Schedulers.newThread(), 1));
    }
}
