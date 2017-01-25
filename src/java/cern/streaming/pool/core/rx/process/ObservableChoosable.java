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

public class ObservableChoosable<T> implements Choosable<T> {

    private final BehaviorProcessor<Iterable<T>> allValuesSubject;
    private final BehaviorProcessor<T> actualValueSubject;

    private ObservableChoosable(Iterable<T> allValues, T defaultValue) {
        requireNonNull(defaultValue, "the default value must not be null");
        requireNonNull(allValues, "allValues must not be null");
        this.actualValueSubject = BehaviorProcessor.createDefault(defaultValue);
        this.allValuesSubject = BehaviorProcessor.createDefault(allValues);
    }

    public static <T> ObservableChoosable<T> allWithDefault(Iterable<T> allValues, T defaultValue) {
        return new ObservableChoosable<>(allValues, defaultValue);
    }

    @Override
    public void choose(T newlySelected) {
        requireNonNull(newlySelected, "the newly selected object must not be null");
        actualValueSubject.onNext(newlySelected);
    }

    public Flowable<Iterable<T>> allOptions() {
        return this.allValuesSubject;
    }

    public Flowable<T> choice() {
        return actualValueSubject;
    }

}
