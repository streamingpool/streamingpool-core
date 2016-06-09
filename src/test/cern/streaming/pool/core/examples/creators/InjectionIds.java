/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static cern.streaming.pool.core.util.ReactStreams.namedId;

import cern.streaming.pool.core.service.StreamId;

public final class InjectionIds {

    private InjectionIds() {
    }
    
    public static final StreamId<InjectionDomainObject> INJECTION_CONTROL_SYSTEM = namedId("Injection control system 1");
}
