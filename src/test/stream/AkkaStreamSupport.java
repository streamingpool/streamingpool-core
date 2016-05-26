/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import static akka.stream.javadsl.AsPublisher.WITHOUT_FANOUT;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import conf.AkkaConfiguration;

@ContextConfiguration(classes = AkkaConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class AkkaStreamSupport extends StreamProcessingSupport {

    @Autowired
    private ActorMaterializer materializer;

    protected Materializer materializer() {
        return materializer;
    }

    protected <Out, Mat> ReactStream<Out> streamFrom(Source<Out, Mat> akkaSource) {
        return ReactStreams.fromPublisher(publisherFrom(akkaSource));
    }

    protected <Out, Mat> OngoingProviding<Out> provide(Source<Out, Mat> akkaSource) {
        return provide(streamFrom(akkaSource));
    }

    private <T, U> Publisher<T> publisherFrom(Source<T, U> source) {
        Sink<T, Publisher<T>> akkaSink = Sink.asPublisher(WITHOUT_FANOUT);
        return source.runWith(akkaSink, materializer);
    }

}
