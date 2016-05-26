/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.projectreactor;
/*
import cern.test.BasicAeronClient;
import cern.test.BasicAeronServer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
*/
/**
 * Tests for Project Reactor.
 * 
 * Project reactor 2.5.0-BUILD-SNAPSHOT needed for running this. They are not in the maven central.
 * Didn't want to pollute the repo with jars since this technology is not likely to be used
 * 
 */
public class ProjectReactorTests {
/*
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectReactorTests.class);
    private static final long ELEMENT_NUM = 10;
    private static final int STREAM_ID = 20;
    private static final String SENDER_HOST = "127.0.0.1";
    private static final String SENDER_CHANNEL = "udp://" + SENDER_HOST + ":12000";
    private static final String RECEIVER_CHANNEL = "udp://" + SENDER_HOST + ":12001";

    @Test
    public void backpressureDropBasedOnConsumptionDelay() throws InterruptedException {
        CountDownLatch syncFastClient = new CountDownLatch(1);
        CountDownLatch syncSlowClient = new CountDownLatch(1);

        AccumulatorTestSubscriber fastClient = new AccumulatorTestSubscriber("Fast (under 1s)",
            Duration.ofMillis(999), syncFastClient);
        AccumulatorTestSubscriber slowClient = new AccumulatorTestSubscriber("Slow (above 1s)",
            Duration.ofMillis(1001), syncSlowClient);

        fastClient.setVerbose(false);
        slowClient.setVerbose(false);

        // SLOW CLIENT
        Flux.interval(Duration.ofMillis(50))
            .take(ELEMENT_NUM * 2)
            .onBackpressureDrop(el -> LOGGER.info("Dropped: {}", el))
            .publishOn(Executors.newSingleThreadExecutor(), 1)
            .subscribe(slowClient)
        ;
        syncSlowClient.await();

        // FAST CLIENT
        Flux.interval(Duration.ofMillis(50))
            .take(ELEMENT_NUM * 2)
            .onBackpressureDrop(el -> LOGGER.info("Dropped: {}", el))
            .publishOn(Executors.newSingleThreadExecutor(), 1)
            .subscribe(fastClient)
        ;
        syncFastClient.await();

        logAccumulatorSubscriber(slowClient);
        logAccumulatorSubscriber(fastClient);
    }

    @Test
    public void backpressureDropOverTheNetworkNeverTerminatesWithTwoClients() throws InterruptedException {
        CountDownLatch sync = new CountDownLatch(2);

        AccumulatorTestSubscriber fastClient = new AccumulatorTestSubscriber("FAST",
            Duration.ofMillis(500), sync);
        AccumulatorTestSubscriber slowClient = new AccumulatorTestSubscriber("SLOW",
            Duration.ofMillis(1100), sync);

        fastClient.setVerbose(false);
        slowClient.setVerbose(false);

        // SOURCE STREAM
        Flux<Long> source = Flux.interval(Duration.ofMillis(50))
            .take(ELEMENT_NUM)
            .onBackpressureDrop(el -> LOGGER.info("Dropped: {}", el));

        // PROVIDE THE STREAM
        new BasicAeronServer("Server", SENDER_CHANNEL)
            .provideStream(STREAM_ID, source);

        // CLIENT A FAST
        new BasicAeronClient("Client A FAST", SENDER_CHANNEL, RECEIVER_CHANNEL)
            .discover(STREAM_ID)
            .publishOn(Executors.newCachedThreadPool(), 1)
            .map(longTxt -> Long.valueOf(longTxt))
            .subscribe(fastClient);

        // CLIENT B SLOW
        new BasicAeronClient("Client B SLOW", SENDER_CHANNEL, RECEIVER_CHANNEL)
            .discover(STREAM_ID)
            .publishOn(Executors.newCachedThreadPool(), 1)
            .map(longTxt -> Long.valueOf(longTxt))
            .subscribe(fastClient);

        sync.await(10, TimeUnit.SECONDS);

        logAccumulatorSubscriber(slowClient);
        logAccumulatorSubscriber(fastClient);
    }

    @Test
    public void provideTwoStreamOnTheSameServer() throws InterruptedException {
        CountDownLatch sync = new CountDownLatch(2);

        final int id1 = 20;
        final int id2 = 30;

        AccumulatorTestSubscriber clientA = new AccumulatorTestSubscriber("Client A",
            Duration.ofMillis(500), sync);
        AccumulatorTestSubscriber clientB = new AccumulatorTestSubscriber("Client B",
            Duration.ofMillis(500), sync);

        // SOURCE STREAMS
        Flux<Long> source1 = Flux.just(1L, 2L, 3L, 4L, 5L)
            .take(ELEMENT_NUM)
            .onBackpressureBuffer();

        Flux<Long> source2 = Flux.just(6L, 7L, 8L, 9L, 10L)
            .take(ELEMENT_NUM)
            .onBackpressureBuffer();

        // PROVIDE THE STREAMS
        BasicAeronServer server = new BasicAeronServer("Server", SENDER_CHANNEL);
        server.provideStream(id1, source1);
        server.provideStream(id2, source2);

        // CLIENT A ID1
        new BasicAeronClient("Client A", SENDER_CHANNEL, RECEIVER_CHANNEL)
            .discover(id1)
            .publishOn(Executors.newCachedThreadPool(), 1)
            .map(longTxt -> Long.valueOf(longTxt))
            .subscribe(clientA);

        // CLIENT B ID2
        new BasicAeronClient("Client B", SENDER_CHANNEL, RECEIVER_CHANNEL)
            .discover(id2)
            .publishOn(Executors.newCachedThreadPool(), 1)
            .map(longTxt -> Long.valueOf(longTxt))
            .subscribe(clientB);

        sync.await(10, TimeUnit.SECONDS);

        logAccumulatorSubscriber(clientA);
        logAccumulatorSubscriber(clientB);
    }

    @Test
    public void backpressureDropOverTheNetworkWithOneClient() throws InterruptedException {
        CountDownLatch sync = new CountDownLatch(1);

        AccumulatorTestSubscriber clientA = new AccumulatorTestSubscriber("CLIENT A",
            Duration.ofMillis(1001), sync);

        clientA.setVerbose(false);

        // SOURCE STREAM
        Flux<Long> source = Flux.interval(Duration.ofMillis(50))
            .take(ELEMENT_NUM)
            .onBackpressureDrop(el -> LOGGER.info("Dropped: {}", el));

        // PROVIDE THE STREAM
        new BasicAeronServer("Server", SENDER_CHANNEL)
            .provideStream(STREAM_ID, source);

        // CLIENT A
        new BasicAeronClient("Client B SLOW", SENDER_CHANNEL, RECEIVER_CHANNEL)
            .discover(STREAM_ID)
            .publishOn(Executors.newCachedThreadPool(), 1)
            .map(longTxt -> Long.valueOf(longTxt))
            .subscribe(clientA);

        sync.await();

        logAccumulatorSubscriber(clientA);
    }

    @Test
    public void bufferBetweenThreads() throws InterruptedException {
        CountDownLatch sync = new CountDownLatch(1);

        AccumulatorTestSubscriber clientA = new AccumulatorTestSubscriber("CLIENT A",
            Duration.ofMillis(1001), sync);

        clientA.setVerbose(false);

        // SOURCE STREAM
        Flux.interval(Duration.ofMillis(500))
            .take(ELEMENT_NUM * 3)
            .onBackpressureDrop(el -> LOGGER.info("Dropped: {}", el))
            .publishOn(Executors.newCachedThreadPool(), 5)
            .subscribe(clientA);

        sync.await();

        logAccumulatorSubscriber(clientA);
    }

    @Test
    public void backpressureStrategies() throws InterruptedException {
        CountDownLatch syncBuffer = new CountDownLatch(1);
        CountDownLatch syncDrop = new CountDownLatch(1);
        CountDownLatch syncLatest = new CountDownLatch(1);

        final Duration clientDelay = Duration.ofMillis(2000);
        final int publisherIntervalMs = 50;
        final long elements = ELEMENT_NUM * 3;

        AccumulatorTestSubscriber bufferClient = new AccumulatorTestSubscriber("BUFFER", clientDelay, syncBuffer, false);
        AccumulatorTestSubscriber dropClient = new AccumulatorTestSubscriber("DROP", clientDelay, syncDrop, false);
        AccumulatorTestSubscriber latestClient = new AccumulatorTestSubscriber("LATEST", clientDelay, syncLatest, false);

        // BACKPRESSURE BUFFER
        Flux.interval(Duration.ofMillis(publisherIntervalMs))
            .take(elements)
            .onBackpressureBuffer()
            .publishOn(Executors.newCachedThreadPool(), 1)
            .subscribe(bufferClient);
        syncBuffer.await();

        // BACKPRESSURE DROP
        Flux.interval(Duration.ofMillis(publisherIntervalMs))
            .take(elements)
            .onBackpressureDrop()
            .publishOn(Executors.newCachedThreadPool(), 1)
            .subscribe(dropClient);
        syncDrop.await();

        // BACKPRESSURE LATEST
        Flux.interval(Duration.ofMillis(publisherIntervalMs))
            .take(elements)
            .onBackpressureLatest()
            .publishOn(Executors.newCachedThreadPool(), 1)
            .subscribe(latestClient);
        syncLatest.await();

        logAccumulatorSubscriber(bufferClient);
        logAccumulatorSubscriber(dropClient);
        logAccumulatorSubscriber(latestClient);
    }

    private void logAccumulatorSubscriber(AccumulatorTestSubscriber sub) {
        LOGGER.info("{} values [{}]: {}", sub.getName(), sub.getValues().size(), sub.getValues());
    }
*/
}
