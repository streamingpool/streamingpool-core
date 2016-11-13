/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.resolve;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

public final class FunctionChain<T, R> implements Function<T, R> {

    private final List<Function<T, R>> functions;

    FunctionChain(Builder<T, R> builder) {
        this.functions = builder.mappers.build();
    }

    public static <T, R> Builder<T, R> chain(Function<T, R> firstMapper) {
        return new Builder<T, R>().then(firstMapper);
    }

    public static final class Builder<T, R> {

        private final ImmutableList.Builder<Function<T, R>> mappers = ImmutableList.builder();

        Builder() {
            /* builder shall be only constructed with the by method */
        }

        public Builder<T, R> then(Function<T, R> mapper) {
            Objects.requireNonNull(mapper, "new mapper must not be null");
            mappers.add(mapper);
            return this;
        }

        public Function<T, R> orElseNull() {
            return new FunctionChain<>(this);
        }

    }

    @Override
    public R apply(T object) {
        Objects.requireNonNull(object, "object to resolve the name for must not be null!");
        for (Function<T, R> function : functions) {
            R name = function.apply(object);
            if (name != null) {
                return name;
            }
        }
        return null;
    }
}
