/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.domain;

import static java.util.Objects.requireNonNull;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

public abstract class Stream<T> {

    private static final NotCreatedStream<?> NOT_CREATED_STREAM = new NotCreatedStream<>();

    public abstract Publisher<T> data();

    public abstract Publisher<Throwable> error();

    public abstract boolean wasCreated();

    @SuppressWarnings("unchecked")
    public static final <T> Stream<T> notCreated() {
        return (Stream<T>) NOT_CREATED_STREAM;
    }

    public static final <T> Stream<T> ofDataError(Publisher<T> dataStream, Publisher<Throwable> errorStream) {
        return new CreatedStream<>(dataStream, errorStream);
    }

    public static final <T> Stream<T> ofData(Publisher<T> dataStream) {
        return ofDataError(dataStream, Flowable.never());
    }

    private static class CreatedStream<T> extends Stream<T> {

        private final Publisher<T> data;
        private final Publisher<Throwable> error;

        public CreatedStream(Publisher<T> data, Publisher<Throwable> error) {
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
        public boolean wasCreated() {
            return true;
        }

    }

    private static class NotCreatedStream<T> extends Stream<T> {

        @Override
        public Publisher<T> data() {
            throw new UnsupportedOperationException("Stream was not created! This call is not allowed.");
        }

        @Override
        public Publisher<Throwable> error() {
            throw new UnsupportedOperationException("Stream was not created! This call is not allowed.");
        }

        @Override
        public boolean wasCreated() {
            return false;
        }

    }

}
