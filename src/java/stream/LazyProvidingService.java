/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import java.util.function.Supplier;

public interface LazyProvidingService {

    <T> void provide(StreamId<T> id, Supplier<ReactStream<T>> streamSupplier);

}
