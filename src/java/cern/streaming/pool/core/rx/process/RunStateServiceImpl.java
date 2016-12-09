/**
 * Copyright (c) 2015 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.rx.process;

import static java.util.Objects.requireNonNull;

import io.reactivex.Flowable;
import io.reactivex.processors.BehaviorProcessor;

public class RunStateServiceImpl implements RunStateService {

    private final BehaviorProcessor<RunState> runState;

    public RunStateServiceImpl() {
        this(RunState.PAUSED);
    }

    public RunStateServiceImpl(RunState initialState) {
        runState = BehaviorProcessor.createDefault(requireNonNull(initialState, "initial state must not be null"));
    }

    @Override
    public RunState getRunState() {
        return runState.getValue();
    }

    @Override
    public void switchTo(RunState newState) {
        runState.onNext(requireNonNull(newState, "new state must not be null"));
    }

    @Override
    public Flowable<RunState> asObservable() {
        return runState;
    }

}
