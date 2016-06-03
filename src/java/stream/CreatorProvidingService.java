/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import stream.impl.StreamCreator;

public interface CreatorProvidingService {

    <T> void provide(StreamId<T> id, StreamCreator<T> streamSupplier);

}
