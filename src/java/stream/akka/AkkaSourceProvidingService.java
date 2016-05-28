/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.akka;

import akka.stream.javadsl.Source;
import stream.StreamId;

public interface AkkaSourceProvidingService {

    <T> void provide(StreamId<T> id, Source<T, ?> akkaSource);

}
