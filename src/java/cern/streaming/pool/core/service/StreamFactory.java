/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import java.util.Optional;

public interface StreamFactory {

    /**
     * @param id
     * @param discoveryService
     * @return
     */
    <T> Optional<ReactiveStream<T>> create(StreamId<T> id, DiscoveryService discoveryService);

}
