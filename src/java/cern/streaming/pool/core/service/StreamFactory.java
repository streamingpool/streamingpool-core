/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

/**
 * This interface represents a factory for streams. An implementation of this interface is able to create a stream given
 * a {@link StreamId} and a {@link DiscoveryService}. During the creation of a stream it is allowed to discover other
 * streams. This allow the possibility to create streams that merge other streams while performing transformations.
 * </p>
 * <strong>NOTE</strong>: it is not allowed to discover other streams using the provided {@link DiscoveryService} in
 * multiple threads. In other words, do not use new threads inside the stream creation. The provided
 * {@link DiscoveryService} checks that subsequent discoveries are performed on the same thread, otherwise an exception
 * is rose. It is not possible to enforce the single thread execution in the {@link #create(StreamId, DiscoveryService)}
 * method, but using the {@link DiscoveryService} from different threads may lead to unpredictable behavior and can
 * cause deadlocks.
 */
@FunctionalInterface
public interface StreamFactory {

    /***
     * Given a {@link StreamId} and a {@link DiscoveryService} this methods attempt to create a {@link ReactStream}. If
     * the implementation of this interface does not know how to create a stream using the given {@link StreamId}, it
     * must return the {@code null}. The provided {@link DiscoveryService} can be used to discover other streams that
     * are needed in the creation process (stream combination, transformation, etc.)
     * </p>
     * <strong>NOTE</strong>: it is strongly discouraged the use of multiple threads inside this method (see
     * {@link StreamFactory} documentation).
     * 
     * @param id the id of the stream to create
     * @param discoveryService {@link DiscoveryService} which can be used by the factory to look up other streams
     *            ('upstream' of the one it will create)
     * @return the newly created stream or {@code null} if this factory cannot create the stream of the given id
     */
    <T> ReactStream<T> create(StreamId<T> id, DiscoveryService discoveryService);

}
