/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import cern.streaming.pool.core.names.impl.ImmutableNameRepository;

public final class NameRepositories {

    public static ImmutableNameRepository newFromConstantContainers(List<ConstantsNameContainer> constantsContainers) {
        return new ImmutableNameRepository(NameRepositories.mapNamesFrom(constantsContainers));
    }

    private static final Map<Object, String> mapNamesFrom(List<ConstantsNameContainer> constantContainers) {
        //@formatter:off
        return constantContainers.stream()
                .flatMap(constants -> Stream.of(constants.getClass().getFields()))
                .distinct()
                .filter(NameRepositories::isConstant)
                .collect(toMap(NameRepositories::valueOfField, NameRepositories::nameOfField));
        //@formatter:on
    }

    private static final boolean isConstant(Field field) {
        final int modifiers = field.getModifiers();
        return isStatic(modifiers) && isFinal(modifiers) && isPublic(modifiers);
    }

    private static final Object valueOfField(Field field) {
        try {
            return field.get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(format("Cannot get the value of the field %s", field), e);
        }
    }

    private static final String nameOfField(Field field) {
        return field.getName();
    }

    private NameRepositories() {
        /* Only static methods */
    }

}
