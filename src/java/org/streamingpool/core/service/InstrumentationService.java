package org.streamingpool.core.service;

import org.streamingpool.core.domain.StreamDependencyTree;

/**
 * Service to get instrumentation/debug information about a pool
 */
public interface InstrumentationService {

    /**
     * Get the {@link StreamDependencyTree} containing all the {@link org.streamingpool.core.service.StreamId} and the
     * corresponding dependencies of the pool
     */
    StreamDependencyTree dependencyTree();
}
