/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.support;

import org.reactivestreams.Publisher;

import stream.CreatorProvidingService;
import stream.ProvidingService;
import stream.ReactStream;
import stream.StreamId;
import stream.impl.StreamCreator;

public interface StreamSupport {

    <T> ReactStream<T> discover(StreamId<T> id);

    <T> OngoingProviding<T> provide(ReactStream<T> reactStream);

    <T> OngoingLazyProviding<T> provide(StreamCreator<T> reactStream);

    <T> Publisher<T> publisherFrom(StreamId<T> id);

    ProvidingService providingService();

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
        private final StreamCreator<T> reactStream;
        private final CreatorProvidingService providingService;

        public OngoingLazyProviding(CreatorProvidingService providingService, StreamCreator<T> reactStream) {
            this.providingService = providingService;
            this.reactStream = reactStream;
        }

        public void as(StreamId<T> id) {
            providingService.provide(id, reactStream);
        }

    }

}
