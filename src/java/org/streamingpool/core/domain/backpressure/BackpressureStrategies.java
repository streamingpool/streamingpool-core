package org.streamingpool.core.domain.backpressure;

import org.streamingpool.core.domain.backpressure.BackpressureBufferStrategy.BackpressureBufferOverflowStrategy;

public final class BackpressureStrategies {

    public static final BackpressureBufferOverflowStrategy DEFAULT_BUFFER_OVERFLOW_STRATEGY = BackpressureBufferOverflowStrategy.DROP_LATEST;
    public static final int DEFAULT_BUFFER_SIZE = 100;
    private static final BackpressureBufferStrategy DEFAULT_BUFFER_STRATEGY = new BackpressureBufferStrategy(DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_OVERFLOW_STRATEGY);
    private static final BackpressureNoneStrategy BACKPRESSURE_NONE_STRATEGY = new BackpressureNoneStrategy();
    private static final BackpressureLatestStrategy BACKPRESSURE_LATEST_STRATEGY = new BackpressureLatestStrategy();
    private static final BackpressureDropStrategy BACKPRESSURE_DROP_STRATEGY = new BackpressureDropStrategy();

    private BackpressureStrategies() {
        /* static methods */
    }

    public static BackpressureStrategy onBackpressureBuffer() {
        return DEFAULT_BUFFER_STRATEGY;
    }

    public static BackpressureStrategy onBackpressureBuffer(int bufferSize) {
        return new BackpressureBufferStrategy(bufferSize, DEFAULT_BUFFER_OVERFLOW_STRATEGY);
    }

    public static BackpressureStrategy onBackpressureBuffer(int bufferSize, BackpressureBufferOverflowStrategy overflowStrategy) {
        return new BackpressureBufferStrategy(bufferSize, overflowStrategy);
    }

    public static BackpressureStrategy onBackpressureDrop() {
        return BACKPRESSURE_DROP_STRATEGY;
    }

    public static BackpressureStrategy onBackpressureLatest() {
        return BACKPRESSURE_LATEST_STRATEGY;
    }

    public static BackpressureStrategy defaultBackpressureStrategy() {
        return onBackpressureLatest();
    }

    public static BackpressureStrategy none() {
        return BACKPRESSURE_NONE_STRATEGY;
    }

}
