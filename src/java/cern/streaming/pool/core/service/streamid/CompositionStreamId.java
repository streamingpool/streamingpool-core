package cern.streaming.pool.core.service.streamid;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Generic implementation of {@link StreamId} which in conjunction with the
 * {@link cern.streaming.pool.core.service.streamfactory.CompositionStreamFactory} allows for the easy creation of
 * general purpose streams based on composition of streams. This class is experimental.
 *
 * @param <X> The type of objects emitted by the source {@link ReactiveStream}s.
 * @param <T> The type of objects emitted by the new created {@link ReactiveStream}.
 * @author timartin
 */
public final class CompositionStreamId<X, T> implements StreamId<T> {
    private final List<StreamId<X>> sourceStreamIds;
    private final Function<List<ReactiveStream<X>>, ReactiveStream<T>> transformation;

    /**
     * Creates a {@link CompositionStreamId} with the provided sourceStreamId and function.
     *
     * @param sourceStreamId A {@link StreamId} that identifies the {@link ReactiveStream} passed to the
     *                       transformation function.
     * @param transformation The transformation {@link Function} to be used on the {@link ReactiveStream} identified by
     *                       the provided {@link StreamId}.
     */
    public CompositionStreamId(StreamId<X> sourceStreamId, Function<List<ReactiveStream<X>>, ReactiveStream<T>> transformation) {
        this(Collections.singletonList(sourceStreamId), transformation);
    }

    /**
     * Creates a {@link CompositionStreamId} with the provided sourceStreamIds and function.
     *
     * @param sourceStreamIds A {@link List} of {@link StreamId}s that will identifies the {@link ReactiveStream} passed
     *                        to the transformation function.
     * @param transformation  The transformation {@link Function} to be used on the {@link ReactiveStream}s identified by
     *                        the provided {@link List} of {@link StreamId}s.
     */
    public CompositionStreamId(List<StreamId<X>> sourceStreamIds,
                               Function<List<ReactiveStream<X>>, ReactiveStream<T>> transformation) {
        this.sourceStreamIds = sourceStreamIds;
        this.transformation = transformation;
    }

    public List<StreamId<X>> sourceStreamIds() {
        return sourceStreamIds;
    }

    public Function<List<ReactiveStream<X>>, ReactiveStream<T>> transformation() {
        return transformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositionStreamId<?, ?> that = (CompositionStreamId<?, ?>) o;

        if (sourceStreamIds != null ? !sourceStreamIds.equals(that.sourceStreamIds) : that.sourceStreamIds != null)
            return false;
        return transformation != null ? transformation.equals(that.transformation) : that.transformation == null;

    }

    @Override
    public int hashCode() {
        int result = sourceStreamIds != null ? sourceStreamIds.hashCode() : 0;
        result = 31 * result + (transformation != null ? transformation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompositionStreamId{" +
                "sourceStreamIds=" + sourceStreamIds +
                ", transformation=" + transformation +
                '}';
    }
}
