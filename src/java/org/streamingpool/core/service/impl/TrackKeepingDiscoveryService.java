// @formatter:off
/*
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package org.streamingpool.core.service.impl;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streamingpool.core.conf.PoolConfiguration;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.domain.backpressure.*;
import org.streamingpool.core.service.CycleInStreamDiscoveryDetectedException;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

/**
 * Special implementation of a {@link DiscoveryService}. It is able to discover streams recursively while preventing
 * cycles. Also, it is able to detect recursive discoveries from multiple threads, which is not allowed.
 */
public class TrackKeepingDiscoveryService implements DiscoveryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrackKeepingDiscoveryService.class);
    private final Action NOOP = () -> {};
    private final Set<StreamId<?>> idsOfStreamsUnderCreation;
    private final Set<StreamId<?>> idsDiscovered = new HashSet<>();
    private final List<StreamFactory> factories;
    private final PoolContent content;
    private final Thread contextOfExecution;
    private final PoolConfiguration poolConfiguration;

    public TrackKeepingDiscoveryService(List<StreamFactory> factories, PoolContent content, PoolConfiguration poolConfiguration) {
        this(factories, content, new HashSet<>(), Thread.currentThread(), poolConfiguration);
    }

    private TrackKeepingDiscoveryService(List<StreamFactory> factories, PoolContent content,
            Set<StreamId<?>> idsOfStreamsUnderCreation, Thread contextOfExecution, PoolConfiguration poolConfiguration) {
        this.factories = requireNonNull(factories, "factories must not be null");
        this.content = requireNonNull(content, "activeStreams must not be null");
        this.idsOfStreamsUnderCreation = Collections.unmodifiableSet(idsOfStreamsUnderCreation);
        this.contextOfExecution = requireNonNull(contextOfExecution, "contextOfExecution must not be null");
        this.poolConfiguration = poolConfiguration;
    }

    @Override
    public <T> Publisher<T> discover(StreamId<T> id) {
        checkSameContexOfExecution();
        checkForRecursiveCycles(id);

        /* Use the factories to create the new id in case is not present. */
        /* NOTE: Using lambda here since calling it from anywhere else would break the synchronization  */
        content.synchronousPutIfAbsent(id, () -> {
            for (StreamFactory factory : factories) {
                /* A fresh discovery service is used to keep track of the under-creation ids in a synchronized way  */
                TrackKeepingDiscoveryService innerDiscoveryService = cloneDiscoveryServiceIncluding(id);
                ErrorStreamPair<T> factoryResult = factory.create(id, innerDiscoveryService);

                if (factoryResult == null) {
                    throw new IllegalStateException(format(
                            "Factory %s returned null instead of a valid stream object for the id %s", factory, id));
                }

                if (factoryResult.isPresent()) {
                    LOGGER.info("Stream from id '{}' was successfully created by factory '{}'", id, factory);
                    innerDiscoveryService.idsDiscovered
                            .forEach(parent -> content.addDependency(id, parent));

                    return ErrorStreamPair.ofDataError(factoryResult.data(), factoryResult.error());
                }
            }
            return ErrorStreamPair.empty();
        });

        idsDiscovered.add(id);

        Publisher<T> publisher = getStreamWithIdOrElseThrow(id);
        Flowable<T> stream =  observeOnThreadPool(publisher);
        if(id instanceof BackpressureAware){
            stream = applyBackpressureStrategy(stream,((BackpressureAware)id).backpressureStrategy());
        }
        return stream;
    }

    private <T> Flowable<T> applyBackpressureStrategy(Flowable<T> source, BackpressureStrategy backpressureStrategy) {
        if(backpressureStrategy == null){
            return source;
        }
        if (backpressureStrategy instanceof BackpressureLatestStrategy) {
            return source.onBackpressureLatest();
        }
        if (backpressureStrategy instanceof BackpressureDropStrategy) {
            return source.onBackpressureDrop(i -> {});
        }
        if (backpressureStrategy instanceof BackpressureBufferStrategy) {
            BackpressureBufferStrategy bufferStrategy = (BackpressureBufferStrategy) backpressureStrategy;

            if (bufferStrategy.overflowStrategy() == BackpressureBufferStrategy.BackpressureBufferOverflowStrategy.DROP_LATEST) {
                return source.onBackpressureBuffer(bufferStrategy.bufferSize(), NOOP, BackpressureOverflowStrategy.DROP_LATEST);
            }
            if (bufferStrategy.overflowStrategy() == BackpressureBufferStrategy.BackpressureBufferOverflowStrategy.DROP_OLDEST) {
                return source.onBackpressureBuffer(bufferStrategy.bufferSize(), NOOP, BackpressureOverflowStrategy.DROP_OLDEST);
            }
            throw new IllegalArgumentException("Cannot determine the specified buffer overflow strategy: " + bufferStrategy);
        }
        if (backpressureStrategy instanceof BackpressureNoneStrategy) {
            return source;
        }
        throw new IllegalArgumentException("Cannot determine the specified backpressure strategy: " + backpressureStrategy);
    }

    private <T> Flowable<T> observeOnThreadPool(Publisher<T> publisher) {
        return Flowable.fromPublisher(publisher)
                .observeOn(poolConfiguration.getScheduler(), false, poolConfiguration.getObserveOnCapacity());
    }

    private <T> Publisher<T> getStreamWithIdOrElseThrow(StreamId<T> id) {
        Publisher<T> activeStream = content.get(id);

        if (activeStream == null) {
            throw new IllegalArgumentException(
                    "The stream for id '" + id + "' is neither present nor can it be created by any factory.");
        }
        return activeStream;
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

    private <T> TrackKeepingDiscoveryService cloneDiscoveryServiceIncluding(StreamId<T> newId) {
        Set<StreamId<?>> newSet = new HashSet<>(idsOfStreamsUnderCreation);
        newSet.add(newId);
        return new TrackKeepingDiscoveryService(factories, content, newSet, contextOfExecution, poolConfiguration);
    }

}
