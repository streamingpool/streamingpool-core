/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.akka;

import akka.stream.javadsl.Source;
import cern.streaming.pool.core.service.ProvidingService;
import cern.streaming.pool.core.service.StreamId;

/**
 * Specific providing service for Akka streams. The main difference from {@link ProvidingService} is that it accepts
 * directly the {@link Source} to be used to create the stream.
 */
public interface AkkaSourceProvidingService {

    <T> void provide(StreamId<T> id, Source<T, ?> akkaSource);

}
