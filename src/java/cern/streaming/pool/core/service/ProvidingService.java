/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

/**
 * Interface that is able to provide {@link ReactiveStream}. The provided stream can be then discovered using the
 * {@link DiscoveryService}. There are no restrictions on how the implementations actually provide the streams but this
 * process must be transparent to the user.
 * 
 * @see DiscoveryService
 */
@FunctionalInterface
public interface ProvidingService {

    /**
     * Provides the stream with the specified id. From the moment the stream is provided, it can be accessed from
     * {@link DiscoveryService} using the same {@link StreamId}.
     * 
     * @param id the {@link StreamId} that identifies the specified stream
     * @param stream the {@link ReactiveStream} to be provided
     */
    <T> void provide(StreamId<T> id, ReactiveStream<T> stream);

}
