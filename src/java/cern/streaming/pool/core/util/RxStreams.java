package cern.streaming.pool.core.util;

import rx.Observable;

public class RxStreams {

	public static final <T> T awaitNext(Observable<T> rxStream) {
		return rxStream.cache().toBlocking().first();
	}

}
