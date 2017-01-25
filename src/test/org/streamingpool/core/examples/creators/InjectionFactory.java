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

package org.streamingpool.core.examples.creators;

import static java.util.Optional.of;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;

import io.reactivex.Flowable;

public class InjectionFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Publisher<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!id.equals(InjectionIds.INJECTION_CONTROL_SYSTEM)) {
            return null;
        }

        return of((Publisher<T>) Flowable.interval(1, SECONDS).map(num -> "Injection number " + num)
                .map(InjectionDomainObject::new));
    }

}
