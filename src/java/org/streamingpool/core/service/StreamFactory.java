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
 * This interface represents a factory for {@link Publisher}s. An implementation of this interface is able to
 * create a stream given an implementation of {@link StreamId} and a {@link DiscoveryService}. During the creation of a
 * stream it is allowed to discover other streams. This allow the possibility to create streams that merge other streams
 * while performing transformations.
 * <p>
 * <strong>NOTE</strong>: it is not allowed to discover other streams using the provided {@link DiscoveryService} in
 * multiple threads. In other words, do not use new threads inside the stream creation. The provided
 * {@link DiscoveryService} checks that subsequent discoveries are performed on the same thread, otherwise an exception
 * is risen. It is not possible to enforce the single thread execution in the
 * {@link #create(StreamId, DiscoveryService)} method, but using the {@link DiscoveryService} from different threads may
 * lead to unpredictable behavior and can cause deadlocks.
 * 
 * @author acalia
 */
public interface StreamFactory {

    /***
     * Given an implementation of {@link StreamId} and a {@link DiscoveryService} this method creates a
     * {@link Publisher}. The provided {@link DiscoveryService} can be used to discover other streams that are
     * needed in the creation process (stream combination, transformation, etc.)
     * <p>
     * <strong>NOTE</strong>: it is strongly discouraged the use of multiple threads inside this method (see
     * {@link TypedStreamFactory} documentation).
     * 
     * @param id the id of the stream to create
     * @param discoveryService {@link DiscoveryService} which can be used by the factory to look up other streams
     *            ('upstream' of the one it will create)
     * @return the newly created stream or {@code null} if this factory cannot create the stream of the given id
     */
    <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService);

}
