/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.testing.NamedStreamId;

public final class InjectionIds {

    private InjectionIds() {
    }

    public static final StreamId<InjectionDomainObject> INJECTION_CONTROL_SYSTEM = NamedStreamId
            .ofName("Injection control system 1");
}
