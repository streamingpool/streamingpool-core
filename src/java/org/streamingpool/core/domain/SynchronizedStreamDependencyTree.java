package org.streamingpool.core.domain;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.streamingpool.core.service.StreamId;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Synchronized implementation of a {@link StreamDependencyTree}
 */
public class SynchronizedStreamDependencyTree implements StreamDependencyTree {

    private final Multimap<StreamId<?>, StreamId<?>> dependencies = HashMultimap.create();

    /**
     * Add a dependency to the tree from source to parent
     */
    public void addDependency(StreamId<?> source, StreamId<?> parent) {
        synchronized (dependencies) {
            dependencies.put(source, parent);
        }
    }

    @Override
    public Set<StreamId<?>> getAncestorsFrom(StreamId<?> source) {
        synchronized (dependencies) {
            Collection<StreamId<?>> ancestors = dependencies.get(source);
            if (ancestors.isEmpty()) {
                return ImmutableSet.of(source);
            }
            Set<StreamId<?>> ancestorsId = ancestors.stream()
                    .flatMap(ancestor -> getAncestorsFrom(ancestor).stream())
                    .collect(Collectors.toSet());
            return ImmutableSet.<StreamId<?>>builder().add(source).addAll(ancestorsId).build();
        }
    }

}
