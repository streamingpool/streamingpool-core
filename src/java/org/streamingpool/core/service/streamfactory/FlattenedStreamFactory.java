// @formatter:off
/**
 * This file is part of streaming pool (http://www.streamingpool.org).
 * <p>
 * Copyright (c) 2017-present, CERN. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.streamingpool.core.service.streamid.FlattenedStreamId;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * {@link StreamFactory} for the {@link FlattenedStreamId}s
 *
 * @author timartin
 * @see FlattenedStreamId
 */
public class FlattenedStreamFactory implements StreamFactory {
    @Override
    public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof FlattenedStreamId)) {
            return ErrorStreamPair.empty();
        }

        FlattenedStreamId<T> flattenedStreamId = (FlattenedStreamId<T>) id;
        return createFlattenedStream(flattenedStreamId, discoveryService);
    }

    private <T> ErrorStreamPair<T> createFlattenedStream(FlattenedStreamId<T> id, DiscoveryService discoveryService) {
        Flowable<Iterable<T>> sourceStream = Flowable.fromPublisher(discoveryService.discover(id.sourceStreamId()));
        ErrorDeflector ed = ErrorDeflector.create();

        return ed.stream(sourceStream.flatMap(iterable -> {
            Stream<T> stream = StreamSupport.stream(iterable.spliterator(), false).filter(Objects::nonNull);
            return Flowable.fromIterable(stream.collect(Collectors.toList()));
        }));
    }
}
