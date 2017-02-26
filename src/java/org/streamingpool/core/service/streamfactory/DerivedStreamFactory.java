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
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streamingpool.core.domain.ErrorDeflector;
import org.streamingpool.core.domain.Stream;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.DerivedStreamId;

import io.reactivex.Flowable;

public class DerivedStreamFactory implements StreamFactory {

    @Override
    public <T> Stream<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof DerivedStreamId)) {
            return Stream.notCreated();
        }
        @SuppressWarnings("unchecked")
        DerivedStreamId<?, T> derivedStreamId = (DerivedStreamId<?, T>) id;
        return createDerivedStream(derivedStreamId, discoveryService);
    }

    private <S, T> Stream<T> createDerivedStream(DerivedStreamId<S, T> id, DiscoveryService discoveryService) {
        Flowable<S> sourceStream = Flowable.fromPublisher(discoveryService.discover(id.sourceStreamId()));
        Function<S, T> conversion = id.conversion();

        ErrorDeflector ed = ErrorDeflector.create();
        return ed.stream(sourceStream.map(ed.emptyOnError(conversion)).filter(Optional::isPresent).map(Optional::get));
    }

}
