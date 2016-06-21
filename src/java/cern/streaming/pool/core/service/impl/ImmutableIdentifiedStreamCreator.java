/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamId;

/**
 * Immutable implementation of a {@link IdentifiedStreamCreator}.
 * 
 * @author acalia
 * @param <T> the type of the data that the stream created using the {@link StreamCreator} will have
 */
public class ImmutableIdentifiedStreamCreator<T> implements IdentifiedStreamCreator<T> {

    private final StreamId<T> id;
    private final StreamCreator<T> creator;

    protected ImmutableIdentifiedStreamCreator(StreamId<T> id, StreamCreator<T> creator) {
        this.id = requireNonNull(id, "id must not be null.");
        this.creator = requireNonNull(creator, "creator must not be null.");
    }

    public static <T> IdentifiedStreamCreator<T> of(StreamId<T> id, StreamCreator<T> creator) {
        return new ImmutableIdentifiedStreamCreator<>(id, creator);
    }

    @Override
    public StreamId<T> getId() {
        return id;
    }

    @Override
    public StreamCreator<T> getCreator() {
        return creator;
    }
}
