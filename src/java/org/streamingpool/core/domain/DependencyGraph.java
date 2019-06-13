package org.streamingpool.core.domain;

import org.streamingpool.core.service.StreamId;

import java.util.Set;

public interface DependencyGraph {
    Set<StreamId<?>> getSubgraphStartingFrom(StreamId<?> source);
}
