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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.reactivestreams.Publisher;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.CompositionStreamId;

/**
 * EXPERIMENTAL {@link StreamFactory} which provides a flexible way to create {@link org.reactivestreams.Publisher}s based on
 * composition of streams.
 *
 * @author timartin
 */
public final class CompositionStreamFactory implements StreamFactory {
    @Override
    public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        Objects.requireNonNull(discoveryService, "discoveryService");
        if (!(id instanceof CompositionStreamId)) {
            return ErrorStreamPair.empty();
        }
        @SuppressWarnings("unchecked")
        CompositionStreamId<?, T> compositionStreamId = (CompositionStreamId<?, T>) id;
        return ErrorStreamPair.ofData(createStream(compositionStreamId, discoveryService));
    }

    private <X, T> Publisher<T> createStream(CompositionStreamId<X, T> id, DiscoveryService discoveryService) {
        List<Publisher<X>> extractedStreams = extractStreams(id.sourceStreamIds(), discoveryService);
        return id.transformation().apply(extractedStreams);
    }

    private <X> List<Publisher<X>> extractStreams(Collection<StreamId<X>> streamIds,
            DiscoveryService discoveryService) {
        List<Publisher<X>> sourceReactiveStreams = new ArrayList<>();
        for (StreamId<X> streamId : streamIds) {
            sourceReactiveStreams.add(discoveryService.discover(streamId));
        }
        return sourceReactiveStreams;
    }
}
