package org.streamingpool.core.domain.backpressure;

import org.streamingpool.core.domain.backpressure.BackpressureBufferStrategy.BackpressureBufferOverflowStrategy;

public final class BackpressureStrategies {

    public static final BackpressureBufferOverflowStrategy DEFAULT_BUFFER_OVERFLOW_STRATEGY = BackpressureBufferOverflowStrategy.DROP_LATEST;
    public static final int DEFAULT_BUFFER_SIZE = 100;

    private BackpressureStrategies() {
        /* static methods */
    }

    public static BackpressureStrategy onBackpressureBuffer() {
        return new BackpressureBufferStrategy(DEFAULT_BUFFER_SIZE, DEFAULT_BUFFER_OVERFLOW_STRATEGY);
    }

    public static BackpressureStrategy onBackpressureBuffer(int bufferSize) {
        return new BackpressureBufferStrategy(bufferSize, DEFAULT_BUFFER_OVERFLOW_STRATEGY);
    }

    public static BackpressureStrategy onBackpressureBuffer(int bufferSize, BackpressureBufferOverflowStrategy overflowStrategy) {
        return new BackpressureBufferStrategy(bufferSize, overflowStrategy);
    }

    public static BackpressureStrategy onBackpressureDrop() {
        return new BackpressureDropStrategy();
    }

    public static BackpressureStrategy onBackpressureLatest() {
        return new BackpressureLatestStrategy();
    }

    public static BackpressureStrategy defaultBackpressureStrategy() {
        return onBackpressureLatest();
    }

}
