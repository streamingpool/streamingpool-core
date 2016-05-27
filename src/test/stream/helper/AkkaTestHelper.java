/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.helper;

import static akka.stream.javadsl.AsPublisher.WITHOUT_FANOUT;

import org.reactivestreams.Publisher;

import akka.NotUsed;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamId;
import stream.helper.AbstractStreamTest.OngoingProviding;

public interface AkkaTestHelper extends StreamTestHelper {

    ActorMaterializer materializer();
    
    default <Out, Mat> ReactStream<Out> streamFrom(Source<Out, Mat> akkaSource) {
        return ReactStreams.fromPublisher(publisherFrom(akkaSource));
    }

    default <Out, Mat> OngoingProviding<Out> provide(Source<Out, Mat> akkaSource) {
        return provide(streamFrom(akkaSource));
    }

    default <T, U> Publisher<T> publisherFrom(Source<T, U> source) {
        Sink<T, Publisher<T>> akkaSink = Sink.asPublisher(WITHOUT_FANOUT);
        return source.runWith(akkaSink, materializer());
    }
    
    default <T> Source<T, NotUsed> sourceFrom(StreamId<T> id) {
        return ReactStreams.sourceFrom(discover(id));
    }
}
