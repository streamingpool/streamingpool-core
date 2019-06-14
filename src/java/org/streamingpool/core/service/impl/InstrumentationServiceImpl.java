package org.streamingpool.core.service.impl;

import org.streamingpool.core.domain.StreamDependencyTree;
import org.streamingpool.core.service.InstrumentationService;

public class InstrumentationServiceImpl implements InstrumentationService {
    private final PoolContent content;

    public InstrumentationServiceImpl(PoolContent content) {
        this.content = content;
    }

    @Override
    public StreamDependencyTree dependencyTree() {
        return content.dependencyTree();
    }
}
