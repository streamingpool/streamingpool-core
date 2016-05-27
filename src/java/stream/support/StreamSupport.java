/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.support;

import java.util.function.Supplier;

import org.reactivestreams.Publisher;

import stream.ReactStream;
import stream.StreamId;
import stream.testing.AbstractStreamTest;
import stream.testing.AbstractStreamTest.OngoingLazyProviding;
import stream.testing.AbstractStreamTest.OngoingProviding;

public interface StreamSupport {

    <T> ReactStream<T> discover(StreamId<T> id);

    <T> OngoingProviding<T> provide(ReactStream<T> reactStream);

    <T> OngoingLazyProviding<T> provide(Supplier<ReactStream<T>> reactStream);

    <T> Publisher<T> publisherFrom(StreamId<T> id);

}
