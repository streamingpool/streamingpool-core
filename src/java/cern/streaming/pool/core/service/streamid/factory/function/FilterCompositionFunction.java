package cern.streaming.pool.core.service.streamid.factory.function;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.util.ReactiveStreams;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;

/**
 * Created by timartin on 06/10/2016.
 */
public class FilterCompositionFunction<X> implements Function<List<ReactiveStream<X>>, ReactiveStream<X>> {

    private final Predicate<X> predicate;

    public FilterCompositionFunction(Predicate<X> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.predicate = predicate;
    }

    @Override
    public ReactiveStream<X> apply(List<ReactiveStream<X>> reactiveStreams) {
        return ReactiveStreams.fromRx(rxFrom(reactiveStreams.get(0)).filter(predicate::test));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FilterCompositionFunction<?> that = (FilterCompositionFunction<?>) o;

        return predicate.equals(that.predicate);

    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }
}
