/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.util;

import org.reactivestreams.Publisher;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import cern.streaming.pool.core.service.StreamId;
import io.reactivex.Flowable;

/**
 * Utility methods for working with {@link ReactiveStream}s.
 * 
 * @deprecated with the dependency on {@link Publisher} directly, no need for these methods anymore. Use technology
 *             specific
 */
@Deprecated
public final class ReactiveStreams {

    private ReactiveStreams() {
        /* static methods only */
    }

    /**
     * @deprecated rxjava2 has {@link Flowable#fromPublisher(Publisher)}
     */
    public static <T> Flowable<T> rxFrom(Publisher<T> stream) {
        return Flowable.fromPublisher(stream);
    }

    /**
     * @deprecated {@link Flowable} is a {@link Publisher}
     */
    public static <T> Publisher<T> fromRx(Flowable<T> source) {
        return source;
    }

    /**
     * @deprecated useless
     * @see ReactiveStreams#fromRx(Flowable)
     */
    public static <T> Publisher<T> publisherFrom(Flowable<T> source) {
        return source;
    }

    /**
     * @deprecated {@link Source} has a {@link Source#fromPublisher(Publisher)} method
     */
    public static <T> Source<T, NotUsed> sourceFrom(Publisher<T> stream) {
        return Source.fromPublisher(stream);
    }

    @SuppressWarnings("unused")
    public static <T> Source<T, NotUsed> sourceFrom(StreamId<T> streamId) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

}
