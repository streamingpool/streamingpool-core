/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

/**
 * This interface represents a factory for {@link ReactiveStream}s. An implementation of this interface is able to
 * create a stream given an implementation of {@link StreamId} and a {@link DiscoveryService}. During the creation of a
 * stream it is allowed to discover other streams. This allow the possibility to create streams that merge other streams
 * while performing transformations.
 * </p>
 * <strong>NOTE</strong>: it is not allowed to discover other streams using the provided {@link DiscoveryService} in
 * multiple threads. In other words, do not use new threads inside the stream creation. The provided
 * {@link DiscoveryService} checks that subsequent discoveries are performed on the same thread, otherwise an exception
 * is rose. It is not possible to enforce the single thread execution in the {@link #create(StreamId, DiscoveryService)}
 * method, but using the {@link DiscoveryService} from different threads may lead to unpredictable behavior and can
 * cause deadlocks.
 * @param <X> The type of objects published by the {@link ReactiveStream} and therefore used by the {@link StreamId}
 *           implementation
 * @param <T> The class of {@link StreamId} to be used in the constrution of {@link ReactiveStream}s.
 * @author maosinsk
 * @author timartin
 */
@FunctionalInterface
public interface StreamFactory <X, T extends StreamId<X>>{

    /***
     * Given an implementation of {@link StreamId} and a {@link DiscoveryService} this method creates a
     * {@link ReactiveStream<X>}. The provided {@link DiscoveryService} can be used to discover other streams that are
     * needed in the creation process (stream combination, transformation, etc.)
     * </p>
     * <strong>NOTE</strong>: it is strongly discouraged the use of multiple threads inside this method (see
     * {@link StreamFactory} documentation).
     * 
     * @param id the id of the stream to create
     * @param discoveryService {@link DiscoveryService} which can be used by the factory to look up other streams
     *            ('upstream' of the one it will create)
     * @return the newly created stream or {@code null} if this factory cannot create the stream of the given id
     */
    ReactiveStream<X> create(T id, DiscoveryService discoveryService);

    /**
     * Given a generic {@link StreamId} this method returns a {@link Boolean} indicating whether the {@link StreamFactory}
     * can create a {@link ReactiveStream}.
     *
     * @param id the {@link StreamId} of the {@link ReactiveStream} to be created.
     * @return
     */
    default boolean canCreate(StreamId id) {
        return id instanceof StreamId;
    }
}
