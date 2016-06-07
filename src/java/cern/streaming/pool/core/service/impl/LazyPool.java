/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import java.util.List;

import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;

public class LazyPool extends SimplePool {

    private final List<StreamFactory> factories;

    public LazyPool(List<StreamFactory> factories) {
        this.factories = factories;
    }

    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {
        return new TrackKeepingDiscoveryService(factories, activeStreams()).discover(id);
    }

}
