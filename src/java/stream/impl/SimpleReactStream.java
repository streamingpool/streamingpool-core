/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.impl;

import rx.Observable;
import stream.ReactStream;

public class SimpleReactStream <T> implements ReactStream <T> {
    
    private final Observable<T> source;

    public SimpleReactStream(Observable<T> source) {
        this.source = source;
    }

    public Observable<T> getSource() {
        return source;
    }
    
}
