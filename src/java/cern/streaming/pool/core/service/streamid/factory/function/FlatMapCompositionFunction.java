package cern.streaming.pool.core.service.streamid.factory.function;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

/**
 * Created by timartin on 06/10/2016.
 */
public class FlatMapCompositionFunction<X, T> implements Function<List<Publisher<X>>, Publisher<T>> {

    private final Function<X, Publisher<T>> flatMapConversion;

    public FlatMapCompositionFunction(Function<X, Publisher<T>> flatMapConversion) {
        Objects.requireNonNull(flatMapConversion);
        this.flatMapConversion = flatMapConversion;
    }

    @Override
    public Publisher<T> apply(List<Publisher<X>> reactiveStreams) {
        return Flowable.fromPublisher(reactiveStreams.get(0)).flatMap(flatMapConversion::apply);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FlatMapCompositionFunction<?, ?> that = (FlatMapCompositionFunction<?, ?>) o;

        return flatMapConversion.equals(that.flatMapConversion);

    }

    @Override
    public int hashCode() {
        return flatMapConversion.hashCode();
    }
}
