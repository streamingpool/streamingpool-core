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
package org.streamingpool.core.service.streamid.factory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;
import org.springframework.util.CollectionUtils;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.CompositionStreamId;
import org.streamingpool.core.service.streamid.DelayedStreamId;
import org.streamingpool.core.service.streamid.DerivedStreamId;
import org.streamingpool.core.service.streamid.FilteredStreamId;
import org.streamingpool.core.service.streamid.factory.function.DelayCompositionFunction;
import org.streamingpool.core.service.streamid.factory.function.FilterCompositionFunction;
import org.streamingpool.core.service.streamid.factory.function.FlatMapCompositionFunction;
import org.streamingpool.core.service.streamid.factory.function.MapCompositionFunction;
import org.streamingpool.core.service.streamid.factory.function.ZipCompositionFunction;

import io.reactivex.Flowable;
/**
 * Factory class which provides {@link StreamId}s that identify general purpose {@link org.reactivestreams.Publisher}s based on stream
 * composition. This class is experimental.
 *
 * @author timartin
 */
public final class ComposedStreams {

    private ComposedStreams() {
    }

    /**
     * Creates a {@link StreamId} that will be used to create a {@link org.reactivestreams.Publisher} which will emit items based on a
     * {@link org.reactivestreams.Publisher} identified by the provided {@link StreamId}. The conversion function always returns an
     * {@link Optional}, if the value is present then it will be emitted, otherwise nothing will be emitted.
     *
     * @param sourceStreamId {@link StreamId} which identifies a {@link org.reactivestreams.Publisher} that will be used as the source
     *                       of the converted objects.
     * @param conversion     {@link Function} used to convert the objects.
     * @return A {@link StreamId}.
     * @throws NullPointerException If the provided source stream id or conversion function are null.
     * @see MapCompositionFunction
     */
    public static final <X, T> StreamId<T> mappedStream(final StreamId<X> sourceStreamId,
                                                        final Function<X, T> conversion) {
        Objects.requireNonNull(sourceStreamId, "sourceStreamId");
        Objects.requireNonNull(conversion, "conversion");
        return new DerivedStreamId<>(sourceStreamId, conversion);
    }

    /**
     * EXPERIMENTAL
     * Creates a {@link StreamId} that will identify a {@link org.reactivestreams.Publisher} which will emit items based on a
     * {@link org.reactivestreams.Publisher} identified by the provided {@link StreamId}. The conversion function always returns a
     * {@link org.reactivestreams.Publisher} which will used as the source of the objects to be flattened, if the stream emits values
     * then these will be emitted, otherwise nothing will be emitted.
     *
     * @param sourceStreamId {@link StreamId} which identifies the {@link org.reactivestreams.Publisher} that will be used as the
     *                       source of the converted objects.
     * @param conversion     {@link Function} used to convert the objects.
     * @return A {@link StreamId}.
     * @throws NullPointerException If the provided source stream id or conversion function are null.
     * @see FlatMapCompositionFunction
     */

    public static final <X, T> StreamId<T> flatMappedStream(final StreamId<X> sourceStreamId,
                                                            final Function<X, Publisher<T>> conversion) {
        Objects.requireNonNull(sourceStreamId, "sourceStreamId");
        Objects.requireNonNull(conversion, "conversion");
        return new CompositionStreamId<>(sourceStreamId, new FlatMapCompositionFunction<>(conversion));
    }

    /**
     * EXPERIMENTAL
     * Creates a {@link StreamId} that will be used to create a {@link org.reactivestreams.Publisher} which will emit all the items
     * emitted by the {@link org.reactivestreams.Publisher}s identified by the provided {@link StreamId}s.
     *
     * @param sourceStreamIds {@link List} of {@link StreamId}s which identify {@link org.reactivestreams.Publisher}s that will be used
     *                        as the source of the new {@link org.reactivestreams.Publisher}.
     * @return A {@link StreamId}.
     * @throws IllegalArgumentException If the provided list of source stream ids is null or empty.
     * @see Flowable#merge(Iterable)
     */
    public static final <X> StreamId<X> mergedStream(final List<StreamId<X>> sourceStreamIds) {
        checkCollectionAndThrow(sourceStreamIds, "sourceStreamIds");
        return new CompositionStreamId<>(sourceStreamIds, reactiveStreams -> {
            List<Flowable<X>> observablesToMerge = new ArrayList<>();
            for (Publisher<X> reactiveStream : reactiveStreams) {
                observablesToMerge.add(Flowable.fromPublisher(reactiveStream));
            }
            return Flowable.merge(observablesToMerge);
        });
    }

