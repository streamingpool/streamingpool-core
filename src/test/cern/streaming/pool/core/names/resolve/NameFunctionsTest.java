/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.resolve;

import static cern.streaming.pool.core.names.resolve.FunctionChain.chain;
import static cern.streaming.pool.core.names.resolve.NameFunctions.anyToString;
import static cern.streaming.pool.core.names.resolve.NameFunctions.overriddenToString;
import static cern.streaming.pool.core.names.resolve.NameFunctions.simpleClassName;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import org.junit.Test;

public class NameFunctionsTest {

    @Test
    public void nonOverriddenStringReturnsNull() {
        Function<Object, String> chain = chain(overriddenToString()).orElseNull();
        String name = chain.apply(new EmptyTestClass());
        assertThat(name).isNull();
    }

    @Test
    public void chainOverriddenToStringWithSimpleClassNameReturnsSimpleClassName() {
        Function<Object, String> chain = chain(overriddenToString()).then(simpleClassName()).orElseNull();
        String name = chain.apply(new EmptyTestClass());
        assertThat(name).isEqualTo("EmptyTestClass");
    }

    @Test
    public void anyToStringWillNotBeCalledWhenSimpleClassNameBefore() {
        Function<Object, String> chain = chain(overriddenToString()).then(simpleClassName()).then(anyToString())
                .orElseNull();
        String name = chain.apply(new EmptyTestClass());
        assertThat(name).isEqualTo("EmptyTestClass");
    }

    private static final class EmptyTestClass {
        /* empty on purpose */
    }
}
