/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.support;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;

/**
 * Support interface for working with RxJava streams.
 * 
 * @author acalia
 */
public interface RxStreamSupport extends StreamSupport {

    default <T> ReactiveStream<T> streamFrom(Observable<T> observable) {
        return ReactiveStreams.fromRx(observable);
    }

    default <T> StreamSupport.OngoingProviding<T> provide(Observable<T> observable) {
        return provide(ReactiveStreams.fromRx(observable));
    }

    default <T> Publisher<T> publisherFrom(Observable<T> observable) {
        return ReactiveStreams.publisherFrom(streamFrom(observable));
    }

    default <T> Observable<T> rxFrom(StreamId<T> id) {
        return ReactiveStreams.rxFrom(discover(id));
    }

}
