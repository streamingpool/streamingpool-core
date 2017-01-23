/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.rx.process;

import io.reactivex.Flowable;
import io.reactivex.processors.BehaviorProcessor;

public class TriggerImpl implements Trigger {

    private BehaviorProcessor<Object> subject = BehaviorProcessor.createDefault(true);

    @Override
    public void trigger() {
        subject.onNext(true);
    }

    @Override
    public Flowable<Object> asObservable() {
        return subject;
    }

}
