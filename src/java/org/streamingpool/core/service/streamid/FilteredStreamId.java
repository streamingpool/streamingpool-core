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

package org.streamingpool.core.service.streamid;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.function.Predicate;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.FilteredStreamFactory;

/**
 * Filter the items of the stream by applying the specified {@link #predicate()}.
 *
 * @author acalia
 * @see FilteredStreamFactory
 * @param <T> the type of the data items
 */
public class FilteredStreamId<T> implements StreamId<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private final StreamId<T> sourceStreamId;
    private final Predicate<T> predicate;

    public static <T> FilteredStreamId<T> filterBy(StreamId<T> source, Predicate<T> predicate) {
        return new FilteredStreamId<>(source, predicate);
    }

    public FilteredStreamId(StreamId<T> source, Predicate<T> predicate) {
        this.sourceStreamId = requireNonNull(source, "source of the filtering must not be null");
        this.predicate = requireNonNull(predicate, "predicate of the filtering must not be null");
    }

    public StreamId<T> sourceStreamId() {
        return sourceStreamId;
    }

    public Predicate<T> predicate() {
        return predicate;
    }

    @Override
    public String toString() {
        return "FilteredStreamId [sourceStreamId=" + sourceStreamId + ", predicate=" + predicate + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
        result = prime * result + ((sourceStreamId == null) ? 0 : sourceStreamId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FilteredStreamId<?> other = (FilteredStreamId<?>) obj;
        if (predicate == null) {
            if (other.predicate != null)
                return false;
        } else if (!predicate.equals(other.predicate))
            return false;
        if (sourceStreamId == null) {
            if (other.sourceStreamId != null)
                return false;
        } else if (!sourceStreamId.equals(other.sourceStreamId))
            return false;
        return true;
    }

}
