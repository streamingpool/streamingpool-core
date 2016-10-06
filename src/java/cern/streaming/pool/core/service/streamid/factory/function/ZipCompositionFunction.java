package cern.streaming.pool.core.service.streamid.factory.function;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;
import rx.functions.Func2;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;

/**
 * Created by timartin on 06/10/2016.
 */
public class ZipCompositionFunction<X, T> implements Function<List<ReactiveStream<X>>, ReactiveStream<T>> {

    private final BiFunction<X, X, Optional<T>> conversion;

    public ZipCompositionFunction(BiFunction<X, X, Optional<T>> conversion) {
        Objects.requireNonNull(conversion, "conversion");
        this.conversion = conversion;
    }

    @Override
    public ReactiveStream<T> apply(List<ReactiveStream<X>> reactiveStreams) {
        return ReactiveStreams.fromRx(Observable.zip(ReactiveStreams.rxFrom(reactiveStreams.get(0)),
                rxFrom(reactiveStreams.get(1)), convertBiFunctionToRxFunc2(conversion))
                .filter(Optional::isPresent)
                .map(Optional::get));
    }

    private static final <X, T> Func2<X, X, Optional<T>> convertBiFunctionToRxFunc2(
            final BiFunction<X, X, Optional<T>> biFunction) {
        return new Func2<X, X, Optional<T>>() {
            @Override
            public Optional<T> call(X x, X x2) {
                return biFunction.apply(x, x2);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZipCompositionFunction<?, ?> that = (ZipCompositionFunction<?, ?>) o;

        return conversion.equals(that.conversion);

    }

    @Override
    public int hashCode() {
        return conversion.hashCode();
    }
}
