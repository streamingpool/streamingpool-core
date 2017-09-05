package org.streamingpool.core.domain.backpressure;

public class BackpressureBufferStrategy implements BackpressureStrategy {

    private final int bufferSize;
    private final BackpressureBufferOverflowStrategy overflowStrategy;

    /**
     * Package protected, create using {@link BackpressureStrategies} utility class.
     */
    BackpressureBufferStrategy(int bufferSize, BackpressureBufferOverflowStrategy overflowStrategy) {
        this.bufferSize = bufferSize;
        this.overflowStrategy = overflowStrategy;
    }

    public int bufferSize() {
        return bufferSize;
    }

    public BackpressureBufferOverflowStrategy overflowStrategy() {
        return overflowStrategy;
    }

    public enum BackpressureBufferOverflowStrategy {
        DROP_LATEST,
        DROP_OLDEST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BackpressureBufferStrategy that = (BackpressureBufferStrategy) o;

        if (bufferSize != that.bufferSize) return false;
        return overflowStrategy == that.overflowStrategy;
    }

    @Override
    public int hashCode() {
        int result = bufferSize;
        result = 31 * result + (overflowStrategy != null ? overflowStrategy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BackpressureBufferStrategy[" +
            "bufferSize=" + bufferSize +
            ", overflowStrategy=" + overflowStrategy +
            ']';
    }
}
