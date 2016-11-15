/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.resolve;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

public class Names {

    public static final Function<Object, String> FROM_NAME_METHOD = Names::fromNameMethod;
    public static final Function<Object, String> FROM_GET_NAME_METHOD = Names::fromGetNameMethod;
    public static final Function<Object, String> FROM_SIMPLE_CLASSNAME = Names::fromSimpleClassName;
    public static final Function<Object, String> FROM_OVERRIDDEN_TOSTRING = Names::fromOverriddenToString;

    private static final String GET_NAME_METHOD_NAME = "getName";
    private static final String NAME_METHOD_NAME = "name";
    private static final String TO_STRING_METHOD_NAME = "toString";

    public static final String fromToString(Object object) {
        return Objects.toString(object);
    }

    public static String fromNameMethod(Object o) {
        return nameFromMethodOfName(o, NAME_METHOD_NAME);
    }

    public static String fromGetNameMethod(Object o) {
        return nameFromMethodOfName(o, GET_NAME_METHOD_NAME);
    }

    public static String fromSimpleClassName(Object o) {
        return o.getClass().getSimpleName();
    }

    public static final String fromOverriddenToString(Object object) {
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
