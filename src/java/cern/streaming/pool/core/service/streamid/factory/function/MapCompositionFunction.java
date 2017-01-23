package cern.streaming.pool.core.service.streamid.factory.function;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

/**
 * Created by timartin on 06/10/2016.
 */
public class MapCompositionFunction<X, T> implements Function<List<Publisher<X>>, Publisher<T>> {

    private final Function<X, Optional<T>> mapFunction;

    public MapCompositionFunction(Function<X, Optional<T>> mapFunction) {
        Objects.requireNonNull(mapFunction, "mapFunction");
        this.mapFunction = mapFunction;
    }

    @Override
    public Publisher<T> apply(List<Publisher<X>> reactiveStreams) {
        return Flowable.fromPublisher(reactiveStreams.get(0))
                .map(mapFunction::apply)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapCompositionFunction<?, ?> that = (MapCompositionFunction<?, ?>) o;

        return mapFunction != null ? mapFunction.equals(that.mapFunction) : that.mapFunction == null;

    }

    @Override
    public int hashCode() {
        return mapFunction != null ? mapFunction.hashCode() : 0;
    }
}
