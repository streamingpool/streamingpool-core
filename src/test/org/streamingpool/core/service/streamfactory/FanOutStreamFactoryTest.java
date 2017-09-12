package org.streamingpool.core.service.streamfactory;

import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Schedules;
import org.streamingpool.core.domain.backpressure.BackpressureStrategies;
import org.streamingpool.core.service.StreamFactoryRegistry;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.diagnostic.ErrorStreamId;
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
    public void testDrop() {
        PublishProcessor<String> in = PublishProcessor.create();

        StreamId<String> inStreamId = provide(in.onBackpressureBuffer()).withUniqueStreamId();

        FanOutStreamId<String> fanOutStreamId = FanOutStreamId.fanOut(inStreamId, BackpressureStrategies.onBackpressureDrop());


//        rxFrom(ErrorStreamId.of(fanOutStreamId)).subscribe(v -> System.out.println("ERROR SP: " + v));

        TestSubscriber<String> fast = rxFrom(fanOutStreamId).doOnError(e -> System.out.println("ERROR: " + e)).test(0);
        TestSubscriber<String> slow = rxFrom(fanOutStreamId).doOnError(e -> System.out.println("ERROR: " + e)).test(0);

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
        slow.request(1);
        in.onNext("E");
        slow.request(1);
        fast.request(1);
        in.onNext("F");
        slow.request(1);


        sleep();
        sleep();
        sleep();
        sleep();
//        fast.awaitCount(2);
//        slow.awaitCount(2);
//        subscriber.assertValueCount(2);
        System.out.println("fast: " + fast.values());
        System.out.println("slow: " + slow.values());
//        Assertions.assertThat(subscriber.values()).containsOnly("A", "C");


//        TestSubscriber<String> subscriber = in.share().onBackpressureDrop().observeOn(Schedulers.computation()).test(0);
//        TestSubscriber<String> subscriber = in
//            .doOnNext(i -> System.out.println("1 received "+i))
////            .onBackpressureDrop(i -> System.out.println("1st dropping "+i))
//            .observeOn(Schedulers.newThread(), false, 1)
//            .doOnNext(i -> System.out.println("2 received "+i))
//            .share()
//            .doOnNext(i -> System.out.println("3 received "+i))
//            .onBackpressureDrop(i -> System.out.println("dropping "+i))
//            .observeOn(Schedulers.newThread(), false, 1)
//            .doOnNext(i -> System.out.println("4 received "+i))
//            .doOnComplete(()-> System.out.println("completed"))
//            .doOnError(i -> System.err.println("ERROR "+i))
//            .test();

    }

    private void sleep() {
        try {
            Thread.sleep(100
            );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
