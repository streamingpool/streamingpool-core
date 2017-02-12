// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package org.streamingpool.core.incubation.akka;

import org.streamingpool.core.service.DiscoveryService;

import akka.stream.Attributes.Attribute;

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