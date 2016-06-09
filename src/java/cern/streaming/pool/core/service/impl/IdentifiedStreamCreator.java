/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamId;

public class IdentifiedStreamCreator<T> {

    private final StreamId<T> id;
    private final StreamCreator<T> creator;

    private IdentifiedStreamCreator(StreamId<T> id, StreamCreator<T> creator) {
        this.id = requireNonNull(id, "id must not be null.");
        this.creator = requireNonNull(creator, "creator must not be null.");
    }

    public static <T> IdentifiedStreamCreator<T> of(StreamId<T> id, StreamCreator<T> creator) {
        return new IdentifiedStreamCreator<T>(id, creator);
    }

    public StreamId<T> id() {
        return id;
    }

    public StreamCreator<T> creator() {
        return creator;
    }
}
