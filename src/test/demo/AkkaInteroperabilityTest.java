/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import demo.projectreactor.AccumulatorTestSubscriber;
import rx.Observable;
import rx.schedulers.Schedulers;
import stream.ReactStream;
import stream.ReactStreams;
import stream.impl.NamedStreamId;
import stream.impl.SimplePool;

@RunWith(JUnit4.class)
public class AkkaInteroperabilityTest {
    private static final NamedStreamId<Long> SOURCE_ID = new NamedStreamId<>("SourceStream");
    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaInteroperabilityTest.class);
    private static final int SOURCE_STREAM_ELEMENTS = 30;
    private static final int SOURCE_INTERVAL_MS = 500;
    
    private ActorMaterializer actorMaterializer;
    private ActorSystem actorSystem;
    private SimplePool streamingPool;

    @Test
    public void publishAkkaStream() throws InterruptedException {
        LOGGER.info("Start");
        Source<Integer, NotUsed> akkaSource = Source.range(1, 10);
        Flow<Integer, Long, NotUsed> intToLong = Flow.fromFunction(Long::valueOf);
        Sink<Long, Publisher<Long>> akkaSink = Sink.asPublisher(AsPublisher.WITHOUT_FANOUT);
        
        Publisher<Long> publisher = akkaSource.via(intToLong)
                .runWith(akkaSink, actorMaterializer);
        
        final CountDownLatch sync = new CountDownLatch(1);
        publisher.subscribe(new AccumulatorTestSubscriber("Test Subscriber", 1000, sync, true));
        sync.await();
    }
    
    
    @Before
    public void setup() {
        actorSystem = ActorSystem.create("sys");
        actorMaterializer = ActorMaterializer.create(actorSystem);
        streamingPool = new SimplePool();
        provideIntervalStream();
    }
    
    @After
    public void teardown() {
        actorSystem.terminate();
        actorMaterializer.shutdown();
    }
    
    private void provideIntervalStream() {
        ReactStream<Long> obs = ReactStreams.fromRx(Observable.interval(SOURCE_INTERVAL_MS, TimeUnit.MILLISECONDS)
                .limit(SOURCE_STREAM_ELEMENTS)
                .onBackpressureDrop(value -> LOGGER.info("Source dropped: {}", value))
                .doOnRequest(request -> LOGGER.info("Source requested: {}", request))
                .observeOn(Schedulers.newThread(), 1));
        streamingPool.provide(SOURCE_ID, obs);
    }
}
