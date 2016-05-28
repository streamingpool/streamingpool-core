/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.support;

import org.reactivestreams.Publisher;

import rx.Observable;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamId;
import stream.support.StreamSupport.OngoingProviding;

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
