/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.resolve;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

public final class Chains {

    private Chains() {

    }

    public static <R> ChainBuilder<Object, R, Function<Object, R>> chain() {
        return newFunctionBuilder();
    }

    public static <R> ChainBuilder<Object, R, Function<Object, R>> chain(
            BiFunction<Object, Function<Object, R>, R> mapperWithCallback) {
        ChainBuilder<Object, R, Function<Object, R>> builder = newFunctionBuilder();
        return builder.or(mapperWithCallback);
    }

    private static <R> ChainBuilder<Object, R, Function<Object, R>> newFunctionBuilder() {
        return new ChainBuilder<>(b -> new Chain<>(new BranchChain<>(b)));
    }

    private static <T, R> ChainBuilder<T, R, BiFunction<T, Function<? super T, R>, R>> newBiFunctionBuilder() {
        return new ChainBuilder<>(BranchChain<T, R>::new);
    }

    private static abstract class AbstractBuilder<T, R, B extends AbstractBuilder<T, R, B>> {

        final ImmutableList.Builder<ConditionedMapper<T, ?, R>> mappers = ImmutableList.builder();
        Predicate<R> returnWhen = v -> v != null;
        boolean throwIfAllReturnNull;
        R defaultValue;

        @SuppressWarnings("unchecked")
        B castedThis = (B) this;

        public B or(Function<? super T, R> mapper) {
            requireNonNull(mapper, "new mapper must not be null");
            or((o, c) -> mapper.apply(o));
            return castedThis;
        }

        public B or(BiFunction<? super T, ? extends Function<? super T, R>, R> mapperWithCallback) {
            requireNonNull(mapperWithCallback, "new mapper must not be null");
            mappers.add(new ConditionedMapper<>(o -> true, Function.identity(), mapperWithCallback));
            return castedThis;
        }

        public B breakOnFirst(Predicate<R> newReturnWhen) {
            this.returnWhen = requireNonNull(newReturnWhen, "predicate to break must not be null");
            return castedThis;
        }

        public BiFunctionBranchBuilder<T, T, R, B> branchIf(Predicate<T> condition, Function<T, R> mapper) {
            return newBranchBuilder(condition).or(mapper);
        }

        public BiFunctionBranchBuilder<T, T, R, B> branchIf(Predicate<T> condition,
                BiFunction<T, ? extends Function<Object, R>, R> mapper) {
            return newBranchBuilder(condition).or(mapper);
        }

        private BiFunctionBranchBuilder<T, T, R, B> newBranchBuilder(Predicate<T> condition) {
            return new BiFunctionBranchBuilder<>(condition, Function.identity(), newBiFunctionBuilder(), castedThis);
        }

        public <T1 extends T> BiFunctionBranchBuilder<T, T1, R, B> branchCase(Class<T1> caseClass,
                Function<? super T1, R> mapper) {
            return new BiFunctionBranchBuilder<>(caseClass::isInstance, caseClass::cast, newBiFunctionBuilder(),
                    castedThis).or(mapper);
        }

        public <T1 extends T> BiFunctionBranchBuilder<T, T1, R, B> branchCase(Class<T1> condition,
                BiFunction<? super T1, ? extends Function<Object, R>, R> mapper) {
            return new BiFunctionBranchBuilder<>(condition::isInstance, condition::cast, newBiFunctionBuilder(),
                    castedThis).or(mapper);
        }

        public <T1 extends T> B when(Predicate<T> condition, Function<T, T1> conv,
                BiFunction<T1, Function<? super T1, R>, R> mapperWithCallback) {
            requireNonNull(condition, "condition must not be null");
            requireNonNull(mapperWithCallback, "mapper must not be null");
            mappers.add(new ConditionedMapper<>(condition, conv, mapperWithCallback));
            return castedThis;
        }

