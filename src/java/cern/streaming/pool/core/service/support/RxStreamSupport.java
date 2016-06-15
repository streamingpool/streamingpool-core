/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.support;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.ReactStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.util.ReactStreams;
import rx.Observable;

/**
 * Support interface for working with RxJava streams.
 * 
 * @author acalia
 */
public interface RxStreamSupport extends StreamSupport {

    default <T> ReactStream<T> streamFrom(Observable<T> observable) {
        return ReactStreams.fromRx(observable);
    }

    default <T> StreamSupport.OngoingProviding<T> provide(Observable<T> observable) {
        return provide(ReactStreams.fromRx(observable));
    }

    default <T> Publisher<T> publisherFrom(Observable<T> observable) {
        return ReactStreams.publisherFrom(streamFrom(observable));
    }

    default <T> Observable<T> rxFrom(StreamId<T> id) {
        return ReactStreams.rxFrom(discover(id));
    }

}
