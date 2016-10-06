package cern.streaming.pool.core.service.streamid.factory.function;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.util.ReactiveStreams;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;


/**
 * Created by timartin on 06/10/2016.
 */
public class FlatMapCompositionFunction<X, T> implements Function<List<ReactiveStream<X>>, ReactiveStream<T>> {

    private final Function<X, ReactiveStream<T>> flatMapConversion;

    public FlatMapCompositionFunction(Function<X, ReactiveStream<T>> flatMapConversion) {
        Objects.requireNonNull(flatMapConversion);
        this.flatMapConversion = flatMapConversion;
    }

    @Override
    public ReactiveStream<T> apply(List<ReactiveStream<X>> reactiveStreams) {
        return ReactiveStreams.fromRx(ReactiveStreams.rxFrom(reactiveStreams.get(0)).flatMap(val -> ReactiveStreams.rxFrom(flatMapConversion.apply(val))));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlatMapCompositionFunction<?, ?> that = (FlatMapCompositionFunction<?, ?>) o;

        return flatMapConversion.equals(that.flatMapConversion);

    }

    @Override
    public int hashCode() {
        return flatMapConversion.hashCode();
    }
}
