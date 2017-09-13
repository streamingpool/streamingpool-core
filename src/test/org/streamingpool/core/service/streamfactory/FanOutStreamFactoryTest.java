package org.streamingpool.core.service.streamfactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.core.domain.backpressure.BackpressureBufferStrategy.BackpressureBufferOverflowStrategy.DROP_OLDEST;
import static org.streamingpool.core.domain.backpressure.BackpressureStrategies.onBackpressureBuffer;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.domain.backpressure.BackpressureStrategies;
import org.streamingpool.core.service.StreamFactoryRegistry;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.FanOutStreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

public class FanOutStreamFactoryTest extends AbstractStreamTest implements RxStreamSupport {

    @Autowired
    private StreamFactoryRegistry streamFactoryRegistry;

    @Before
    public void setUp() {
        streamFactoryRegistry.addIntercept(new FanOutStreamFactory());
    }

    @Test
    public void testOnBackpressureLatest() {
        PublishProcessor<String> in = PublishProcessor.create();

        StreamId<String> inStreamId = provide(in).withUniqueStreamId();

        FanOutStreamId<String> fanOutStreamId = FanOutStreamId
                .fanOut(inStreamId, BackpressureStrategies.onBackpressureLatest());

        TestSubscriber<String> fast = rxFrom(fanOutStreamId)
                //.onBackpressureLatest() // this is necessary because a backpressure strategy needs to be applied after the observeOn applied in TrackKeppingDiscoveryService
                .test(0);
        TestSubscriber<String> slow = rxFrom(fanOutStreamId)
                // .onBackpressureLatest()
                .test(0);

        slow.request(1);
        fast.request(1);
        in.onNext("A");
        fast.request(1);
        in.onNext("B");
        fast.request(1);
        in.onNext("C");
        fast.request(1);
        in.onNext("D");
        fast.request(1);
        in.onNext("E");
        in.onNext("F");
        in.onNext("G");
        in.onNext("H");
        sleep(); // necessary for the propagation between threads
        slow.request(1);
        fast.request(1);
        in.onNext("I");
        slow.request(1);
        in.onNext("J");

        fast.awaitCount(6);
        slow.awaitCount(3);
        fast.assertValueCount(6);
        slow.assertValueCount(3);
        System.out.println("fast: " + fast.values());
        System.out.println("slow: " + slow.values());
        assertThat(fast.values()).containsOnly("A", "B", "C", "D", "E", "H");
        assertThat(slow.values()).containsOnly("A", "H", "I");
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOnBackpressureDrop() {
        PublishProcessor<String> in = PublishProcessor.create();

        StreamId<String> inStreamId = provide(in).withUniqueStreamId();

        FanOutStreamId<String> fanOutStreamId = FanOutStreamId
                .fanOut(inStreamId, BackpressureStrategies.onBackpressureDrop());

        TestSubscriber<String> fast = rxFrom(fanOutStreamId)
                .test(0);
        TestSubscriber<String> slow = rxFrom(fanOutStreamId)
                .test(0);

        slow.request(1);
        fast.request(1);
        in.onNext("A");
        fast.request(1);
        in.onNext("B");
        fast.request(1);
        in.onNext("C");
        fast.request(1);
        in.onNext("D");
        fast.request(1);
        in.onNext("E");
        in.onNext("F");
        in.onNext("G");
        in.onNext("H");
        sleep(); // necessary for the propagation between threads
        slow.request(1);
        fast.request(1);
        in.onNext("I");
        slow.request(1);
        in.onNext("J");

        fast.awaitCount(6);
        slow.awaitCount(3);
        fast.assertValueCount(6);
        slow.assertValueCount(3);
        System.out.println("fast: " + fast.values());
        System.out.println("slow: " + slow.values());
        assertThat(fast.values()).containsOnly("A", "B", "C", "D", "E", "I");
        assertThat(slow.values()).containsOnly("A", "I", "J");
    }

    @Test
    public void testOnBackpressureBuffer() {
        PublishProcessor<String> in = PublishProcessor.create();

        StreamId<String> inStreamId = provide(in).withUniqueStreamId();

        FanOutStreamId<String> fanOutStreamId = FanOutStreamId.fanOut(inStreamId, onBackpressureBuffer(2,
                DROP_OLDEST));

        TestSubscriber<String> fast = rxFrom(fanOutStreamId)
                .test(0);
        TestSubscriber<String> slow = rxFrom(fanOutStreamId)
                .test(0);

        slow.request(1);
        fast.request(1);
        in.onNext("A");
        fast.request(1);
        in.onNext("B");
        fast.request(1);
        in.onNext("C");
        fast.request(1);
        in.onNext("D");
        fast.request(1);
        in.onNext("E");
        in.onNext("F");
        in.onNext("G");
        in.onNext("H");
        sleep(); // necessary for the propagation between threads
        slow.request(1);
        fast.request(3);
        in.onNext("I");
        in.onNext("J");
        sleep(); // necessary for the propagation between threads
        slow.request(1);

        fast.awaitCount(8);
        slow.awaitCount(3);
        fast.assertValueCount(8);
        slow.assertValueCount(3);
        System.out.println("fast: " + fast.values());
        System.out.println("slow: " + slow.values());
        assertThat(fast.values()).containsOnly("A", "B", "C", "D", "E", "G", "H", "I");
        assertThat(slow.values()).containsOnly("A", "G", "I");
    }

    @Test
    public void testWithPureFlowable() {
        PublishProcessor<String> in = PublishProcessor.create();

        Flowable<String> input = in
                .observeOn(Schedulers.newThread(), false)
                .share()
                .observeOn(Schedulers.newThread(), false)
                .onBackpressureLatest();

        TestSubscriber<String> fast = input
                .doOnError(e -> System.out.println("ERROR: " + e))
                .test(0);
        TestSubscriber<String> slow = input
                .doOnError(e -> System.out.println("ERROR: " + e))
                .test(0);

        slow.request(1);
        fast.request(1);
        in.onNext("A");
        fast.request(1);
        in.onNext("B");
        fast.request(1);
        in.onNext("C");
        fast.request(1);
        in.onNext("D");
        fast.request(1);
        in.onNext("E");
        in.onNext("F");
        in.onNext("G");
        in.onNext("H");
        sleep(); // necessary for the propagation between threads
        slow.request(1);
        fast.request(1);
        in.onNext("I");
        slow.request(1);

        fast.awaitCount(6);
        slow.awaitCount(3);
        fast.assertValueCount(6);
        slow.assertValueCount(3);
        System.out.println("fast: " + fast.values());
        System.out.println("slow: " + slow.values());
        assertThat(fast.values()).containsOnly("A", "B", "C", "D", "E", "H");
        assertThat(slow.values()).containsOnly("A", "H", "I");
    }

    @Test
    public void testShareOnColdStream() {
        AtomicInteger emissionCounter = new AtomicInteger();
        Flowable<Long> in = Flowable.rangeLong(1, 10)
                .doOnNext(i -> emissionCounter.incrementAndGet())
                .share();

        TestSubscriber<Long> sub1 = in.observeOn(Schedulers.newThread()).test();
        TestSubscriber<Long> sub2 = in.observeOn(Schedulers.newThread()).test();

        sub1.awaitTerminalEvent();
        sub2.awaitTerminalEvent();

        Assert.assertEquals(20, emissionCounter.get());
    }

}
