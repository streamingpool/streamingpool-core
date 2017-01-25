// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

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
