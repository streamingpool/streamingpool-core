package org.streamingpool.core.domain;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.streamingpool.core.service.StreamId;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyGraphImpl implements DependencyGraph {

    private final Multimap<StreamId<?>, StreamId<?>> dependencies = HashMultimap.create();

    public void addDependency(StreamId<?> target, StreamId<?> parent) {
        synchronized (dependencies) {
            dependencies.put(target, parent);
        }
    }

    @Override
    public Set<StreamId<?>> getSubgraphStartingFrom(StreamId<?> source) {
        synchronized (dependencies) {
            Collection<StreamId<?>> ancestors = dependencies.get(source);
            if (ancestors.isEmpty()) {
                return ImmutableSet.of(source);
            }
            Set<StreamId<?>> ancestorsId = ancestors.stream()
                    .flatMap(ancestor -> getSubgraphStartingFrom(ancestor).stream())
                    .collect(Collectors.toSet());
            return ImmutableSet.<StreamId<?>>builder().add(source).addAll(ancestorsId).build();
        }
    }

}
