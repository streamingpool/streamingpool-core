package org.streamingpool.core.service.streamid;

import org.streamingpool.core.service.StreamId;

import java.util.Objects;

/**
 * Given a source {@link StreamId} it published all the (deflected) errors of its ancestors
 */
public class MergedErrorStreamId implements StreamId<Throwable> {
    private static final long serialVersionUID = 1L;

    private final StreamId<?> source;

    private MergedErrorStreamId(StreamId<?> source) {
        this.source = source;
    }

    public static MergedErrorStreamId mergeErrorsStartingFrom(StreamId<?> source) {
        return new MergedErrorStreamId(source);
    }

    public StreamId<?> getSourceStreamId() {
        return source;
    }

    @Override
    public String toString() {
        return "MergedErrorStreamId{" +
                "source=" + source +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MergedErrorStreamId that = (MergedErrorStreamId) o;
        return Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source);
    }
}
