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

package cern.streaming.pool.core.service;

/**
 * Interface for defining custom stream identifiers. Each stream is identified with one {@link StreamId} so a custom,
 * domain-specific, implementation is expected to be provided.
 * </p>
 * For example, a stream of random numbers may have a special implementation of {@link StreamId} that includes the seed
 * to be used in the number generation. Also, another implementation may include information about the range in which
 * the numbers need to be scaled.
 * 
 * @see ReactiveStream
 * @param <T> the type of the data that the a stream created from this id contains
 */
public interface StreamId<T> {
    /* Marker interface */
}
