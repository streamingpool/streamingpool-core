/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.resolve;

import static cern.streaming.pool.core.names.resolve.Chains.chain;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import org.junit.Test;

public class NamesTest {

    @Test
    public void nonOverriddenStringReturnsNull() {
        Function<Object, String> chain = Chains.<String> chain().or(Names::fromOverriddenToString).orElseNull();
        String name = chain.apply(new EmptyTestClass());
        assertThat(name).isNull();
    }

    @Test
    public void chainOverriddenToStringWithSimpleClassNameReturnsSimpleClassName() {
        Function<Object, String> chain = Chains.<String> chain().or(Names::fromOverriddenToString)
                .or(Names::fromSimpleClassName).orElseNull();
        String name = chain.apply(new EmptyTestClass());
        assertThat(name).isEqualTo("EmptyTestClass");
    }

    @Test
    public void anyToStringWillNotBeCalledWhenSimpleClassNameBefore() {
        Function<Object, String> chain = Chains.<String> chain().or(Names::fromOverriddenToString)
                .or(Names::fromSimpleClassName).or(Names::fromToString).orElseNull();
        String name = chain.apply(new EmptyTestClass());
        assertThat(name).isEqualTo("EmptyTestClass");
    }

    private static final class EmptyTestClass {
        /* empty on purpose */
    }
}
