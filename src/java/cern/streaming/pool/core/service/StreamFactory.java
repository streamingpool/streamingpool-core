/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

public interface StreamFactory {

    /***
     * @param id the id of the stream to create
     * @param discoveryService A discovery service which can be used by the factory to look up other streams ('upstream'
     *            of the one it will create)
     * @return the new stream or {@code null} if this factory cannot create the stream of the given id
     */
    <T> ReactStream<T> create(StreamId<T> id, DiscoveryService discoveryService);

}
