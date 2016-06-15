/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.util;

import static rx.RxReactiveStreams.toObservable;
import static rx.RxReactiveStreams.toPublisher;

import org.reactivestreams.Publisher;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.impl.NamedStreamId;
import cern.streaming.pool.core.service.impl.SimpleReactStream;
import rx.Observable;

/**
 * Utility methods for working with {@link ReactStream}s.
 * 
 */
public final class ReactStreams {

    private ReactStreams() {
        /* static methods only */
    }

    public static <T> Observable<T> rxFrom(ReactStream<T> stream) {
        return toObservable(publisherFrom(stream));
    }

    public static <T> ReactStream<T> fromRx(Observable<T> source) {
        return fromPublisher(toPublisher(source));
    }

    public static <T> Publisher<T> publisherFrom(ReactStream<T> stream) {
        return ((SimpleReactStream<T>) stream).getSource();
    }

    public static <T> ReactStream<T> fromPublisher(Publisher<T> publisher) {
        return new SimpleReactStream<>(publisher);
    }

    public static <T> Source<T, NotUsed> sourceFrom(ReactStream<T> stream) {
        return Source.fromPublisher(publisherFrom(stream));
    }

    @SuppressWarnings("unused")
    public static <T> Source<T, NotUsed> sourceFrom(StreamId<T> streamId) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    public static <T> StreamId<T> namedId(String name) {
        return new NamedStreamId<>(name);
    }

}
