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

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.streamingpool.core.domain.ErrorDeflector;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.ZippedStreamId;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import io.reactivex.functions.Function;

public class ZippedStreamFactory implements StreamFactory {

    @Override
    public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        Objects.requireNonNull(discoveryService, "discoveryService");
        if (!(id instanceof ZippedStreamId)) {
            return ErrorStreamPair.empty();
        }
        @SuppressWarnings("unchecked")
        ZippedStreamId<?, T> zippedId = (ZippedStreamId<?, T>) id;

        return createStream(zippedId, discoveryService);
    }

    public <S, T> ErrorStreamPair<T> createStream( ZippedStreamId<S,  T> id, DiscoveryService discoveryService) {

        Iterable<Publisher<S>> publishers = StreamSupport.stream(id.sourceStreamIds().spliterator(), false)
                .map(discoveryService::discover)
                .collect(Collectors.toList());
        final Function< Object[], Optional<T>> function = id.function();

        ErrorDeflector ed = ErrorDeflector.create();
        return ed.streamNonEmpty(Flowable.zip(publishers, function));

    }


}
