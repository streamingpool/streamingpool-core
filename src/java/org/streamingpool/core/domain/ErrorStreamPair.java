/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.domain;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

public abstract class ErrorStreamPair<T> {

    private static final EmptyStream<?> EMPTY_STREAM = new EmptyStream<>();

    public abstract Publisher<T> data();

    public abstract Publisher<Throwable> error();

    public abstract boolean isPresent();

    @SuppressWarnings("unchecked")
    public static final <T> ErrorStreamPair<T> empty() {
        return (ErrorStreamPair<T>) EMPTY_STREAM;
    }

    public static final <T> ErrorStreamPair<T> ofDataError(Publisher<T> dataStream, Publisher<Throwable> errorStream) {
        return new NonEmptyStream<>(dataStream, errorStream);
    }

    public static final <T> ErrorStreamPair<T> ofData(Publisher<T> dataStream) {
        return ofDataError(dataStream, Flowable.never());
    }

    private static class NonEmptyStream<T> extends ErrorStreamPair<T> {

        private final Publisher<T> data;
        private final Publisher<Throwable> error;

        public NonEmptyStream(Publisher<T> data, Publisher<Throwable> error) {
            this.data = requireNonNull(data, "dataStream must not be null");
            this.error = requireNonNull(error, "errorStream must not be null");
        }

        @Override
        public Publisher<T> data() {
            return this.data;
        }

        @Override
        public Publisher<Throwable> error() {
            return this.error;
        }

        @Override
        public boolean isPresent() {
            return true;
        }

    }

    private static class EmptyStream<T> extends ErrorStreamPair<T> {

        @Override
        public Publisher<T> data() {
            throw new UnsupportedOperationException("Empty Stream! This call is not allowed.");
        }

        @Override
        public Publisher<Throwable> error() {
            throw new UnsupportedOperationException("Empty Stream! This call is not allowed.");
        }

        @Override
        public boolean isPresent() {
            return false;
        }

    }

}
