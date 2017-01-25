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

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.rx.RxStreams;

/**
 * Support interface that provides convenience methods for working with streams.
 * 
 * @author acalia
 */
public interface StreamCollectingSupport extends RxStreamSupport {

    /**
     * The given id will be discovered and the stream can be manipulated using {@link OngoingBlockingCollecting}
     * methods. This is useful for simple use cases in which is not needed to use a more advanced ReactiveStreams
     * library.
     */
    default <T> OngoingBlockingCollecting<T> from(StreamId<T> streamId) {
        return new OngoingBlockingCollecting<>(streamId, this);
    }

    class OngoingBlockingCollecting<T> {
        private int skip = 0;
        private final StreamId<T> streamId;
        private final RxStreamSupport support;

        private OngoingBlockingCollecting(StreamId<T> streamId, RxStreamSupport support) {
            this.support = requireNonNull(support, "RxStreamSupport must not be null");
            this.streamId = requireNonNull(streamId, "streamId must not be null");
            if (skip < 0) {
                throw new IllegalArgumentException(
                        "The number of acquisitions to skip must be >=0, but was " + skip + ".");
            }
        }

        /**
         * Instruct the {@link OngoingBlockingCollecting} to skip the given number of elements.
         */
        public OngoingBlockingCollecting<T> skip(int itemsToSkip) {
            this.skip = itemsToSkip;
            return this;
        }

        /**
         * No-op method for fluent method chaining
         */
        public OngoingBlockingCollecting<T> and() {
            return this;
        }

        /**
         * End the {@link OngoingBlockingCollecting}. It discovers the stream with the given id, it applies all the
         * registered operations and returns the value.
         */
        public T awaitNext() {
            return RxStreams.awaitNext(support.rxFrom(streamId).skip(skip));
        }

    }

}