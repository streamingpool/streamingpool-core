/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.ReactStream;

public class SimpleReactStream <T> implements ReactStream <T> {
    
    private final Publisher<T> source;

    public SimpleReactStream(Publisher<T> source) {
        this.source = source;
    }

    public Publisher<T> getSource() {
        return source;
    }
    
}
