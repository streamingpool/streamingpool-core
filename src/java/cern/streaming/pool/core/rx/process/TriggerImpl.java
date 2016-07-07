/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.rx.process;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class TriggerImpl implements Trigger {

    private BehaviorSubject<Object> subject = BehaviorSubject.create(true);

    @Override
    public void trigger() {
        subject.onNext(true);
    }

    @Override
    public Observable<Object> asObservable() {
        return subject.asObservable();
    }

}
