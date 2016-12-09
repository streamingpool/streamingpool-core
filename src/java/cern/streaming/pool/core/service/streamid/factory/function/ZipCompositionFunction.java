package cern.streaming.pool.core.service.streamid.factory.function;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

/**
 * Created by timartin on 06/10/2016.
 */
public class ZipCompositionFunction<X, T> implements Function<List<Publisher<X>>, Publisher<T>> {

    private final BiFunction<X, X, Optional<T>> conversion;

    public ZipCompositionFunction(BiFunction<X, X, Optional<T>> conversion) {
        Objects.requireNonNull(conversion, "conversion");
        this.conversion = conversion;
    }

    @Override
    public Publisher<T> apply(List<Publisher<X>> reactiveStreams) {
        return Flowable.zip(Flowable.fromPublisher(reactiveStreams.get(0)),
                Flowable.fromPublisher(reactiveStreams.get(1)), conversion::apply).filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ZipCompositionFunction<?, ?> that = (ZipCompositionFunction<?, ?>) o;

        return conversion.equals(that.conversion);

    }

    @Override
    public int hashCode() {
        return conversion.hashCode();
    }
}
