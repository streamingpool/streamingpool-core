package org.streamingpool.core.service.impl;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.core.conf.DefaultPoolConfiguration.STREAMINGPOOL_THREAD_POOL_SIZE;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class LocalPoolThreadingTest extends AbstractStreamTest implements RxStreamSupport {

    public LocalPoolThreadingTest(){
        System.setProperty(STREAMINGPOOL_THREAD_POOL_SIZE, "10");
    }

    @Test(timeout = 500)
    public void shouldObserveOnThreadPool() {
        Flowable<Long> source = Flowable.just(1L, 2L, 3L, 4L)
                .share()
                .onBackpressureLatest();

        StreamId<Long> streamId = provide(source).withUniqueStreamId();
        Flowable<Long> stream = rxFrom(streamId);
        stream.subscribe(i -> SECONDS.sleep(10));
        TestSubscriber<Long> test = stream.test();
        test.awaitCount(4);
        assertThat(test.values()).containsOnly(1L, 2L, 3L, 4L);
    }
}
