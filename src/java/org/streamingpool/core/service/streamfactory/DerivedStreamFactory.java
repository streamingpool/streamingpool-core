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

package org.streamingpool.core.service.streamfactory;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.DerivedStreamId;

import io.reactivex.Flowable;

public class DerivedStreamFactory implements StreamFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DerivedStreamFactory.class);

    @Override
    public <T> Optional<Publisher<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof DerivedStreamId)) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        DerivedStreamId<?, T> derivedStreamId = (DerivedStreamId<?, T>) id;
        return Optional.of(createDerivedStream(derivedStreamId, discoveryService));
    }

    private <S, T> Flowable<T> createDerivedStream(DerivedStreamId<S, T> id, DiscoveryService discoveryService) {
        Flowable<S> sourceStream = Flowable.fromPublisher(discoveryService.discover(id.sourceStreamId()));
        return sourceStream.map(val -> {
            try {
                return Optional.<T> of(id.conversion().apply(val));
            } catch (Exception e) {
                LOGGER.error("Error while converting '" + val + "' by derived stream id '" + id + "'.", e);
                return Optional.<T> empty();
            }
        }).filter(Optional::isPresent).map(Optional::get);
    }
}
