package akka;

import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;

import java.util.concurrent.CompletionStage;

/**
 * Created by mgalilee on 19/05/2016.
 *
 * Demo of different consumer rates, with the slower one using a dropping buffer not to backpressure the producer.
 * When run, we can see the 3rd consumer gets only a few elements of the stream, but at least the last 4 (the size of
 * its buffer).
 */
public class DifferentRatesDemo1 extends AbstractDifferentRatesDemo {

    private static final int BUFFER_SIZE = 4;

    public static void main(String[] args) {
        new DifferentRatesDemo1().run();
    }

    protected <T, U> Sink<T, CompletionStage<U>> droppySink(Sink<T, CompletionStage<U>> sink) {
        return Flow.<T>create()
                .buffer(BUFFER_SIZE, OverflowStrategy.dropHead())
                .toMat(sink, Keep.right());
    }
}
