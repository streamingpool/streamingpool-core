/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.akka;

import akka.stream.javadsl.Source;
import cern.streaming.pool.core.service.StreamId;

public interface AkkaSourceProvidingService {

    <T> void provide(StreamId<T> id, Source<T, ?> akkaSource);

}
