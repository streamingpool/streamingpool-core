/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

import stream.DiscoveryService;
import stream.ReactStream;

@FunctionalInterface
public interface StreamCreator<T> {

    ReactStream<T> createWith(DiscoveryService discoveryService);

}
