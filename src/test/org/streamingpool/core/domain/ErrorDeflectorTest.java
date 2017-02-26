/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.domain;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

public class ErrorDeflectorTest {

    @Test(expected = NullPointerException.class)
    public void optionalOfNullThrowsNpe() {
        Optional.of(null);
    }

}
