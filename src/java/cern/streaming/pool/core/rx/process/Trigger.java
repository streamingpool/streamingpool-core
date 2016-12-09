/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.rx.process;

import io.reactivex.Flowable;

public interface Trigger {

    void trigger();

    Flowable<Object> asObservable();

}