        public <T1 extends T> B when(Predicate<T> condition, Function<T, T1> conv, Function<T1, R> mapper) {
            requireNonNull(mapper, "mapper must not be null");
            when(condition, conv, (o, c) -> mapper.apply(o));
            return castedThis;
        }

    }

    public abstract static class AbstractBranchBuilder<PT, T extends PT, R, B extends AbstractBuilder<T, R, B>, PB extends AbstractBuilder<PT, R, PB>, BB extends AbstractBranchBuilder<PT, T, R, B, PB, BB>> {

        final B delegate;
        final PB parentBuilder;
        final Predicate<PT> condition;
        final Function<PT, T> conversion;

        @SuppressWarnings("unchecked")
        private BB castedThis = (BB) this;

        AbstractBranchBuilder(Predicate<PT> condition, Function<PT, T> conversion, B delegate, PB parentBuilder) {
            this.condition = requireNonNull(condition, "condition must not be null");
            this.conversion = requireNonNull(conversion, "conversion must not be null");
            this.delegate = requireNonNull(delegate, "delegate must not be null");
            this.parentBuilder = requireNonNull(parentBuilder, "parentBuilder must not be null");
        }

        public BB or(Function<? super T, R> mapper) {
            delegate.or(mapper);
            return castedThis;
        }

        public BB or(BiFunction<? super T, ? extends Function<Object, R>, R> mapperWithCallback) {
            delegate.or(mapperWithCallback);
            return castedThis;
        }

        public BB breakOnFirst(Predicate<R> newReturnWhen) {
            delegate.breakOnFirst(newReturnWhen);
            return castedThis;
        }

        public <T1 extends T> BB when(Predicate<T> newCondition, Function<T, T1> conv,
                BiFunction<T1, Function<? super T1, R>, R> mapperWithCallback) {
            delegate.when(newCondition, conv, mapperWithCallback);
            return castedThis;
        }

        public <T1 extends T> BB when(Predicate<T> newCondition, Function<T, T1> conv, Function<T1, R> mapper) {
            delegate.when(newCondition, conv, mapper);
            return castedThis;
        }

    }

    public static class FunctionBranchBuilder<PT, T extends PT, R, PB extends AbstractBuilder<PT, R, PB>> extends
            AbstractBranchBuilder<PT, T, R, ChainBuilder<T, R, Function<T, R>>, PB, FunctionBranchBuilder<PT, T, R, PB>> {

        FunctionBranchBuilder(Predicate<PT> condition, Function<PT, T> conversion,
                ChainBuilder<T, R, Function<T, R>> delegate, PB parentBuilder) {
            super(condition, conversion, delegate, parentBuilder);
        }

        public PB orElseThrow() {
            return parentBuilder.when(condition, conversion, delegate.orElseThrow());
        }

        public PB orElseNull() {
            return parentBuilder.when(condition, conversion, delegate.orElseNull());
        }

        public PB orElse(R newDefaultValue) {
            return parentBuilder.when(condition, conversion, delegate.orElse(newDefaultValue));
        }

    }

    public static class BiFunctionBranchBuilder<PT, T extends PT, R, PB extends AbstractBuilder<PT, R, PB>> extends
            AbstractBranchBuilder<PT, T, R, ChainBuilder<T, R, BiFunction<T, Function<? super T, R>, R>>, PB, BiFunctionBranchBuilder<PT, T, R, PB>> {

        BiFunctionBranchBuilder(Predicate<PT> condition, Function<PT, T> conversion,
                ChainBuilder<T, R, BiFunction<T, Function<? super T, R>, R>> delegate, PB parentBuilder) {
            super(condition, conversion, delegate, parentBuilder);
        }

        public PB orElseThrow() {
            return parentBuilder.when(condition, conversion, delegate.orElseThrow());
        }

        public PB orElseNull() {
            return parentBuilder.when(condition, conversion, delegate.orElseNull());
        }

        public PB orElse(R newDefaultValue) {
            return parentBuilder.when(condition, conversion, delegate.orElse(newDefaultValue));
        }

    }

