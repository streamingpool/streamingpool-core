/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.impl;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import cern.streaming.pool.core.names.NameRepository;

/**
 * Immutable repository for object's name backed by a {@link Map}. Resolves the name of an object using the {@link Map}
 * specified in the constructor. In case no name can be found within the map, then {@code null is returned}.
 * 
 * @see #nameFor(Object)
 */
public class ImmutableNameRepository implements NameRepository {

    private final Map<Object, String> objectNames;

    /**
     * Creates a new name repository backed by the given map.
     * 
     * @param objectNames the mapping from objects to names
     */
    public ImmutableNameRepository(Map<Object, String> objectNames) {
        requireNonNull(objectNames, "objectNames must not be null");
        this.objectNames = ImmutableMap.copyOf(objectNames);
    }

    @Override
    public String nameFor(Object object) {
        return objectNames.get(object);
    }

    public Map<Object, String> content() {
        return this.objectNames;
    }

}
