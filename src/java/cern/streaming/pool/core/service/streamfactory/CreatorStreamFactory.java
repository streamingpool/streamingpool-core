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

package cern.streaming.pool.core.service.streamfactory;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.CreatorProvidingService;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.TypedStreamFactory;
import cern.streaming.pool.core.service.impl.IdentifiedStreamCreator;
import cern.streaming.pool.core.service.impl.ImmutableIdentifiedStreamCreator;
import io.reactivex.Flowable;

/**
 * {@link TypedStreamFactory} specifically designed to create {@link ReactiveStream}s using {@link StreamCreator}s. In
 * order to use the right {@link StreamCreator} for creating the {@link ReactiveStream}, it uses
 * {@link ImmutableIdentifiedStreamCreator} to map a specific {@link StreamId} to the correspondent
 * {@link StreamCreator}.
 * 
 * @see StreamCreator
 * @see ImmutableIdentifiedStreamCreator
 */
public class CreatorStreamFactory implements CreatorProvidingService, StreamFactory {

    private final ConcurrentMap<StreamId<?>, StreamCreator<?>> suppliers = new ConcurrentHashMap<>();

    public CreatorStreamFactory(Iterable<IdentifiedStreamCreator<?>> identifiedCreators) {
        requireNonNull(identifiedCreators, "identifiedStreamCreators must not be null.");

        identifiedCreators.forEach(this::register);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<Publisher<T>> create(StreamId<T> newId, DiscoveryService discoveryService) {
        StreamCreator<?> streamCreator = suppliers.get(newId);
        if (streamCreator == null) {
            return Optional.empty();
        }
        return Optional.of((Flowable<T>) streamCreator.createWith(discoveryService));
    }

    @Override
    public <T> void provide(StreamId<T> id, StreamCreator<T> streamSupplier) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(streamSupplier, "stream suplier must not be null!");

        StreamCreator<?> existingCreator = suppliers.putIfAbsent(id, streamSupplier);
        if (existingCreator != null) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

    private <T> void register(IdentifiedStreamCreator<T> identifiedCreator) {
        suppliers.put(identifiedCreator.getId(), identifiedCreator.getCreator());
    }
}
