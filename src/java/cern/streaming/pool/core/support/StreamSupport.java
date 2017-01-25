// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

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
