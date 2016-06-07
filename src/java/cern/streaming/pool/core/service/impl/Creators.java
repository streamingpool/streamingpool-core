/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import java.util.Objects;
import java.util.function.Supplier;

import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamId;

public class Creators {

    private Creators() {
        /* Only static methods */
    }

    public static final <T> OngoingCreatorCreation<T> create(StreamCreator<T> creator) {
        return new OngoingCreatorCreation<>(creator);
    }

    public static final <T> OngoingCreatorCreation<T> create(Supplier<ReactStream<T>> supplier) {
        return new OngoingCreatorCreation<T>(discovery -> supplier.get());
    }

    public static class OngoingCreatorCreation<T> {
        private final StreamCreator<T> streamCreator;

        public OngoingCreatorCreation(StreamCreator<T> streamCreator) {
            this.streamCreator = Objects.requireNonNull(streamCreator, "streamCreator must not be null.");
        }

        public IdentifiedStreamCreator<T> as(StreamId<T> streamId) {
            return IdentifiedStreamCreator.of(streamId, streamCreator);
        }
    }

}
