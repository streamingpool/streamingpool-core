/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactStream;

@FunctionalInterface
public interface StreamCreator<T> {

    ReactStream<T> createWith(DiscoveryService discoveryService);

}
