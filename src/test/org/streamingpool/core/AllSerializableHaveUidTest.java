/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core;

import org.streamingpool.core.testing.SerializableHasUid;

public class AllSerializableHaveUidTest extends SerializableHasUid {

    public AllSerializableHaveUidTest() {
        super(PackageReference.packageName());
    }

}
