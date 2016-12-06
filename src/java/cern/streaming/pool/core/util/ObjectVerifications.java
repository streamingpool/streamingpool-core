/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;

public class ObjectVerifications {

    public static final Object invokeUnchecked(Object object, Method getter) {
        try {
            return getter.invoke(object);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("Invocation of getter '" + getter.getName() + "' resulted in an exception.", e);
        }
    }

    public static List<Method> declaredGetters(Object object) {
        return Arrays.stream(object.getClass().getDeclaredMethods()).filter(m -> (m.getParameterCount() == 0))
                .filter(m -> !m.getReturnType().equals(Void.TYPE)).filter(m -> Modifier.isPublic(m.getModifiers()))
                .filter(m -> !Modifier.isStatic(m.getModifiers())).collect(toList());
    }

    public static final void verifyAllDeclaredGettersReturnSomething(Object object) {
        List<Method> getters = declaredGetters(object);

        for (Method getter : getters) {
            Object value = invokeUnchecked(object, getter);
            requireNonNull(value, "value from getter '" + getter.getName() + "' must not be null!");
        }
    }

    public static Object valueOfField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(format("Exception getting the field %s of object %s", field, target), e);
        }
    }

    public static boolean isPublicField(Field field) {
        return Modifier.isPublic(field.getModifiers());
    }

    public static boolean isFinalField(Field field) {
        return Modifier.isFinal(field.getModifiers());
    }

}
