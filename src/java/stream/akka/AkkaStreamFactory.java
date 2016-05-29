/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.akka;

import static akka.stream.javadsl.AsPublisher.WITH_FANOUT;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.reactivestreams.Publisher;

import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamFactory;
import stream.StreamId;

public class AkkaStreamFactory implements AkkaSourceProvidingService, StreamFactory {

    private final Materializer materializer;
    private final ConcurrentMap<StreamId<?>, Source<?, ?>> suppliers = new ConcurrentHashMap<>();

    public AkkaStreamFactory(Materializer materializer) {
        this.materializer = Objects.requireNonNull(materializer, "materializer must not be null.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ReactStream<T> create(StreamId<T> newId) {
        Source<T, ?> source = (Source<T, ?>) suppliers.get(newId);
        if (source == null) {
            return null;
        }
        Sink<T, Publisher<T>> akkaSink = Sink.asPublisher(WITH_FANOUT);
        return ReactStreams.fromPublisher(source.runWith(akkaSink, materializer));
    }

    @Override
    public <T> void provide(StreamId<T> id, Source<T, ?> akkaSource) {
        requireNonNull(id, "id must not be null!");
        requireNonNull(akkaSource, "akkaSource must not be null!");

        Source<?, ?> existingSource = suppliers.putIfAbsent(id, akkaSource);
        if (existingSource != null) {
            throw new IllegalArgumentException("Id " + id + " already registered! Cannot register twice.");
        }
    }

}
