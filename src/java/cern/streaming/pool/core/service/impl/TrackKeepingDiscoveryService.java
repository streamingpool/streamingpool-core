/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import cern.streaming.pool.core.exception.CycleInStreamDiscoveryDetectedException;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;

public class TrackKeepingDiscoveryService implements DiscoveryService {

    private final Set<StreamId<?>> idsOfStreamsUnderCreation;

    private final List<StreamFactory> factories;
    private final ConcurrentMap<StreamId<?>, ReactStream<?>> activeStreams;
    private final Thread contextOfExecution;

    public TrackKeepingDiscoveryService(List<StreamFactory> factories,
            ConcurrentMap<StreamId<?>, ReactStream<?>> activeStreams) {
        this.factories = requireNonNull(factories, "factories must not be null");
        this.activeStreams = requireNonNull(activeStreams, "activeStreams must not be null");
        this.idsOfStreamsUnderCreation = new HashSet<>();
        this.contextOfExecution = Thread.currentThread();
    }

    public TrackKeepingDiscoveryService(List<StreamFactory> factories,
            ConcurrentMap<StreamId<?>, ReactStream<?>> activeStreams, Set<StreamId<?>> idsOfStreamsUnderCreation,
            Thread contextOfExecution) {
        this.factories = requireNonNull(factories, "factories must not be null");
        this.activeStreams = requireNonNull(activeStreams, "activeStreams must not be null");
        this.idsOfStreamsUnderCreation = Collections.unmodifiableSet(idsOfStreamsUnderCreation);
        this.contextOfExecution = requireNonNull(contextOfExecution, "contextOfExecution must not be null");
    }

    private <T> TrackKeepingDiscoveryService cloneCreatingId(StreamId<T> newId) {
        Set<StreamId<?>> newSet = new HashSet<>(idsOfStreamsUnderCreation);
        newSet.add(newId);
        return new TrackKeepingDiscoveryService(factories, activeStreams, newSet, contextOfExecution);
    }

    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {
        if (!Thread.currentThread().equals(contextOfExecution)) {
            throw new IllegalStateException(String.format(
                    "Invalid context of execution. It is not allowed to recursively discover streams from different threads. The allowed thread is [%s] while the current one is [%s]",
                    contextOfExecution.getName(), Thread.currentThread().getName()));
        }

        if (idsOfStreamsUnderCreation.contains(id)) {
            throw new CycleInStreamDiscoveryDetectedException(
                    "Cycle detected when looking up streams. (At least) the following id was queried twice: " + id
                            + ". Number of queried ids without revolving: " + idsOfStreamsUnderCreation.size());
        }

        if (!activeStreams.containsKey(id)) {
            synchronized (activeStreams) {
                if (!activeStreams.containsKey(id)) {
                    ReactStream<T> reactStream = createFromFactories(id);
                    if (reactStream != null) {
                        activeStreams.put(id, reactStream);
                    }
                }
            }
        }

        /* This cast is safe, because we only allow to add the right types into the map */
        @SuppressWarnings("unchecked")
        ReactStream<T> activeStream = (ReactStream<T>) activeStreams.get(id);

        if (activeStream == null) {
            throw new IllegalArgumentException(
                    "The stream for id " + id + "is neither present nor can it be created by any factory.");
        }
        return activeStream;
    }

    private <T> ReactStream<T> createFromFactories(StreamId<T> newId) {
        for (StreamFactory factory : factories) {
            ReactStream<T> stream = factory.create(newId, cloneCreatingId(newId));
            if (stream != null) {
                return stream;
            }
        }
        return null;
    }

}
