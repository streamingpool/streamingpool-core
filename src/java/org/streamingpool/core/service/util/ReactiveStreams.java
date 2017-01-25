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

package org.streamingpool.core.service.util;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.StreamId;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import io.reactivex.Flowable;

/**
 * Utility methods for working with {@link ReactiveStream}s.
 * 
 * @deprecated with the dependency on {@link Publisher} directly, no need for these methods anymore. Use technology
 *             specific
 */
@Deprecated
public final class ReactiveStreams {

    private ReactiveStreams() {
        /* static methods only */
    }

    /**
     * @deprecated rxjava2 has {@link Flowable#fromPublisher(Publisher)}
     */
    public static <T> Flowable<T> rxFrom(Publisher<T> stream) {
        return Flowable.fromPublisher(stream);
    }

    /**
     * @deprecated {@link Flowable} is a {@link Publisher}
     */
    public static <T> Publisher<T> fromRx(Flowable<T> source) {
        return source;
    }

    /**
     * @deprecated useless
     * @see ReactiveStreams#fromRx(Flowable)
     */
    public static <T> Publisher<T> publisherFrom(Flowable<T> source) {
        return source;
    }

    /**
     * @deprecated {@link Source} has a {@link Source#fromPublisher(Publisher)} method
     */
    public static <T> Source<T, NotUsed> sourceFrom(Publisher<T> stream) {
        return Source.fromPublisher(stream);
    }

    @SuppressWarnings("unused")
    public static <T> Source<T, NotUsed> sourceFrom(StreamId<T> streamId) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

}
