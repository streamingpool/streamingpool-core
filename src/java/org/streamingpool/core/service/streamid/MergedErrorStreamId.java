package org.streamingpool.core.service.streamid;

import org.streamingpool.core.service.StreamId;

public class MergedErrorStreamId implements StreamId<Throwable> {

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
}
