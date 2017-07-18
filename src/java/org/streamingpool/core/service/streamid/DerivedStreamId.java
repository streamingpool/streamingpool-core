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
import java.util.function.Function;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.DerivedStreamFactory;

/**
 * A stream id, that applies the provided function to transform the elements of the data stream. It is much like a map
 * operator, but the operation is specified before the actual stream is created or discovered.
 *
 * @see DerivedStreamFactory
 * @author kfuchsbe
 * @param <S> the type of the source stream
 * @param <T> the type of the final stream
 */
public class DerivedStreamId<S, T> implements StreamId<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private final StreamId<S> sourceStreamId;
    private final Function<S, T> conversion;

    public static <S, T> DerivedStreamId<S, T> derive(StreamId<S> sourceStreamId, Function<S, T> conversion) {
        return new DerivedStreamId<>(sourceStreamId, conversion);
    }

    public DerivedStreamId(StreamId<S> sourceStreamId, Function<S, T> conversion) {
        this.sourceStreamId = requireNonNull(sourceStreamId, "sourceStreamId must not be null");
        this.conversion = requireNonNull(conversion, "conversion must not be null");
    }

    public StreamId<S> sourceStreamId() {
        return sourceStreamId;
    }

    public Function<S, T> conversion() {
        return conversion;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conversion == null) ? 0 : conversion.hashCode());
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
        DerivedStreamId<?, ?> other = (DerivedStreamId<?, ?>) obj;
        if (conversion == null) {
            if (other.conversion != null) {
                return false;
            }
        } else if (!conversion.equals(other.conversion)) {
            return false;
        }
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
        return "SimpleDerivedStreamId [sourceStreamId=" + sourceStreamId + ", conversion=" + conversion + "]";
    }

}
