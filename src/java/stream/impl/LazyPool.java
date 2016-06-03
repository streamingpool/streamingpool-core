/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

import java.util.List;

import stream.ReactStream;
import stream.StreamFactory;
import stream.StreamId;

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
