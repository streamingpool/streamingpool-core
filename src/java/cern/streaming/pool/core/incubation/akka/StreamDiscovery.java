/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.incubation.akka;

import akka.stream.Attributes.Attribute;
import cern.streaming.pool.core.service.DiscoveryService;

public class StreamDiscovery implements Attribute {

    private final DiscoveryService service;

    public StreamDiscovery(DiscoveryService service) {
        super();
        this.service = service;
    }

    public DiscoveryService service() {
        return this.service;
    }

}