    /**
     * Creates a {@link StreamId} that will be used to create a {@link org.reactivestreams.Publisher} which will filter and re-emit the
     * items emitted by the {@link org.reactivestreams.Publisher}s identified by the provided {@link StreamId}.
     *
     * @param sourceStreamId {@link StreamId} which identifies the {@link org.reactivestreams.Publisher} that will be used as the
     *                       source of the filtered objects.
     * @param predicate      {@link Predicate} that will be used to filter the items emitted by the source.
     * @return A {@link StreamId}.
     * @throws NullPointerException If the provided source stream id or predicate are null.
     * @see FilterCompositionFunction
     */
    public static final <X> StreamId<X> filteredStream(final StreamId<X> sourceStreamId,
                                                       final Predicate<X> predicate) {
        Objects.requireNonNull(sourceStreamId, "sourceStreamId");
        Objects.requireNonNull(predicate, "predicate");
        return new FilteredStreamId<>(sourceStreamId, predicate);
    }

    /**
     * Creates a {@link StreamId} that will be used to create a {@link org.reactivestreams.Publisher} which will re-emit all the items
     * emitted by the {@link org.reactivestreams.Publisher}s identified by the provided {@link StreamId} with the specified delay.
     *
     * @param sourceStreamId {@link StreamId} which identifies the {@link org.reactivestreams.Publisher} that will be used as the
     *                       source of the emitted objects.
     * @param duration       {@link Duration} that will be used as the delay before re-emitting.
     * @return A {@link StreamId}.
     * @throws NullPointerException If the provided source stream id or duration are null.
     * @see DelayCompositionFunction
     */
    public static final <X> StreamId<X> delayedStream(final StreamId<X> sourceStreamId, final Duration duration) {
        Objects.requireNonNull(sourceStreamId, "sourceStreamId");
        Objects.requireNonNull(duration, "duration");
        return new DelayedStreamId<>(sourceStreamId, duration);
    }

    /**
     * EXPERIMENTAL
     * Creates a {@link StreamId} that will identify a {@link org.reactivestreams.Publisher} which will emit items generated using the
     * provided method and the values emitted by the {@link org.reactivestreams.Publisher}s identified by the provided {@link StreamId}
     * s. The zip function always returns an {@link Optional}, if the value is present then it will be emitted,
     * otherwise nothing will be emitted.
     *
     * @param sourceStreamId1 {@link StreamId} which identifies the {@link org.reactivestreams.Publisher} that will be used as the
     *                        source of the objects used by the zip function.
     * @param sourceStreamId2 {@link StreamId} which identifies the {@link org.reactivestreams.Publisher} that will be used as the
     *                        source of the objects used by the zip function.
     * @param zip             A {@link BiFunction} which will merge both objects into a single new instance of type T.
     * @return A {@link StreamId}.
     * @throws NullPointerException If any of the provided source stream ids or zip function are null.
     * @see ZipCompositionFunction
     */
    public static final <X, T> StreamId<T> zippedStream(final StreamId<X> sourceStreamId1,
                                                        final StreamId<X> sourceStreamId2,
                                                        final BiFunction<X, X, Optional<T>> zip) {
        Objects.requireNonNull(sourceStreamId1, "sourceStreamId1");
        Objects.requireNonNull(sourceStreamId2, "sourceStreamId2");
        Objects.requireNonNull(zip, "zip");
        return new CompositionStreamId<X, T>(Arrays.asList(sourceStreamId1, sourceStreamId2),
                new ZipCompositionFunction<>(zip));
    }

    private static void checkCollectionAndThrow(Collection<?> collection, String collectionName) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException("The collection " + collectionName + " cannot be null nor empty");
        }
    }
}
