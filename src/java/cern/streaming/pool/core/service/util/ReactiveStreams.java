/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.util;

import static rx.RxReactiveStreams.toObservable;
import static rx.RxReactiveStreams.toPublisher;

import org.reactivestreams.Publisher;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.SimpleReactStream;
import rx.Observable;
import rx.RxReactiveStreams;

/**
 * Utility methods for working with {@link ReactiveStream}s.
 * 
 */
public final class ReactiveStreams {

    private ReactiveStreams() {
        /* static methods only */
    }

    public static <T> Observable<T> rxFrom(ReactiveStream<T> stream) {
        return toObservable(publisherFrom(stream));
    }

    public static <T> ReactiveStream<T> fromRx(Observable<T> source) {
        return fromPublisher(toPublisher(source));
    }

    public static <T> Publisher<T> publisherFrom(ReactiveStream<T> stream) {
        return ((SimpleReactStream<T>) stream).getSource();
    }
    
    public static <T> Publisher<T> publisherFrom(Observable<T> source) {
        return RxReactiveStreams.toPublisher(source);
    }

    public static <T> ReactiveStream<T> fromPublisher(Publisher<T> publisher) {
        return new SimpleReactStream<>(publisher);
    }

    public static <T> Source<T, NotUsed> sourceFrom(ReactiveStream<T> stream) {
        return Source.fromPublisher(publisherFrom(stream));
    }

    @SuppressWarnings("unused")
    public static <T> Source<T, NotUsed> sourceFrom(StreamId<T> streamId) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

}
