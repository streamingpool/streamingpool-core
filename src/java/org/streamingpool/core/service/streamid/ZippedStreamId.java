package org.streamingpool.core.service.streamid;

import io.reactivex.functions.BiFunction;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.factory.function.ZipCompositionFunction;

import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Zips the items of the stream with the {@link ZipCompositionFunction}.
 *
 * @param <S1> type of the first source stream
 * @param <S2> type of the second source stream
 * @param <T> type that function holds
 */
public class ZippedStreamId<S1, S2, T> implements StreamId<T> {
    private static final long serialVersionUID = 1L;
    private final StreamId<S1> sourceStreamId1;
    private final StreamId<S2> sourceStreamId2;
    private final BiFunction<S1, S2, Optional<T>> function;

    private ZippedStreamId(StreamId<S1> sourceStreamId1, StreamId<S2> sourceStreamId2,
                           BiFunction<S1, S2, Optional<T>> function) {
        this.sourceStreamId1 = sourceStreamId1;
        this.sourceStreamId2 = sourceStreamId2;
        this.function = requireNonNull(function, "function must not be null");
    }

    public static <S1, S2, T> ZippedStreamId<S1, S2, T> zip(StreamId<S1> sourceStreamId1, StreamId<S2> sourceStreamId2,
                                                            BiFunction<S1, S2, Optional<T>> function) {
        Objects.requireNonNull(sourceStreamId1, "sourceStreamId1 must not be null");
        Objects.requireNonNull(sourceStreamId2, "sourceStreamId2 must not be null");
        return new ZippedStreamId<>(sourceStreamId1, sourceStreamId2, function);
    }

    public StreamId<S1> sourceStreamId1() {
        return sourceStreamId1;
    }

    public StreamId<S2> sourceStreamId2() {
        return sourceStreamId2;
    }

    public BiFunction<S1, S2, Optional<T>> function() {
        return function;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZippedStreamId<?, ?, ?> that = (ZippedStreamId<?, ?, ?>) o;

        if (sourceStreamId1 != null ? !sourceStreamId1.equals(that.sourceStreamId1) : that.sourceStreamId1 != null)
            return false;
        if (sourceStreamId2 != null ? !sourceStreamId2.equals(that.sourceStreamId2) : that.sourceStreamId2 != null)
            return false;
        return function != null ? function.equals(that.function) : that.function == null;
    }

    @Override
    public int hashCode() {
        int result = sourceStreamId1 != null ? sourceStreamId1.hashCode() : 0;
        result = 31 * result + (sourceStreamId2 != null ? sourceStreamId2.hashCode() : 0);
        result = 31 * result + (function != null ? function.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ZippedStreamId{" +
                "sourceStreamId1=" + sourceStreamId1 +
                ", sourceStreamId2=" + sourceStreamId2 +
                ", function=" + function +
                '}';
    }
}