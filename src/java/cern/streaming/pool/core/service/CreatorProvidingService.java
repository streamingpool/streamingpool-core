/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

public interface CreatorProvidingService {

    <T> void provide(StreamId<T> id, StreamCreator<T> streamSupplier);

}
