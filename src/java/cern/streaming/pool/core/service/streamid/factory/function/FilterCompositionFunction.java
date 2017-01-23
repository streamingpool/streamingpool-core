package cern.streaming.pool.core.service.streamid.factory.function;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

/**
 * Created by timartin on 06/10/2016.
 */
public class FilterCompositionFunction<X> implements Function<List<Publisher<X>>, Publisher<X>> {

    private final Predicate<X> predicate;

    public FilterCompositionFunction(Predicate<X> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.predicate = predicate;
    }

    @Override
    public Publisher<X> apply(List<Publisher<X>> reactiveStreams) {
        return Flowable.fromPublisher(reactiveStreams.get(0)).filter(predicate::test);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FilterCompositionFunction<?> that = (FilterCompositionFunction<?>) o;

        return predicate.equals(that.predicate);

    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }
}
