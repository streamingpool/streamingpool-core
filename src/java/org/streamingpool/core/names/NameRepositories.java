// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
*
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/
// @formatter:on

package org.streamingpool.core.names;

import static java.lang.String.format;
import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.streamingpool.core.names.impl.ImmutableNameRepository;

/**
 * @deprecated Should not be necessary anymore, because of classpathscanning from tensorics
 * @author kfuchsbe
 */
@Deprecated
public final class NameRepositories {

    private NameRepositories() {
        /* Only static methods */
    }

    public static ImmutableNameRepository newFromConstantContainers(List<ConstantsContainer> constantsContainers) {
        return new ImmutableNameRepository(NameRepositories.mapNamesFrom(constantsContainers));
    }

    private static Map<Object, String> mapNamesFrom(List<ConstantsContainer> constantContainers) {
        //@formatter:off
        return constantContainers.stream()
                .flatMap(constants -> Stream.of(constants.getClass().getFields()))
                .distinct()
                .filter(NameRepositories::isPublicConstant)
                .collect(toMap(NameRepositories::valueOfField, NameRepositories::nameOfField));
        //@formatter:on
    }

    private static boolean isPublicConstant(Field field) {
        final int modifiers = field.getModifiers();
        return isStatic(modifiers) && isFinal(modifiers) && isPublic(modifiers);
    }

    private static Object valueOfField(Field field) {
        try {
            return field.get(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(format("Cannot get the value of the field %s", field), e);
        }
    }

    private static String nameOfField(Field field) {
        return field.getName();
    }

}
