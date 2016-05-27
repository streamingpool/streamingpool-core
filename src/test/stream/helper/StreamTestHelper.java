/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.helper;

import java.util.function.Supplier;

import org.reactivestreams.Publisher;

import stream.ReactStream;
import stream.StreamId;
import stream.helper.AbstractStreamTest.OngoingProviding;
import stream.helper.AbstractStreamTest.OngoingLazyProviding;

public interface StreamTestHelper {

    <T> ReactStream<T> discover(StreamId<T> id);
    
    <T> OngoingProviding<T> provide(ReactStream<T> reactStream);
    <T> OngoingLazyProviding<T> provide(Supplier<ReactStream<T>> reactStream);
    
    <T> Publisher<T> publisherFrom(StreamId<T> id);
    
}
