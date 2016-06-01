/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.util.subscriber;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSubscriber <T> implements Subscriber<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSubscriber.class);

    private final List<T> values;
    private final String name;
    private final long consumingDurationMs;
    private Subscription subscription;
    private boolean isVerbose;

    private int requestedItems;

    public TestSubscriber(String name, long consumingDurationMs) {
        this.name = name;
        this.consumingDurationMs = consumingDurationMs;
        this.values = new LinkedList<>();
        this.isVerbose = false;
        this.requestedItems = 1;
    }

    public TestSubscriber(String name, long consumingDurationMs, boolean verbose) {
        this.name = name;
        this.consumingDurationMs = consumingDurationMs;
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
    public void onNext(T value) {
        log("[{}] onNext: {}", name, value);
        values.add(value);
        sleep();
        subscription.request(requestedItems);
    }

    @Override
    public void onError(Throwable t) {
        log("[{}] onError", name, t);
        subscription.cancel();
    }

    @Override
    public void onComplete() {
        log("[{}] onComplete", name);
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

    public List<T> getValues() {
        return new ArrayList<>(values);
    }

    public String getName() {
        return name;
    }
}
