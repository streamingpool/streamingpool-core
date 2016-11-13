/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.resolve;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

public class NameFunctions {

    private static final String GET_NAME_METHOD_NAME = "getName";
    private static final String NAME_METHOD_NAME = "name";
    private static final String TO_STRING_METHOD_NAME = "toString";

    public static final Function<Object, String> anyToString() {
        return Objects::toString;
    }

    public static final Function<Object, String> overriddenToString() {
        return NameFunctions::nameFromOverriddenToString;
    }

    public static final Function<Object, String> nameMethod() {
        return o -> nameFromMethodOfName(o, NAME_METHOD_NAME);
    }

    public static final Function<Object, String> getNameMethod() {
        return o -> nameFromMethodOfName(o, GET_NAME_METHOD_NAME);
    }

    public static final Function<Object, String> simpleClassName() {
        return o -> o.getClass().getSimpleName();
    }

    private static final String nameFromOverriddenToString(Object object) {
        if (isToStringOverriden(object)) {
            return object.toString();
        }
        return null;
    }

    private static final boolean isToStringOverriden(Object object) {
        try {
            return !object.getClass().getMethod(TO_STRING_METHOD_NAME).getDeclaringClass().equals(Object.class);
        } catch (Exception e) {
            /* returning false */
        }
        return false;
    }

    private static final String nameFromMethodOfName(Object object, String methodName) {
        Method nameMethod;
        try {
            nameMethod = object.getClass().getMethod(methodName);
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
