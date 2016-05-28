/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import rx.Observable;
import stream.impl.SimplePool;
import stream.proto.akka.IdBasedSource;

public class AkkaIdSourceTest {

    private static final StreamId<Integer> STREAM_ID = new StreamId<Integer>() {
        @Override
        public String toString() {
            return "testIntegerStreamId";
        }
    };

    private  static final SimplePool SIMPLE_POOL = new SimplePool();

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

        Source.fromGraph(sourceGraph)
                .runForeach(i -> {
                    System.out.println(i);
                    latch.countDown();
                }, materializer);

        latch.await();
    }
}
