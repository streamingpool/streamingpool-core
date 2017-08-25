package org.streamingpool.core.service.streamid;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.factory.function.ZipCompositionFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import io.reactivex.functions.Function;

import static java.util.Objects.requireNonNull;

/**
 * Zips the items of the stream with the {@link ZipCompositionFunction}.
 * @param <S> type of the source stream
 * @param <T> type that function holds
 */
public class ZippedStreamId<S, T> implements StreamId<T> {
    private static final long serialVersionUID = 1L;
    private final Iterable<StreamId<S>> sourceStreamIds;
    private final Function<Object[], Optional<T>> function;

    private ZippedStreamId(Iterable<StreamId<S>> sourceStreamIds, Function<Object[], Optional<T>> function) {
        this.sourceStreamIds = sourceStreamIds;
        this.function = requireNonNull(function, "function must not be null");
    }

    public static <S, T> ZippedStreamId<S, T> zip(Iterable<StreamId<S>> streamIds,  Function<Object[], Optional<T>> function){
        Objects.requireNonNull(streamIds, "streamIds must not be null");
        return new ZippedStreamId<>(streamIds, function);
    }

    public Iterable<StreamId<S>> sourceStreamIds() { return sourceStreamIds; }

    public Function<Object[], Optional<T>> function() {
        return function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZippedStreamId<?, ?> that = (ZippedStreamId<?, ?>) o;

        if (sourceStreamIds != null ? !sourceStreamIds.equals(that.sourceStreamIds) : that.sourceStreamIds != null)
            return false;
        return function != null ? function.equals(that.function) : that.function == null;
    }

    @Override
    public int hashCode() {
        int result = sourceStreamIds != null ? sourceStreamIds.hashCode() : 0;
        result = 31 * result + (function != null ? function.hashCode() : 0);
        return result;
    }
}



