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

import static cern.streaming.pool.core.rx.process.RunState.RUNNING;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Flowable;
import io.reactivex.processors.BehaviorProcessor;

/**
 * Provides a buffered version of an observable of items of a certain type, where the buffering can be customized.
 * Additionally, the input is only propagated if the {@link RunState}, which is provided by another Flowable, is
 * {@link RunState#RUNNING}.
 * <p>
 * The following input Flowable can/must be provided:
 * <ul>
 * <li>An input Flowable ({@link #setInput(Flowable)}): This Flowable provides the elements which have to be buffered.
 * This Flowable has to be provided (else the resulting Flowable will simply never publish anything).
 * <li>A Flowable of a runstate ({@link #setRunState(Flowable)}): The items of the input Flowable are only propagated
 * when the run state is equal to {@link RunState#RUNNING}. If this Flowable is not provided or never emits any item,
 * then the state is considered as being {@link RunState#RUNNING} all the time.
 * <li>A trigger Flowable ({@link #setClearTrigger(Flowable)}): Each time a new item is received on this Flowable, a new
 * (empty) buffer is created, which then fills up.
 * </ul>
 * The buffer is customized by the following parameters, which can be set at any time. However, they are only effective
 * at the moment when a new buffer is created (which happens anytime a new item is observed on the trigger Flowable).
 * <p>
 * The possible options are:
 * <ul>
 * <li>{@link #setBufferSize(int)}: The size of the buffers. The default value for this is {@value #DEFAULT_BUFFER_SIZE}
 * .
 * <li>{@link #setSkip(int)}: the number items of the input variable to skip. The default value for this is
 * {@value #DEFAULT_SKIP}.
 * <li>{@link #setMinEmitSize(int)}: This is the first index at which the first buffer starts emitting. This buffer then
 * fills up and is emitted when it is full. All following buffers are only emitted once. The default value for this is
 * {@value #DEFAULT_MIN_EMIT_SIZE}.
 * </ul>
 * 
 * @author kfuchsbe
 * @param <T> of the observable items to buffer.
 */
public class ClearableBufferProcessor<T> {

    private static final RunState DEFAULT_RUN_STATE = RUNNING;
    private static final int DEFAULT_MIN_EMIT_SIZE = 1;

    private AtomicInteger minEmitSize = new AtomicInteger(DEFAULT_MIN_EMIT_SIZE);
    private final AtomicReference<RunState> runState = new AtomicReference<>(DEFAULT_RUN_STATE);
    private BehaviorProcessor<List<T>> bufferedContent = BehaviorProcessor.create();
    private ConcurrentCircularBuffer<T> buffer = new ConcurrentCircularBuffer<>();

    public void setClearTrigger(Flowable<?> triggerClear) {
        triggerClear.subscribe(object -> buffer.clear());
    }

    public Flowable<List<T>> bufferedContent() {
        return bufferedContent;
    }

    public void setMinEmitSize(int minEmitSize) {
        this.minEmitSize.set(minEmitSize);
    }

    public void setBufferSize(int bufferSize) {
        buffer.setLength(bufferSize);
    }

    public void setBufferSize(Flowable<Integer> bufferSize) {
        bufferSize.subscribe(buffer::setLength);
    }

    public void setInput(Flowable<T> input) {
        input.subscribe(element -> {
            if (isAcquiring()) {
                buffer.add(element);
                publish();
            }
        });
    }

    private boolean isAcquiring() {
        return RunState.RUNNING.equals(runState.get());
    }

    private void publish() {
        List<T> bufferedList = buffer.toList();
        if (bufferedList.size() >= minEmitSize.get()) {
            bufferedContent.onNext(bufferedList);
        }
    }

    public void setRunState(RunState runState) {
        this.runState.set(runState);
    }

    public void setRunState(Flowable<RunState> runState) {
        runState.subscribe(this.runState::set);
    }

}
