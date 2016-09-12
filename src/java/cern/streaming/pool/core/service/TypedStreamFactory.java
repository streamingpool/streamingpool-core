/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import java.util.Optional;

/**
 * Typed version of a {@link StreamFactory}. A {@link StreamFactory} can create any type of streams, but sometimes there
 * are scenarios in which the type of the {@link StreamId} and the data type of the {@link ReactiveStream} to be created
 * are known. In those cases, {@link TypedStreamFactory} can simplify the development of a {@link StreamFactory}. The
 * {@link StreamFactory#create(StreamId, DiscoveryService)} method is provided with a default implementation that suites
 * most use cases.
 * 
 * @param <X> The type of objects published by the {@link ReactiveStream} and therefore used by the {@link StreamId}
 *            implementation
 * @param <T> The class of {@link StreamId} to be used in the constrution of {@link ReactiveStream}s.
 * @author maosinsk
 * @author timartin
 */
public interface TypedStreamFactory<X, T extends StreamId<X>> extends StreamFactory {

    /**
     * Default implementation of the {@link StreamFactory#create(StreamId, DiscoveryService)} method. Is uses
     * {@link #createReactiveStream(StreamId, DiscoveryService)} and {@link #streamIdClass()} to provide a more
     * developer friendly way of creating {@link ReactiveStream}s.
     */
    @SuppressWarnings("unchecked")
    @Override
    default <Y> Optional<ReactiveStream<Y>> create(StreamId<Y> id, DiscoveryService discoveryService) {
        if (!streamIdClass().isAssignableFrom(id.getClass())) {
            return Optional.empty();
        }

        return Optional.of((ReactiveStream<Y>) createReactiveStream((T) id, discoveryService));
    }

    /**
     * Actually create the {@link ReactiveStream} from the given id. It is much like
     * {@link StreamFactory#create(StreamId, DiscoveryService)} but with typed {@link StreamId}.
     */
    ReactiveStream<X> createReactiveStream(T id, DiscoveryService discoveryService);

    /**
     * Returns the class that this {@link TypedStreamFactory} is able to create.
     */
    Class<T> streamIdClass();

    /**
     * Given a generic {@link StreamId} this method returns a {@link Boolean} indicating whether the
     * {@link TypedStreamFactory} can create a {@link ReactiveStream}.
     *
     * @param id the {@link StreamId} of the {@link ReactiveStream} to be created.
     * @return true if the factory can create a @{ReactiveStream} out of the provided @{link StreamId}, false,
     *         otherswise.
     */
}
