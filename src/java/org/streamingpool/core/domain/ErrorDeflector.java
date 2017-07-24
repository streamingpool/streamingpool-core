/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.domain;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.processors.PublishProcessor;

/**
 * Provides different ways to intercept exceptions from streams (or lambdas) and deflect the caught exceptions onto an
 * error stream. The error stream (subject) is created internally. An external event can be pushed onto it bu using the
 * {@link #publishException(Throwable)} method. Further it will be included in the errors stream pair in case the two
 * factory methods for these are used ({@link #stream(Publisher)} or {@link #streamNonEmpty(Publisher)}).
 * <p>
 * Since one error stream usually corresponds to one data stream, each time a new stream is created, a new instance of
 * an error deflector should be created, so that the contained error stream is not shared between different data
 * streams.
 */
public final class ErrorDeflector {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorDeflector.class);

    /** The subject onto which all the errors will be forwarded */
    private final PublishProcessor<Throwable> errorStream = PublishProcessor.create();

    /**
     * Private constructor to avoid instantiation. Use the factory method {@link #create()}.
     */
    private ErrorDeflector() {
        /* Use the factory method */
    }

    /**
     * Factory method to create a new error deflector. Each error deflector contains its own instance of an error
     * stream.
     *
     * @return a new error deflector.
     */
    public static final ErrorDeflector create() {
        return new ErrorDeflector();
    }

    /**
     * Deflects all exceptions appearing in the execution of the runnable onto the error stream.
     *
     * @param runnable the runnable which shall be executed and whose exceptions shall be deflected
     */
    public void deflectExceptions(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            deflectOperationError(runnable, e);
        }
    }

    /**
     * Deflects all exceptions appearing in the supplier onto the error stream. In case an exception occures, then the
     * given default value is returned.
     *
     * @param supplier the supplier which shall be executed and whose errors shall be deflected onto the error strem
     * @param defaultValue the default value to return in case an exception occured
     * @return the value from the supplier or the default one.
     */
    public <T> T deflectExceptions(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Exception e) {
            deflectOperationError(supplier, e);
            return defaultValue;
        }
    }

    public <T> Optional<T> emptyOnException(Supplier<T> callable) {
        try {
            return Optional.of(callable.get());
        } catch (Exception e) {
            deflectOperationError(callable, e);
            return Optional.empty();
        }
    }

    public <S, T> io.reactivex.functions.Function<S, Optional<T>> emptyOnException(Function<S, T> function) {
        return val -> {
            try {
                return Optional.of(function.apply(val));
            } catch (Exception e) {
                deflectOperationIncomingError(function, val, e);
                return Optional.empty();
            }
        };
    }

    /**
     * @deprecated use {@link #emptyOnException(Function)}
     */
    @Deprecated
    public <S, T> io.reactivex.functions.Function<S, Optional<T>> emptyOnError(Function<S, T> function) {
        return emptyOnException(function);
    }

    public <T> io.reactivex.functions.Predicate<T> falseOnException(Predicate<T> predicate) {
        return it -> {
            try {
                return predicate.test(it);
            } catch (Exception e) {
                deflectOperationIncomingError(predicate, it, e);
                return false;
            }
        };
    }

    /**
     * @deprecated use {@link #falseOnException(Predicate)}
     */
    @Deprecated
    public <T> io.reactivex.functions.Predicate<T> falseOnError(Predicate<T> predicate) {
        return falseOnException(predicate);
    }

    public void publishException(Throwable exception) {
        errorStream.onNext(exception);
    }

    private <T> void deflectOperationIncomingError(Object operation, T incoming, Exception e) {
        ErrorStreamException exception = new ErrorStreamException(
                "Error in operation " + operation + ". Incoming value: " + incoming, e);
        errorStream.onNext(exception);
    }

    private void deflectOperationError(Object operation, Exception e) {
        ErrorStreamException exception = new ErrorStreamException("Error in operation " + operation + ".", e);
        errorStream.onNext(exception);
    }

    public <T> ErrorStreamPair<T> stream(Publisher<T> dataPublisher) {
        return ErrorStreamPair.ofDataError(dataPublisher,
                errorStream.toSerialized().onBackpressureBuffer(10,
                        () -> LOGGER.error("Discarding exception due to backpressure buffer limit"),
                        BackpressureOverflowStrategy.DROP_OLDEST));
    }

    public <T> ErrorStreamPair<T> streamNonEmpty(Publisher<Optional<T>> optionalPublisher) {
        return stream(Flowable.fromPublisher(optionalPublisher).filter(Optional::isPresent).map(Optional::get));
    }

}
