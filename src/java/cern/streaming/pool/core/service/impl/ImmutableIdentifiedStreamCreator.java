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

package cern.streaming.pool.core.service.impl;

import static java.util.Objects.requireNonNull;

import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamId;

/**
 * Immutable implementation of a {@link IdentifiedStreamCreator}.
 * 
 * @author acalia
 * @param <T> the type of the data that the stream created using the {@link StreamCreator} will have
 */
public class ImmutableIdentifiedStreamCreator<T> implements IdentifiedStreamCreator<T> {

    private final StreamId<T> id;
    private final StreamCreator<T> creator;

    protected ImmutableIdentifiedStreamCreator(StreamId<T> id, StreamCreator<T> creator) {
        this.id = requireNonNull(id, "id must not be null.");
        this.creator = requireNonNull(creator, "creator must not be null.");
    }

    public static <T> IdentifiedStreamCreator<T> of(StreamId<T> id, StreamCreator<T> creator) {
        return new ImmutableIdentifiedStreamCreator<>(id, creator);
    }

    @Override
    public StreamId<T> getId() {
        return id;
    }

    @Override
    public StreamCreator<T> getCreator() {
        return creator;
    }
}
