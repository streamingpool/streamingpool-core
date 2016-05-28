/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.support;

import java.util.function.Supplier;

import org.reactivestreams.Publisher;

import stream.LazyProvidingService;
import stream.ProvidingService;
import stream.ReactStream;
import stream.StreamId;

public interface StreamSupport {

    <T> ReactStream<T> discover(StreamId<T> id);

    <T> OngoingProviding<T> provide(ReactStream<T> reactStream);

    <T> OngoingLazyProviding<T> provide(Supplier<ReactStream<T>> reactStream);

    <T> Publisher<T> publisherFrom(StreamId<T> id);

    class OngoingProviding<T> {
        private final ReactStream<T> reactStream;
        private final ProvidingService providingService;

        public OngoingProviding(ProvidingService providingService, ReactStream<T> reactStream) {
            this.providingService = providingService;
            this.reactStream = reactStream;
        }

        public void as(StreamId<T> id) {
            providingService.provide(id, reactStream);
        }

    }

    class OngoingLazyProviding<T> {
        private final Supplier<ReactStream<T>> reactStream;
        private final LazyProvidingService providingService;

        public OngoingLazyProviding(LazyProvidingService providingService, Supplier<ReactStream<T>> reactStream) {
            this.providingService = providingService;
            this.reactStream = reactStream;
        }

        public void as(StreamId<T> id) {
            providingService.provide(id, reactStream);
        }

    }

}
