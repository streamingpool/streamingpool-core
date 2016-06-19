/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

/**
 * A stream id, which depends on one single other one (called source).
 * 
 * @author kfuchsbe
 * @param <S> the type of the source stream
 * @param <T> the type of the final stream
 */
public class DerivedStreamId<S, T> implements StreamId<T> {

    private final StreamId<S> sourceStreamId;
    private final Function<S, T> conversion;

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
        DerivedStreamId other = (DerivedStreamId) obj;
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
