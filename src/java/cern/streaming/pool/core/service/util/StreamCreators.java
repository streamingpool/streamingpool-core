/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.util;

import java.util.Objects;
import java.util.function.Supplier;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.IdentifiedStreamCreator;
import cern.streaming.pool.core.service.impl.ImmutableIdentifiedStreamCreator;

public final class StreamCreators {

    private StreamCreators() {
        /* Only static methods */
    }

    public static <T> OngoingCreatorCreation<T> create(StreamCreator<T> creator) {
        return new OngoingCreatorCreation<>(creator);
    }

    public static <T> OngoingCreatorCreation<T> create(Supplier<ReactiveStream<T>> supplier) {
        return new OngoingCreatorCreation<T>(discovery -> supplier.get());
    }

    public static class OngoingCreatorCreation<T> {
        private final StreamCreator<T> streamCreator;

        public OngoingCreatorCreation(StreamCreator<T> streamCreator) {
            this.streamCreator = Objects.requireNonNull(streamCreator, "streamCreator must not be null.");
        }

        public IdentifiedStreamCreator<T> as(StreamId<T> streamId) {
            return ImmutableIdentifiedStreamCreator.of(streamId, streamCreator);
        }
    }

}
