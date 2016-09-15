/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamfactory.FilteredStreamFactory;

/**
 * Filter the items of the stream by applying the specified {@link #predicate()}.
 * 
 * @author acalia
 * @see FilteredStreamFactory
 * @param <T> the type of the data items
 */
public class FilteredStreamId<T> implements StreamId<T> {

    private final StreamId<T> sourceStreamId;
    private final Predicate<T> predicate;

    public static <T> FilteredStreamId<T> filterBy(StreamId<T> source, Predicate<T> predicate) {
        return new FilteredStreamId<>(source, predicate);
    }

    public FilteredStreamId(StreamId<T> source, Predicate<T> predicate) {
        this.sourceStreamId = requireNonNull(source, "source of the filtering must not be null");
        this.predicate = requireNonNull(predicate, "predicate of the filtering must not be null");
    }

    public StreamId<T> sourceStreamId() {
        return sourceStreamId;
    }

    public Predicate<T> predicate() {
        return predicate;
    }

    @Override
    public String toString() {
        return "FilteredStreamId [sourceStreamId=" + sourceStreamId + ", predicate=" + predicate + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        result = prime * result + ((sourceStreamId == null) ? 0 : sourceStreamId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FilteredStreamId<?> other = (FilteredStreamId<?>) obj;
        if (predicate == null) {
            if (other.predicate != null)
                return false;
        } else if (!predicate.equals(other.predicate))
            return false;
        if (sourceStreamId == null) {
            if (other.sourceStreamId != null)
                return false;
        } else if (!sourceStreamId.equals(other.sourceStreamId))
            return false;
        return true;
    }

}
