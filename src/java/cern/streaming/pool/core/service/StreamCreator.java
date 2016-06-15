/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

/**
 * Interface that represents an entity that is able to create a specific {@link ReactStream}. The implementations of
 * this interfaces should know a priori the type and how to create a {@link ReactStream}.
 * 
 * @param <T> the type of data that the stream contains
 */
@FunctionalInterface
public interface StreamCreator<T> {

    /**
     * Creates a {@link ReactStream}. The provided {@link DiscoveryService} can be used to discover other
     * {@link ReactStream}s in order to combine them during the creation process.
     * </p>
     * <strong>NOTE</strong>: it is strongly discouraged the use of multiple threads inside this method (see
     * {@link StreamFactory} interface documentation).
     * 
     * @param discoveryService {@link DiscoveryService} which can be used by the factory to look up other streams
     *            ('upstream' of the one it will create)
     * @return the newly created {@link ReactStream}
     */
    ReactStream<T> createWith(DiscoveryService discoveryService);

}
