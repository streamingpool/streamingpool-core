/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import cern.streaming.pool.core.names.NameRepository;

/**
 * Immutable repository for object's name backed by a {@link Map}. Resolves the name of an object using the {@link Map}
 * specified in the constructor, or provides a suitable substitute in case the object is not found in the {@link Map}.
 * 
 * @see NameRepository
 * @see #nameFor(Object)
 */
public class ImmutableNameRepository implements NameRepository {

    private final Map<Object, String> expressionNames;

    public ImmutableNameRepository(Map<Object, String> expressionNames) {
        this.expressionNames = ImmutableMap.copyOf(expressionNames);
    }

    @Override
    public String nameFor(Object object) {
        String registeredName = expressionNames.get(object);
        if (registeredName != null) {
            return registeredName;
        }
        String nameFromNameMethod = nameFromNameMethod(object);
        if (nameFromNameMethod != null) {
            return nameFromNameMethod;
        }
        if (isToStringOverriden(object)) {
            return object.toString();
        }
        return object.getClass().getSimpleName();
    }

    private static final boolean isToStringOverriden(Object object) {
        try {
            return !object.getClass().getMethod("toString").getDeclaringClass().equals(Object.class);
        } catch (Exception e) {
            /* returning false */
        }
        return false;
    }

    private static final String nameFromNameMethod(Object object) {
        Method nameMethod;
        try {
            nameMethod = object.getClass().getMethod("name");
        } catch (NoSuchMethodException e) {
            /* Null On purpose! */
            return null;
        }
        if (!String.class.isAssignableFrom(nameMethod.getReturnType())) {
            return null;
        }
        try {
            return (String) nameMethod.invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            return null;
        }
    }

}
