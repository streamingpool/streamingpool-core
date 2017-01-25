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

package org.streamingpool.core.support;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.service.CreatorProvidingService;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.ProvidingService;
import org.streamingpool.core.service.StreamCreator;
import org.streamingpool.core.service.StreamId;

/**
 * This class provides support for the discovery and for providing streams.
 * </p>
 * Dependency injection:
 * <ul>
 * <li>{@link DiscoveryService}</li>
 * <li>{@link ProvidingService}</li>
 * <li>{@link CreatorProvidingService}</li>
 * </ul>
 * 
 * @author kfuchsbe
 */
public class AbstractStreamSupport implements StreamSupport {

    @Autowired
    private DiscoveryService discoveryService;
    @Autowired
    private ProvidingService providingService;
    @Autowired
    private CreatorProvidingService lazyProvidingService;

    @Override
    public <T> Publisher<T> discover(StreamId<T> id) {
        return discoveryService.discover(id);
    }

    @Override
    public <T> StreamSupport.OngoingProviding<T> provide(Publisher<T> reactStream) {
        return new StreamSupport.OngoingProviding<>(providingService, reactStream);
    }

    @Override
    public <T> StreamSupport.OngoingLazyProviding<T> provide(StreamCreator<T> reactStream) {
        return new StreamSupport.OngoingLazyProviding<>(lazyProvidingService, reactStream);
    }

    @Override
    public ProvidingService providingService() {
        return providingService;
    }

}