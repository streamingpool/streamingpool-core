/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

public interface DiscoveryService {

    <T> ReactStream<T> discover(StreamId<T> id);
    
}
