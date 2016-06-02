/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.util.subscriber;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BlockingTestSubscriber<T> extends TestSubscriber<T> {

    private CountDownLatch sync = new CountDownLatch(1);

    private BlockingTestSubscriber(String name, long consumingDurationMs) {
        super(name, consumingDurationMs);
    }

    private BlockingTestSubscriber(String name, long consumingDurationMs, boolean verbose) {
        super(name, consumingDurationMs, verbose);
    }

    public static final <T1> BlockingTestSubscriber<T1> ofName(String name) {
        return new BlockingTestSubscriber<>(name, 0);
    }

    public <T1> BlockingTestSubscriber<T1> withConsumingdelayInMs(long consumingDelayInMs) {
        return new BlockingTestSubscriber<>(this.getName(), consumingDelayInMs, this.isVerbose());
    }

    public <T1> BlockingTestSubscriber<T1> verbose() {
        return new BlockingTestSubscriber<>(this.getName(), this.getConsumingDurationMs(), true);
    }

    public <T1> BlockingTestSubscriber<T1> nonverbose() {
        return new BlockingTestSubscriber<>(this.getName(), this.getConsumingDurationMs(), false);
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
