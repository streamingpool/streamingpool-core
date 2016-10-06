package cern.streaming.pool.core.service.streamid.factory.function;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.util.ReactiveStreams;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by timartin on 06/10/2016.
 */
public class MapCompositionFunction<X, T> implements Function<List<ReactiveStream<X>>, ReactiveStream<T>> {

    private final Function<X, Optional<T>> mapFunction;

    public MapCompositionFunction(Function<X, Optional<T>> mapFunction) {
        Objects.requireNonNull(mapFunction, "mapFunction");
        this.mapFunction = mapFunction;
    }

    @Override
    public ReactiveStream<T> apply(List<ReactiveStream<X>> reactiveStreams) {
        return ReactiveStreams.fromRx(ReactiveStreams.rxFrom(reactiveStreams.get(0))
                .map(mapFunction::apply)
                .filter(Optional::isPresent)
                .map(Optional::get));
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
