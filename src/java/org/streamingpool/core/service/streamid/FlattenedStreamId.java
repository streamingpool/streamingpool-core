// @formatter:off
/**
 * This file is part of streaming pool (http://www.streamingpool.org).
 * <p>
 * Copyright (c) 2017-present, CERN. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// @formatter:on

package org.streamingpool.core.service.streamid;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.FlattenedStreamFactory;

import static java.util.Objects.requireNonNull;

/**
 * A stream id, that flattens all of the values emitted by another stream of iterables, null values are excluded from
 * the resulting stream.
 *
 * @param <T> the type of the final stream
 * @author timartin
 * @see FlattenedStreamFactory
 */
public class FlattenedStreamId<T> implements StreamId<T> {
    private static final long serialVersionUID = 1L;

    private final StreamId<? extends Iterable<? extends T>> sourceStreamId;

    public static <T> FlattenedStreamId<T> flatten(StreamId<? extends Iterable<? extends T>> sourceStreamId) {
        return new FlattenedStreamId<>(sourceStreamId);
    }

    public FlattenedStreamId(StreamId<? extends Iterable<? extends T>> sourceStreamId) {
        this.sourceStreamId = requireNonNull(sourceStreamId, "sourceStreamId must not be null");
    }

    public StreamId<? extends Iterable<? extends T>> sourceStreamId() {
        return sourceStreamId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sourceStreamId == null) ? 0 : sourceStreamId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FlattenedStreamId<?> other = (FlattenedStreamId<?>) obj;
        if (sourceStreamId == null) {
            if (other.sourceStreamId != null) {
                return false;
            }
        } else if (!sourceStreamId.equals(other.sourceStreamId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "FlattenedStreamId [sourceStreamId=" + sourceStreamId + "]";
    }

}
