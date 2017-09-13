package org.streamingpool.core.service.impl;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.streamingpool.core.conf.DefaultPoolConfiguration.STREAMINGPOOL_THREAD_POOL_SIZE;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Flowable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

@RunWith(SpringJUnit4ClassRunner.class)
public class LocalPoolSingleThreadingTest extends AbstractStreamTest implements RxStreamSupport {

    public LocalPoolSingleThreadingTest() {
        System.setProperty(STREAMINGPOOL_THREAD_POOL_SIZE, "1");
    }

    @Test(expected = LocalPoolSingleThreadingTestException.class)
    public void shouldObserveOnASingleThread() {
        Flowable<Long> source = Flowable.just(1L, 2L, 3L, 4L).share().onBackpressureLatest();
        StreamId<Long> streamId = provide(source).withUniqueStreamId();
        rxFrom(streamId).subscribe(i -> SECONDS.sleep(10));
        rxFrom(streamId).test().awaitCount(2, () -> {
            throw new LocalPoolSingleThreadingTestException();
        } , 100);
    }

    private static class LocalPoolSingleThreadingTestException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

}
