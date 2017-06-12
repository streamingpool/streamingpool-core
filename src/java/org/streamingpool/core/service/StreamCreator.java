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

import org.reactivestreams.Publisher;

/**
 * Interface that represents an entity that is able to create a specific {@link org.reactivestreams.Publisher}. The implementations of
 * this interfaces should know a priori the type and how to create a {@link org.reactivestreams.Publisher}.
 * 
 * @param <T> the type of data that the stream contains
 */
@FunctionalInterface
public interface StreamCreator<T> {

    /**
     * Creates a {@link org.reactivestreams.Publisher}. The provided {@link DiscoveryService} can be used to discover other
     * {@link org.reactivestreams.Publisher}s in order to combine them during the creation process.
     * <p>
     * <strong>NOTE</strong>: it is strongly discouraged the use of multiple threads inside this method (see
     * {@link TypedStreamFactory} documentation).
     * 
     * @param discoveryService {@link DiscoveryService} which can be used by the factory to look up other streams
     *            ('upstream' of the one it will create)
     * @return the newly created {@link org.reactivestreams.Publisher}
     */
    Publisher<T> createWith(DiscoveryService discoveryService);

}
