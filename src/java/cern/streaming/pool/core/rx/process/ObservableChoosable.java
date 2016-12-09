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
