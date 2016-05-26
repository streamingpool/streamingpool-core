package akka;

import akka.japi.function.Function2;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;

import java.util.concurrent.CompletionStage;

/**
 * Created by mgalilee on 19/05/2016.
 *
 * Demo of different consumer rates, with the slower one using a conflating mechanism not to backpressure the producer.
 * When run, we can see the 3rd consumer gets only a few elements of the stream, and always the latest.
 * The conflation is also printed, stating which elements are merged.
 */
public class DifferentRatesDemo2 extends AbstractDifferentRatesDemo {

    public static void main(String[] args) {
        new DifferentRatesDemo2().run();
    }

    protected <T, U> Sink<T, CompletionStage<U>> droppySink(Sink<T, CompletionStage<U>> sink) {
        Function2<T, T, T> function2 = (older, newer) -> {
            System.out.println("conflating " + older + " and " + newer + " to " + newer);
            return newer;
        };
        return Flow.<T>create()
                .conflate(function2)
                .toMat(sink, Keep.right());
    }
}
