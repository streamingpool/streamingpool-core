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
 * Interface that is able to provide {@link Publisher}. The provided stream can be then discovered using the
 * {@link DiscoveryService}. There are no restrictions on how the implementations actually provide the streams but this
 * process must be transparent to the user.
 * 
 * @see DiscoveryService
 */
@FunctionalInterface
public interface ProvidingService {

    /**
     * Provides the stream with the specified id. From the moment the stream is provided, it can be accessed from
     * {@link DiscoveryService} using the same {@link StreamId}.
     * 
     * @param id the {@link StreamId} that identifies the specified stream
     * @param stream the {@link Publisher} to be provided
     */
    <T> void provide(StreamId<T> id, Publisher<T> stream);

}
