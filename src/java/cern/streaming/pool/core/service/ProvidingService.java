/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

public interface ProvidingService {

    <T> void provide(StreamId<T> id, ReactStream<T> stream);
    
}
