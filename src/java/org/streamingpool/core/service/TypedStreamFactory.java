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

package org.streamingpool.core.service;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.streamingpool.core.domain.ErrorStreamPair;

/**
 * Typed version of a {@link StreamFactory}. A {@link StreamFactory} can create any type of streams, but sometimes there
 * are scenarios in which the type of the {@link StreamId} and the data type of the {@link Publisher} to be created are
 * known. In those cases, {@link TypedStreamFactory} can simplify the development of a {@link StreamFactory}. The
 * {@link StreamFactory#create(StreamId, DiscoveryService)} method is provided with a default implementation that suites
 * most use cases.
 * 
 * @param <X> The type of objects published by the {@link Publisher} and therefore used by the {@link StreamId}
 *            implementation
 * @param <T> The class of {@link StreamId} to be used in the constrution of {@link Publisher}s.
 * @author maosinsk
 * @author timartin
 */
public interface TypedStreamFactory<X, T extends StreamId<X>> extends StreamFactory {

    /**
     * Default implementation of the {@link StreamFactory#create(StreamId, DiscoveryService)} method. Is uses
     * {@link #createReactiveStream(StreamId, DiscoveryService)} and {@link #streamIdClass()} to provide a more
     * developer friendly way of creating {@link org.reactivestreams.Publisher}s.
     */
    @SuppressWarnings("unchecked")
    @Override
    default <Y> ErrorStreamPair<Y> create(StreamId<Y> id, DiscoveryService discoveryService) {
        if (!streamIdClass().isAssignableFrom(id.getClass())) {
            return ErrorStreamPair.empty();
        }

        return ErrorStreamPair.ofData((Publisher<Y>) createReactiveStream((T) id, discoveryService));
    }

    /**
     * Actually create the {@link org.reactivestreams.Publisher} from the given id. It is much like
     * {@link StreamFactory#create(StreamId, DiscoveryService)} but with typed {@link StreamId}.
     */
    Publisher<X> createReactiveStream(T id, DiscoveryService discoveryService);

    /**
     * Returns the class that this {@link TypedStreamFactory} is able to create.
     */
    Class<T> streamIdClass();
}
