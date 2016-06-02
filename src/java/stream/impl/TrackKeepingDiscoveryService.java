/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import stream.DiscoveryService;
import stream.ReactStream;
import stream.StreamFactory;
import stream.StreamId;

public class TrackKeepingDiscoveryService implements DiscoveryService {

    private final Set<StreamId<?>> queriedIds = Collections
            .newSetFromMap(new ConcurrentHashMap<StreamId<?>, Boolean>());

    private final List<StreamFactory> factories;

    public TrackKeepingDiscoveryService(List<StreamFactory> factories) {
        this.factories = requireNonNull(factories, "factories must not be null");
    }

    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {
        boolean alreadyPresent = !(queriedIds.add(id));
        if (alreadyPresent) {
            throw new CycleInStreamDiscoveryDetectedException(
                    "Cycle detected when looking up streams. (At least) the following id was queried twice: " + id
                            + ". Number of queried ids without revolving: " + queriedIds.size());
        }
        return createFromFactories(id);
    }

    private <T> ReactStream<T> createFromFactories(StreamId<T> newId) {
        for (StreamFactory factory : factories) {
            ReactStream<T> stream = factory.create(newId, this);
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

}
