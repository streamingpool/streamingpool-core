/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import cern.streaming.pool.core.incubation.akka.IdBasedSource;
import cern.streaming.pool.core.service.impl.SimplePool;
import cern.streaming.pool.core.util.ReactStreams;
import rx.Observable;

public class AkkaIdSourceTest {

    private static final StreamId<Integer> STREAM_ID = new StreamId<Integer>() {
        @Override
        public String toString() {
            return "testIntegerStreamId";
        }
    };

    private static final SimplePool SIMPLE_POOL = new SimplePool();

    static {
        SIMPLE_POOL.provide(STREAM_ID, ReactStreams.fromRx(Observable.range(0, 1000)));
    }

    private static class TestIdBasedSource extends IdBasedSource<Integer> {

        TestIdBasedSource() {
            super(STREAM_ID);
        }

        @Override
        protected DiscoveryService getDiscoveryService(Attributes attributes) {
            return SIMPLE_POOL;
        }
    }

    @Test
    public void createIdSource() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1000);

        ActorSystem system = ActorSystem.create("AkkaIdSourceTest");
        Materializer materializer = ActorMaterializer.create(system);

        IdBasedSource<Integer> sourceGraph = new TestIdBasedSource();

        List<Integer> results = new ArrayList<>(1000);
        Source.fromGraph(sourceGraph).runForeach(i -> {
            results.add(i);
            latch.countDown();
        }, materializer);

        latch.await();

        List<Integer> expected = Observable.range(0, 1000).toList().toBlocking().first();

        assertThat(results).hasSize(1000).containsExactlyElementsOf(expected);
    }
}
