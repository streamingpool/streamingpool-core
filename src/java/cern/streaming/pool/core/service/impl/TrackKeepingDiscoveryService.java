/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.streaming.pool.core.service.CycleInStreamDiscoveryDetectedException;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;

/**
 * Special implementation of a {@link DiscoveryService}. It is able to discover streams recursively while preventing
 * cycles. Also, it is able to detect recursive discoveries from multiple threads, which is not allowed.
 */
public class TrackKeepingDiscoveryService implements DiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackKeepingDiscoveryService.class);

    private final Set<StreamId<?>> idsOfStreamsUnderCreation;
    private final List<StreamFactory> factories;
    private final ConcurrentMap<StreamId<?>, ReactiveStream<?>> activeStreams;
    private final Thread contextOfExecution;

    public TrackKeepingDiscoveryService(List<StreamFactory> factories,
            ConcurrentMap<StreamId<?>, ReactiveStream<?>> activeStreams) {
        this(factories, activeStreams, new HashSet<>(), Thread.currentThread());
    }

    public TrackKeepingDiscoveryService(List<StreamFactory> factories,
            ConcurrentMap<StreamId<?>, ReactiveStream<?>> activeStreams, Set<StreamId<?>> idsOfStreamsUnderCreation,
            Thread contextOfExecution) {
        this.factories = requireNonNull(factories, "factories must not be null");
        this.activeStreams = requireNonNull(activeStreams, "activeStreams must not be null");
        this.idsOfStreamsUnderCreation = Collections.unmodifiableSet(idsOfStreamsUnderCreation);
        this.contextOfExecution = requireNonNull(contextOfExecution, "contextOfExecution must not be null");
    }

    @Override
    public <T> ReactiveStream<T> discover(StreamId<T> id) {
        checkSameContexOfExecution();
        checkForRecursiveCycles(id);

        synchronouslyCreateStreamIfAbsent(id);

        return getStreamWithIdOrElseThrow(id);
    }

    private <T> ReactiveStream<T> getStreamWithIdOrElseThrow(StreamId<T> id) {
        /* This cast is safe, because we only allow to add the right types into the map */
        @SuppressWarnings("unchecked")
        ReactiveStream<T> activeStream = (ReactiveStream<T>) activeStreams.get(id);

        if (activeStream == null) {
            throw new IllegalArgumentException(
                    "The stream for id '" + id + "' is neither present nor can it be created by any factory.");
        }
        return activeStream;
    }

    private <T> void synchronouslyCreateStreamIfAbsent(StreamId<T> id) {
        if (!activeStreams.containsKey(id)) {
            synchronized (activeStreams) {
                if (!activeStreams.containsKey(id)) {
                    ReactiveStream<T> reactStream = createFromFactories(id);
                    if (reactStream != null) {
                        activeStreams.put(id, reactStream);
                    }
                }
            }
        }
    }

    private <T> void checkForRecursiveCycles(StreamId<T> id) {
        if (idsOfStreamsUnderCreation.contains(id)) {
            throw new CycleInStreamDiscoveryDetectedException(
                    format("Cycle detected when looking up streams. (At least) the following id was queried twice: %s."
                            + " Number of queried ids without revolving: %s", id, idsOfStreamsUnderCreation.size()));
        }
    }

    private void checkSameContexOfExecution() {
        if (!Thread.currentThread().equals(contextOfExecution)) {
            throw new IllegalStateException(format(
                    "Invalid context of execution. It is not allowed to recursively discover streams from different"
                            + " threads. The allowed thread is [%s] while the current one is [%s]",
                    contextOfExecution.getName(), Thread.currentThread().getName()));
        }
    }

    private <T> TrackKeepingDiscoveryService cloneTrackKeepingDiscoveryServiceIncluding(StreamId<T> newId) {
        Set<StreamId<?>> newSet = new HashSet<>(idsOfStreamsUnderCreation);
        newSet.add(newId);
        return new TrackKeepingDiscoveryService(factories, activeStreams, newSet, contextOfExecution);
    }

    private <T> ReactiveStream<T> createFromFactories(StreamId<T> newId) {
        for (StreamFactory factory : factories) {
            ReactiveStream<T> stream = factory.create(newId, cloneTrackKeepingDiscoveryServiceIncluding(newId));
            if (stream != null) {
                LOGGER.debug("Stream of id '{}' was created by factory '{}'.", newId, factory);
                return stream;
            }
        }
        return null;
    }

}
