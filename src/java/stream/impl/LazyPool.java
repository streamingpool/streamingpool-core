/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

import java.util.List;

import stream.ReactStream;
import stream.StreamFactory;
import stream.StreamId;

public class LazyPool extends SimplePool {

    private final List<StreamFactory> factories;

    public LazyPool(List<StreamFactory> factories) {
        this.factories = factories;
    }

    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {
        /* This cast is safe, because we only allow to add the right types into the map */
        @SuppressWarnings("unchecked")
        ReactStream<T> activeStream = (ReactStream<T>) activeStreams().computeIfAbsent(id, this::create);

        if (activeStream == null) {
            throw new IllegalArgumentException(
                    "The stream for id " + id + "is neither present nor can it be created by any factory.");
        }
        return activeStream;
    }

    private <T> ReactStream<T> create(StreamId<T> newId) {
        for (StreamFactory factory : factories) {
            ReactStream<T> stream = factory.create(newId);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

}
