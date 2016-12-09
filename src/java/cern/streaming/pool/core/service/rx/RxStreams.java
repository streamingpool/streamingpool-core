package cern.streaming.pool.core.service.rx;

import io.reactivex.Flowable;

public final class RxStreams {
    
    private RxStreams() {
        /* only static methods */
    }

    public static <T> T awaitNext(Flowable<T> rxStream) {
        return rxStream.blockingFirst();
    }

}
