package cern.streaming.pool.core.service.streamid.factory.function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

/**
 * Created by timartin on 06/10/2016.
 */
public class DelayCompositionFunction<X> implements Function<List<Publisher<X>>, Publisher<X>> {

    private final Duration duration;

    public DelayCompositionFunction(Duration duration) {
        Objects.requireNonNull(duration, "duration");
        this.duration = duration;
    }

    @Override
    public Publisher<X> apply(List<Publisher<X>> reactiveStreams) {
        return Flowable.fromPublisher(reactiveStreams.get(0)).delay(duration.toMillis(), MILLISECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DelayCompositionFunction<?> that = (DelayCompositionFunction<?>) o;

        return duration.equals(that.duration);

    }

    @Override
    public int hashCode() {
        return duration.hashCode();
    }
}
