package org.streamingpool.core.service.streamid;

import org.streamingpool.core.domain.backpressure.BackpressureStrategy;
import org.streamingpool.core.service.StreamId;

import static java.util.Objects.requireNonNull;
import static org.streamingpool.core.domain.backpressure.BackpressureStrategies.defaultBackpressureStrategy;

public class FanOutStreamId<T> implements StreamId<T> {

    private final StreamId<T> target;
    private final BackpressureStrategy backpressureStrategy;

    private FanOutStreamId(StreamId<T> target, BackpressureStrategy backpressureStrategy) {
        this.target = requireNonNull(target);
        this.backpressureStrategy = requireNonNull(backpressureStrategy);
    }

    public static <T> FanOutStreamId<T> fanOut(StreamId<T> target, BackpressureStrategy backpressureStrategy) {
        return new FanOutStreamId<T>(target, backpressureStrategy);
    }
    public static <T> FanOutStreamId<T> fanOut(StreamId<T> target) {
        return new FanOutStreamId<T>(target, defaultBackpressureStrategy());
    }

    public StreamId<T> target() {
        return target;
    }

    public BackpressureStrategy backpressureStrategy() {
        return backpressureStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FanOutStreamId<?> that = (FanOutStreamId<?>) o;

        return target != null ? target.equals(that.target) : that.target == null;
    }

    @Override
    public int hashCode() {
        return target != null ? target.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FanOutStreamId[" +
            "target=" + target +
            ']';
    }
}
