/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

/**
 * Interface used for providing a stream using a {@link StreamCreator}.
 * 
 * @see StreamCreator
 */
@FunctionalInterface
public interface CreatorProvidingService {

    /**
     * Provides a {@link StreamCreator} associated to the specified id.
     * 
     * @param id the identifier of the stream that the {@link StreamCreator} will create
     * @param streamSupplier the {@link StreamCreator} that is able to create the {@link ReactiveStream} specified using
     *            the given id
     */
    <T> void provide(StreamId<T> id, StreamCreator<T> streamSupplier);

}
