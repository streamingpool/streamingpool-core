/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import rx.Observable;
import stream.impl.SimpleReactStream;

public final class ReactStreams {

    private ReactStreams() {
    }
    
    public static <T> Observable<T> rxFrom(ReactStream<T> stream) {
        return ((SimpleReactStream<T>) stream).getSource();
    }
    
    public static <T> ReactStream<T> fromRx(Observable<T> source) {
        return new SimpleReactStream<>(source);
    }
}

