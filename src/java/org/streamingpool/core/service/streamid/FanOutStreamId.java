package org.streamingpool.core.service.streamid;

import static java.util.Objects.requireNonNull;
import static org.streamingpool.core.domain.backpressure.BackpressureStrategies.defaultBackpressureStrategy;

import java.util.Objects;

import org.streamingpool.core.domain.backpressure.BackpressureAware;
import org.streamingpool.core.domain.backpressure.BackpressureStrategy;
import org.streamingpool.core.service.StreamId;

public class FanOutStreamId<T> implements StreamId<T>, BackpressureAware {
    private static final long serialVersionUID = 1L;

    private final StreamId<T> target;
    private final BackpressureStrategy backpressureStrategy;

    private FanOutStreamId(StreamId<T> target, BackpressureStrategy backpressureStrategy) {
        this.target = requireNonNull(target);
        this.backpressureStrategy = requireNonNull(backpressureStrategy);
    }

    public static <T> FanOutStreamId<T> fanOut(StreamId<T> target, BackpressureStrategy backpressureStrategy) {
        return new FanOutStreamId<>(target, backpressureStrategy);
    }
    public static <T> FanOutStreamId<T> fanOut(StreamId<T> target) {
        return new FanOutStreamId<>(target, defaultBackpressureStrategy());
    }

    public StreamId<T> target() {
        return target;
    }

    @Override
    public BackpressureStrategy backpressureStrategy() {
        return backpressureStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FanOutStreamId<?> that = (FanOutStreamId<?>) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(backpressureStrategy, that.backpressureStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, backpressureStrategy);
    }

    @Override
    public String toString() {
        return "FanOutStreamId{" +
                "target=" + target +
                ", backpressureStrategy=" + backpressureStrategy +
                '}';
    }
}
