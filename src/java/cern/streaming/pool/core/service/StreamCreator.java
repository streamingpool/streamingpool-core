/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

@FunctionalInterface
public interface StreamCreator<T> {

    ReactStream<T> createWith(DiscoveryService discoveryService);

}
