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

package org.streamingpool.core.service.akka;

import static akka.stream.javadsl.AsPublisher.WITH_FANOUT;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;

import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

public class AkkaStreamFactory implements AkkaSourceProvidingService, StreamFactory {

    private final Materializer materializer;
    private final ConcurrentMap<StreamId<?>, Source<?, ?>> suppliers = new ConcurrentHashMap<>();

    public AkkaStreamFactory(Materializer materializer) {
        this.materializer = Objects.requireNonNull(materializer, "materializer must not be null.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Publisher<T>> create(StreamId<T> newId, DiscoveryService discoveryService) {
        Source<T, ?> source = (Source<T, ?>) suppliers.get(newId);
        if (source == null) {
            return Optional.empty();
        }
        Sink<T, Publisher<T>> akkaSink = Sink.asPublisher(WITH_FANOUT);
        return Optional.of(source.runWith(akkaSink, materializer));
    }

    @Override
    public <T> void provide(StreamId<T> id, Source<T, ?> akkaSource) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(akkaSource, "akkaSource must not be null!");

        Source<?, ?> existingSource = suppliers.putIfAbsent(id, akkaSource);
        if (existingSource != null) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

}
