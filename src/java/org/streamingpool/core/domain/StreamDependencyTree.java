package org.streamingpool.core.domain;

import org.streamingpool.core.service.StreamId;

import java.util.Set;

/**
 * Data structure that holds the dependencies between {@link StreamId}s
 */
public interface StreamDependencyTree {

    /**
     * Get all the ancestors of the given {@link StreamId}. NOTE: this INCLUDES the source {@link StreamId}!
     */
    Set<StreamId<?>> getAncestorsFrom(StreamId<?> source);

}
