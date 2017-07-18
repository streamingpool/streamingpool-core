/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core;


import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.testing.TransportableEntityFulfilled;

public class TransportableEntitiesFulfillContractTest extends TransportableEntityFulfilled {

    public TransportableEntitiesFulfillContractTest() {
        super(PackageReference.packageName(), StreamId.class);
    }

}
