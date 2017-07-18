package org.streamingpool.core.service.util;

import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;

/**
 * This transformer runs the given action after the first subscription.
 *
 * @param <T>
 */
public class DoAfterFirstSubscribe<T> implements FlowableTransformer<T, T> {
    private AtomicBoolean done = new AtomicBoolean(false);
    private Runnable afterFirstSubscribe;

    public DoAfterFirstSubscribe(Runnable actionAfterFirstSubscribe) {
        this.afterFirstSubscribe = actionAfterFirstSubscribe;
    }

    @Override
    public Publisher<T> apply(Flowable<T> flowable) {
        return Flowable.create(flowableEmitter -> {
            flowable.subscribe(flowableEmitter::onNext,
                    flowableEmitter::onError,
                    flowableEmitter::onComplete
            );
            if (done.compareAndSet(false, true)) {
                afterFirstSubscribe.run();
            }
        }, BackpressureStrategy.MISSING);
    }
}