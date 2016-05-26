/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.proto.akka;

import akka.stream.Attributes.Attribute;
import stream.DiscoveryService;

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