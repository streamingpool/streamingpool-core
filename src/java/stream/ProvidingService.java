/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

public interface ProvidingService {

    <T> void provide(StreamId<T> id, ReactStream<T> obs);
    
}
