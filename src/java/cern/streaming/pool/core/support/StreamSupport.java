/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.support;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.CreatorProvidingService;
import cern.streaming.pool.core.service.ProvidingService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamId;

/**
 * Support interface for working with {@link ReactiveStream}s. Provides convenience and fluid methods. 
 * 
 * @author acalia
 */
public interface StreamSupport {

    <T> ReactiveStream<T> discover(StreamId<T> id);

    <T> OngoingProviding<T> provide(ReactiveStream<T> reactStream);

    <T> OngoingLazyProviding<T> provide(StreamCreator<T> reactStream);

    <T> Publisher<T> publisherFrom(StreamId<T> id);

    ProvidingService providingService();

    class OngoingProviding<T> {
        private final ReactiveStream<T> reactStream;
        private final ProvidingService providingService;

        public OngoingProviding(ProvidingService providingService, ReactiveStream<T> reactStream) {
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
