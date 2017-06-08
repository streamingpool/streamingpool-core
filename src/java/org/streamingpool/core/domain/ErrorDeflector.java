/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.domain;

import static io.reactivex.BackpressureStrategy.DROP;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

public final class ErrorDeflector {

    private final PublishSubject<Throwable> errorStream = PublishSubject.create();

    public static final ErrorDeflector create() {
        return new ErrorDeflector();
    }

    public <S, T> io.reactivex.functions.Function<S, Optional<T>> emptyOnError(Function<S, T> function) {
        return val -> {
            try {
                return Optional.of(function.apply(val));
            } catch (Exception e) {
                deflectOperationIncomingError(function, val, e);
                return Optional.empty();
            }
        };
    }

    public <T> io.reactivex.functions.Predicate<T> falseOnError(Predicate<T> predicate) {
        return it -> {
            try {
                return predicate.test(it);
            } catch (Exception e) {
                deflectOperationIncomingError(predicate, it, e);
                return false;
            }
        };
    }

    private <T> void deflectOperationIncomingError(Object operation, T incoming, Exception e) {
        ErrorStreamException exception = new ErrorStreamException(
                "Error in operation " + operation + ". Incoming value: " + incoming, e);
        errorStream.onNext(exception);
    }

    public <T> ErrorStreamPair<T> stream(Publisher<T> dataPublisher) {
        return ErrorStreamPair.ofDataError(dataPublisher, errorStream.toFlowable(DROP));
    }

    public <T> ErrorStreamPair<T> streamNonEmpty(Publisher<Optional<T>> optionalPublisher) {
        return stream(Flowable.fromPublisher(optionalPublisher).filter(Optional::isPresent).map(Optional::get));
    }

}
