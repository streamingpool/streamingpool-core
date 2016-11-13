/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.resolve;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FunctionChainTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullThrows() {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("new function");
        FunctionChain.chain(null);
    }

}
