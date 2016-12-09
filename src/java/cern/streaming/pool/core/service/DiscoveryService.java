/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import org.reactivestreams.Publisher;

/**
 * Interface used to discover {@link Publisher}.
 * 
 * @see ProvidingService
 */
@FunctionalInterface
public interface DiscoveryService {

    /**
     * Given a {@link StreamId}, this method returns the correspondent {@link Publisher}. This method should not
     * return null, instead is preferred to throw a specific exception in the case the given id is not present in the
     * system. From the API level, this behavior is not forced.
     * 
     * @param id the identifier of the stream to be discovered
     * @return the discovered {@link Publisher}
     */
    <T> Publisher<T> discover(StreamId<T> id);

}
