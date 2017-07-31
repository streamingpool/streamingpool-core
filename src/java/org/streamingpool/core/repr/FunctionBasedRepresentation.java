/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.repr;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;

public final class FunctionBasedRepresentation<T> implements Representation<T> {

    private final Function<Object, T> function;

    private FunctionBasedRepresentation(Function<Object, T> function) {
        this.function = requireNonNull(function, "function must not be null");
    }

    public static <T> FunctionBasedRepresentation<T> of(Function<Object, T> function) {
        return new FunctionBasedRepresentation<>(function);
    }

    @Override
    public T apply(Object object) {
        return function.apply(object);
    }

}
