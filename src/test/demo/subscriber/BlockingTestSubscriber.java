/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.subscriber;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BlockingTestSubscriber <T> extends TestSubscriber<T> {

    private CountDownLatch sync = new CountDownLatch(1);
    
    public BlockingTestSubscriber(String name, long consumingDurationMs) {
        super(name, consumingDurationMs);
    }
    
    public BlockingTestSubscriber(String name, long consumingDurationMs, boolean verbose) {
        super(name, consumingDurationMs, verbose);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        sync.countDown();
    }
    
    @Override
    public void onError(Throwable t) {
        super.onError(t);
        sync.countDown();
    }
    
    public void await() {
        try {
            sync.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void await(long timeout, TimeUnit timeUnit) {
        try {
            sync.await(timeout, timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
