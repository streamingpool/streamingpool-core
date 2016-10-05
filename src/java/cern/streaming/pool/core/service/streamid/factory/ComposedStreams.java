package cern.streaming.pool.core.service.streamid.factory;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.CompositionStreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import org.springframework.util.CollectionUtils;
import rx.Observable;
import rx.functions.Func2;

import java.time.Duration;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Factory class which provides {@link StreamId}s that identify general purpose {@link ReactiveStream}s based on stream
 * composition.
 *
 * @author timartin
 */
public final class ComposedStreams {

    private ComposedStreams() {
    }

    /**
     * Creates a {@link StreamId} that will be used to create a {@link ReactiveStream} which will emit items based on a
     * {@link ReactiveStream} identified by the provided {@link StreamId}. The conversion function always returns an
     * {@link Optional}, if the value is present then it will be emitted, otherwise nothing will be emitted.
     *
     * @param sourceStreamId {@link StreamId} which identifies a {@link ReactiveStream} that will be used as the source
     *                       of the converted objects.
     * @param conversion     {@link Function} used to convert the objects.
     * @return A {@link StreamId}.
     * @throws NullPointerException If the provided source stream id or conversion function are null.
     * @see Observable#map(rx.functions.Func1)
     */
    public static final <X, T> StreamId<T> mappedStream(final StreamId<X> sourceStreamId,
                                                        final Function<X, Optional<T>> conversion) {
        Objects.requireNonNull(sourceStreamId, "sourceStreamId");
        Objects.requireNonNull(conversion, "conversion");
        return new CompositionStreamId<X, T>(sourceStreamId, reactiveStreams -> fromRx(rxFrom(reactiveStreams.get(0))
                .map(conversion::apply)
                .filter(Optional::isPresent).map(Optional::get)));
    }

    /**
     * Creates a {@link StreamId} that will identify a {@link ReactiveStream} which will emit items based on a
     * {@link ReactiveStream} identified by the provided {@link StreamId}. The conversion function always returns an
     * {@link Observable} which will used as the source of the objects to be flattened, if the observable emits values
     * then these will be emitted, otherwise nothing will be emitted.
     *
     * @param sourceStreamId {@link StreamId} which identifies the {@link ReactiveStream} that will be used as the
     *                       source of the converted objects.
     * @param conversion     {@link Function} used to convert the objects.
     * @return A {@link StreamId}.
     * @throws NullPointerException If the provided source stream id or conversion function are null.
     * @see Observable#flatMap(rx.functions.Func1)
     */
    public static final <X, T> StreamId<T> flatMappedStream(final StreamId<X> sourceStreamId,
                                                            final Function<X, Observable<T>> conversion) {
        Objects.requireNonNull(sourceStreamId, "sourceStreamId");
        Objects.requireNonNull(conversion, "conversion");
        return new CompositionStreamId<X, T>(sourceStreamId,
                reactiveStreams -> ReactiveStreams.fromRx(rxFrom(reactiveStreams.get(0)).flatMap(conversion::apply)));
    }

    /**
     * Creates a {@link StreamId} that will be used to create a {@link ReactiveStream} which will emit all the items
     * emitted by the {@link ReactiveStream}s identified by the provided {@link StreamId}s.
     *
     * @param sourceStreamIds {@link List} of {@link StreamId}s which identify {@link ReactiveStream}s that will be used
     *                        as the source of the new {@link ReactiveStream}.
     * @return A {@link StreamId}.
     * @throws IllegalArgumentException If the provided list of source stream ids is null or empty.
     * @see Observable#merge(Iterable)
     */
    public static final <X> StreamId<X> mergedStream(final List<StreamId<X>> sourceStreamIds) {
        checkCollectionAndThrow(sourceStreamIds, "sourceStreamIds");
        return new CompositionStreamId<>(sourceStreamIds, reactiveStreams -> {
            List<Observable<X>> observablesToMerge = new ArrayList<>();
            for (ReactiveStream<X> reactiveStream : reactiveStreams) {
                observablesToMerge.add(rxFrom(reactiveStream));
            }
            return ReactiveStreams.fromRx(Observable.merge(observablesToMerge));
        });
    }

