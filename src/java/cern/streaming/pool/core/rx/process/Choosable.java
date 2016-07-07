package cern.streaming.pool.core.rx.process;

public interface Choosable<T> {

    void choose(T newlySelected);

}
