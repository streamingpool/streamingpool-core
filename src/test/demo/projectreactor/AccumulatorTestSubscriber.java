/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo.projectreactor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccumulatorTestSubscriber implements Subscriber<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumulatorTestSubscriber.class);

    private final List<Long> values;
    private final String name;
    private final long consumingDurationMs;
    private final CountDownLatch sync;
    private Subscription subscription;
    private boolean isVerbose;

    private int requestedItems;

    public AccumulatorTestSubscriber(String name, long consumingDurationMs, CountDownLatch sync) {
        this.name = name;
        this.consumingDurationMs = consumingDurationMs;
        this.sync = sync;
        this.values = new LinkedList<>();
        this.isVerbose = false;
        this.requestedItems = 1;
    }

    public AccumulatorTestSubscriber(String name, long consumingDurationMs, CountDownLatch sync, boolean verbose) {
        this.name = name;
        this.consumingDurationMs = consumingDurationMs;
        this.sync = sync;
        this.values = new LinkedList<>();
        this.isVerbose = verbose;
        this.requestedItems = 1;
    }

    @Override
    public void onSubscribe(Subscription s) {
        log("[{}] onSubscribe", name);
        this.subscription = s;
        s.request(requestedItems);
    }

    @Override
    public void onNext(Long value) {
        log("[{}] onNext: {}", name, value);
        values.add(value);
        sleep();
        subscription.request(requestedItems);
    }

    @Override
    public void onError(Throwable t) {
        log("[{}] onError", name, t);
        subscription.cancel();
        sync.countDown();
    }

    @Override
    public void onComplete() {
        log("[{}] onComplete", name);
        sync.countDown();
    }

    public void setRequestedItems(int requestedItems) {
        this.requestedItems = requestedItems;
    }

    private void log(String format, Object... args) {
        if (isVerbose) {
            LOGGER.info(format, args);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(consumingDurationMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setVerbose(boolean verbose) {
        isVerbose = verbose;
    }

    public List<Long> getValues() {
        return new ArrayList<>(values);
    }

    public String getName() {
        return name;
    }
}