    /**
     * Creates a {@link StreamId} that will be used to create a {@link ReactiveStream} which will filter and re-emit the
     * items emitted by the {@link ReactiveStream}s identified by the provided {@link StreamId}.
     *
     * @param sourceStreamId {@link StreamId} which identifies the {@link ReactiveStream} that will be used as the
     *                       source of the filtered objects.
     * @param predicate      {@link Predicate} that will be used to filter the items emitted by the source.
     * @return A {@link StreamId}.
     * @throws NullPointerException If the provided source stream id or predicate are null.
     * @see Observable#filter(rx.functions.Func1)
     */
    public static final <X> StreamId<X> filteredStream(final StreamId<X> sourceStreamId,
                                                       final Predicate<X> predicate) {
        Objects.requireNonNull(sourceStreamId, "sourceStreamId");
        Objects.requireNonNull(predicate, "predicate");
        return new CompositionStreamId<X, X>(sourceStreamId,
                reactiveStreams -> ReactiveStreams.fromRx(rxFrom(reactiveStreams.get(0)).filter(predicate::test)));
    }

    /**
     * Creates a {@link StreamId} that will be used to create a {@link ReactiveStream} which will re-emit all the items
     * emitted by the {@link ReactiveStream}s identified by the provided {@link StreamId} with the specified delay.
     *
     * @param sourceStreamId {@link StreamId} which identifies the {@link ReactiveStream} that will be used as the
     *                       source of the emitted objects.
     * @param duration       {@link Duration} that will be used as the delay before re-emitting.
     * @return A {@link StreamId}.
     * @throws NullPointerException If the provided source stream id or duration are null.
     * @see Observable#delay(long, java.util.concurrent.TimeUnit)
     */
    public static final <X> StreamId<X> delayedStream(final StreamId<X> sourceStreamId, final Duration duration) {
        Objects.requireNonNull(sourceStreamId, "sourceStreamId");
        Objects.requireNonNull(duration, "duration");
        return new CompositionStreamId<X, X>(sourceStreamId,
                reactiveStreams -> ReactiveStreams.fromRx(rxFrom(reactiveStreams.get(0)).delay(duration.toMillis(),
                        MILLISECONDS)));
    }

    /**
     * Creates a {@link StreamId} that will identify a {@link ReactiveStream} which will emit items generated using the
     * provided method and the values emitted by the {@link ReactiveStream}s identified by the provided {@link StreamId}
     * s. The zip function always returns an {@link Optional}, if the value is present then it will be emitted,
     * otherwise nothing will be emitted.
     *
     * @param sourceStreamId1 {@link StreamId} which identifies the {@link ReactiveStream} that will be used as the
     *                        source of the objects used by the zip function.
     * @param sourceStreamId2 {@link StreamId} which identifies the {@link ReactiveStream} that will be used as the
     *                        source of the objects used by the zip function.
     * @param zip             A {@link BiFunction} which will convert both objects emitted by the
     *                        {@link ReactiveStreams} into a single new instance of type T.
     * @return A {@link StreamId}.
     * @throws NullPointerException If any of the provided source stream ids or zip function are null.
     * @see Observable#zip(Observable, Observable, Func2)
     */
    public static final <X, T> StreamId<T> zippedStream(final StreamId<X> sourceStreamId1,
                                                        final StreamId<X> sourceStreamId2,
                                                        final BiFunction<X, X, Optional<T>> zip) {
        Objects.requireNonNull(sourceStreamId1, "sourceStreamId1");
        Objects.requireNonNull(sourceStreamId2, "sourceStreamId2");
        Objects.requireNonNull(zip, "zip");
        return new CompositionStreamId<X, T>(Arrays.asList(sourceStreamId1, sourceStreamId2),
                reactiveStreams -> ReactiveStreams
                        .fromRx(
                                Observable.zip(rxFrom(reactiveStreams.get(0)), rxFrom(reactiveStreams.get(1)),
                                        convertBiFunctionToRxFunc2(zip))
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)));
    }

    private static final void checkCollectionAndThrow(Collection collection, String collectionName) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new IllegalArgumentException("The collection " + collectionName + " cannot be null nor empty");
        }
    }

    private static final <X, T> Func2<X, X, Optional<T>> convertBiFunctionToRxFunc2(
            final BiFunction<X, X, Optional<T>> biFunction) {
        return new Func2<X, X, Optional<T>>() {
            @Override
            public Optional<T> call(X x, X x2) {
                return biFunction.apply(x, x2);
            }
        };
    }
}
