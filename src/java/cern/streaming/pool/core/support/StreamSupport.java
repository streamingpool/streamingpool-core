/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.support;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.CreatorProvidingService;
import cern.streaming.pool.core.service.ProvidingService;
import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamId;

/**
 * Support interface for working with {@link Publisher}s. Provides convenience and fluid methods.
 * 
 * @author acalia
 */
public interface StreamSupport {

    <T> Publisher<T> discover(StreamId<T> id);

    <T> OngoingProviding<T> provide(Publisher<T> reactStream);

    <T> OngoingLazyProviding<T> provide(StreamCreator<T> reactStream);

    ProvidingService providingService();

    class OngoingProviding<T> {
        private final Publisher<T> reactStream;
        private final ProvidingService providingService;

        public OngoingProviding(ProvidingService providingService, Publisher<T> reactStream) {
            this.providingService = providingService;
            this.reactStream = reactStream;
        }

        public void as(StreamId<T> id) {
            providingService.provide(id, reactStream);
        }

        public StreamId<T> withUniqueStreamId() {
            StreamId<T> uniqueStreamId = generateUniqueId();
            this.as(uniqueStreamId);
            return uniqueStreamId;
        }

        private static <T> StreamId<T> generateUniqueId() {
            return new StreamId<T>() {
                @Override
                public String toString() {
                    return "Generated unique StreamId from StreamSupport";
                }
            };
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
