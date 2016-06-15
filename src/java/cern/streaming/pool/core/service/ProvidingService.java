/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

/**
 * Interface that is able to provide {@link ReactStream}. The provided stream can be then discovered using the
 * {@link DiscoveryService}. There are no restrictions on how the implementations actually provide the streams but this
 * process must be transparent to the user.
 * 
 * @see DiscoveryService
 */
@FunctionalInterface
public interface ProvidingService {

    /**
     * Provides in the streaming-pool system the stream with the specified id. From the moment the stream is provided,
     * it will be accessed from {@link DiscoveryService} usign the same {@link StreamId}
     * 
     * @param id the {@link StreamId} that identifies the specified stream
     * @param stream the {@link ReactStream} to be provided
     */
    <T> void provide(StreamId<T> id, ReactStream<T> stream);

}
