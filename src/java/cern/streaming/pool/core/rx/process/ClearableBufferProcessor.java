package cern.streaming.pool.core.rx.process;

import static cern.streaming.pool.core.rx.process.RunState.RUNNING;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Provides a buffered version of an observable of items of a certain type, where the buffering can be customized.
 * Additionally, the input is only propagated if the {@link RunState}, which is provided by another observable, is
 * {@link RunState#RUNNING}.
 * <p>
 * The following input observables can/must be provided:
 * <ul>
 * <li>An input observable ({@link #setInput(Observable)}): This observable provides the elements which have to be
 * buffered. This observable has to be provided (else the resulting observable will simply never publish anything).
 * <li>An observable of a runstate ({@link #setRunState(Observable)}): The items of the input observable are only
 * propagated when the run state is equal to {@link RunState#RUNNING}. If this observable is not provided or never emits
 * any item, then the state is considered as being {@link RunState#RUNNING} all the time.
 * <li>A trigger observable ({@link #setClearTrigger(Observable)}): Each time a new item is received on this observable,
 * a new (empty) buffer is created, which then fills up.
 * </ul>
 * The buffer is customized by the following parameters, which can be set at any time. However, they are only effective
 * at the moment when a new buffer is created (which happens anytime a new item is observed on the trigger observable).
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
    private BehaviorSubject<List<T>> bufferedContent = BehaviorSubject.create();
    private ConcurrentCircularBuffer<T> buffer = new ConcurrentCircularBuffer<>();

    public void setClearTrigger(Observable<?> triggerClear) {
        triggerClear.subscribe(object -> buffer.clear());
    }

    public Observable<List<T>> bufferedContent() {
        return bufferedContent;
    }

    public void setMinEmitSize(int minEmitSize) {
        this.minEmitSize.set(minEmitSize);
    }

    public void setBufferSize(int bufferSize) {
        buffer.setLength(bufferSize);
    }

    public void setBufferSize(Observable<Integer> bufferSize) {
        bufferSize.subscribe(buffer::setLength);
    }

    public void setInput(Observable<T> input) {
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

    public void setRunState(Observable<RunState> runState) {
        runState.subscribe(this.runState::set);
    }

}
