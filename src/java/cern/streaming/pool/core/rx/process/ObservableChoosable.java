package cern.streaming.pool.core.rx.process;

import static java.util.Objects.requireNonNull;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class ObservableChoosable<T> implements Choosable<T> {

    private final BehaviorSubject<Iterable<T>> allValuesSubject;
    private final BehaviorSubject<T> actualValueSubject;

    private ObservableChoosable(Iterable<T> allValues, T defaultValue) {
        requireNonNull(defaultValue, "the default value must not be null");
        requireNonNull(allValues, "allValues must not be null");
        this.actualValueSubject = BehaviorSubject.create(defaultValue);
        this.allValuesSubject = BehaviorSubject.create(allValues);
    }

    public static <T> ObservableChoosable<T> allWithDefault(Iterable<T> allValues, T defaultValue) {
        return new ObservableChoosable<>(allValues, defaultValue);
    }

    @Override
    public void choose(T newlySelected) {
        requireNonNull(newlySelected, "the newly selected object must not be null");
        actualValueSubject.onNext(newlySelected);
    }

    public Observable<Iterable<T>> allOptions() {
        return this.allValuesSubject;
    }

    public Observable<T> choice() {
        return actualValueSubject.asObservable();
    }

}
