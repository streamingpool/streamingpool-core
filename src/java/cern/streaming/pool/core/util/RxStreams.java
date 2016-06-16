package cern.streaming.pool.core.util;

import rx.Observable;

public final class RxStreams {
    
    private RxStreams() {
        /* only static methods */
    }

    public static <T> T awaitNext(Observable<T> rxStream) {
        return rxStream.cache().toBlocking().first();
    }

}
