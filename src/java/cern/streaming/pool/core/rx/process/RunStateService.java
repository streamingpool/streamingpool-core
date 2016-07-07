/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.rx.process;

import rx.Observable;

public interface RunStateService {

    RunState getRunState();

    void switchTo(RunState runState);

    Observable<RunState> asObservable();

}