    public static class ChainBuilder<T, R, F> extends AbstractBuilder<T, R, ChainBuilder<T, R, F>> {

        private final Function<ChainBuilder<T, R, F>, ? extends F> constructor;

        ChainBuilder(Function<ChainBuilder<T, R, F>, ? extends F> constructor) {
            this.constructor = constructor;
        }

        public F orElseThrow() {
            this.defaultValue = null;
            this.throwIfAllReturnNull = true;
            return constructor.apply(this);
        }

        public F orElseNull() {
            this.defaultValue = null;
            this.throwIfAllReturnNull = false;
            return constructor.apply(this);
        }

        public F orElse(R newDefaultValue) {
            this.defaultValue = newDefaultValue;
            this.throwIfAllReturnNull = false;
            return constructor.apply(this);
        }

    }

    private static final class BranchChain<T, R> implements BiFunction<T, Function<? super T, R>, R> {

        private final List<ConditionedMapper<T, ?, R>> conditionedMappers;
        private final R defaultValue;
        private final boolean throwIfAllReturnNull;
        private final Predicate<R> returnWhen;

        BranchChain(ChainBuilder<T, R, ?> builder) {
            this.conditionedMappers = builder.mappers.build();
            this.returnWhen = builder.returnWhen;
            this.defaultValue = builder.defaultValue;
            this.throwIfAllReturnNull = builder.throwIfAllReturnNull;
        }

        @Override
        public R apply(T input, Function<? super T, R> callback) {
            requireNonNull(input, "input value to chained functions must not be null.");
            for (ConditionedMapper<T, ?, R> conditionedMapper : conditionedMappers) {
                if (conditionedMapper.condition().test(input)) {
                    /*
                     * XXX Simply passing in the callback is of course dangerous. Proof of principle for the moment ...
                     * We should keep track of the ongoing calls and detect loops!
                     */
                    R returnValue = conditionedMapper.apply(input, callback);
                    if (returnWhen.test(returnValue)) {
                        return returnValue;
                    }
                }
            }
            if (throwIfAllReturnNull) {
                throw new IllegalArgumentException(
                        "None of the chained functions returned a non-null value for input value '" + input + "'.");
            }
            return defaultValue;
        }

    }

    private static final class Chain<R> implements Function<Object, R> {

        private final BiFunction<Object, Function<? super Object, R>, R> delegate;

        public Chain(BiFunction<Object, Function<? super Object, R>, R> delegate) {
            this.delegate = requireNonNull(delegate, "delegate must not be null");
        }

        @Override
        public R apply(Object input) {
            return delegate.apply(input, this);
        }

    }

    private final static class ConditionedMapper<T, T1 extends T, R>
            implements BiFunction<T, Function<? super T1, R>, R> {

        private final Predicate<T> condition;
        private final BiFunction<? super T1, ? extends Function<? super T1, R>, R> mapper;
        private final Function<T, T1> conversion;

        public ConditionedMapper(Predicate<T> condition, Function<T, T1> conversion,
                BiFunction<? super T1, ? extends Function<? super T1, R>, R> mapper) {
            this.condition = requireNonNull(condition, "condition must not be null");
            this.conversion = requireNonNull(conversion, "conversion must not be null");
            this.mapper = requireNonNull(mapper, "mapper must not be null");
        }

        public ConditionedMapper(Predicate<T> condition, Function<T, T1> conversion, Function<T1, R> mapper) {
            this(condition, conversion, (o, c) -> requireNonNull(mapper, "mapper must not be null").apply(o));
        }

        public Predicate<T> condition() {
            return condition;
        }

        @Override
        public R apply(T input, Function<? super T1, R> callback) {
            T1 converted = conversion.apply(input);
            return mapper().apply(converted, callback);
        }

        private BiFunction<? super T1, Function<? super T1, R>, R> mapper() {
            return (BiFunction<? super T1, Function<? super T1, R>, R>) mapper;
        }

    }

}
