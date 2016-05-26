/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package demo;

import java.io.File;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import org.mockito.runners.ConsoleSpammingMockitoJUnitRunner;
import org.reactivestreams.Publisher;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigMemorySize;
import com.typesafe.config.ConfigMergeable;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.ConfigResolveOptions;
import com.typesafe.config.ConfigValue;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.ActorMaterializerSettings;
import akka.stream.FlowShape;
import akka.stream.impl.io.FileSink;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import rx.Observable;
import rx.schedulers.Schedulers;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamId;
import stream.impl.NamedStreamId;
import stream.impl.SimplePool;

/**
 * Simple demo to create an rxJava stream, use it as an akka-stream source, and wire it within an akka materialized
 * flow. The sink (akka) logs on the console.
 * 
 * Weird buffering somewhere
 * 
 * @author acalia
 */
public class RxJavaAkkaInteroperabilityDemo {

    public static void main(String args[]) {

        System.setProperty("akka.stream.materializer.max-input-buffer-size", "16");
        System.setProperty("akka.stream.materializer.initial-input-buffer-size", "1");

        SimplePool simplePool = new SimplePool();

        StreamId<Long> rxJavaStreamId = provideRxStream(simplePool);

        ReactStream<Long> discoveredStream = simplePool.discover(rxJavaStreamId);

        Publisher<Long> publisherFrom = ReactStreams.publisherFrom(discoveredStream);

        Source<Long, NotUsed> rxSource = Source.fromPublisher(publisherFrom);
        Sink<Long, CompletionStage<Done>> consoleSink = Sink.foreach(l -> {
            TimeUnit.SECONDS.sleep(2);
            System.out.println("akka : " + l);
        });

        RunnableGraph<CompletionStage<Done>> mat = rxSource.toMat(consoleSink, Keep.right());

        CompletionStage<Done> run = mat.run(ActorMaterializer.create(ActorSystem.create("sys")));
        while (!run.toCompletableFuture().isDone()) {
            // do nothing
        }

    }

    private static StreamId<Long> provideRxStream(SimplePool simplePool) {
        StreamId<Long> rxJavaStreamId = new NamedStreamId<>("rxJavaStream");
        ReactStream<Long> obs = ReactStreams.fromRx(Observable.interval(500, TimeUnit.MILLISECONDS).limit(100)
                .doOnNext(l -> System.out.println("rx : " + l))
                .onBackpressureDrop(value -> System.out.println("dropped : " + value))
                .doOnRequest(request -> System.out.println("request : " + request))
                .observeOn(Schedulers.newThread(), 1));
        simplePool.provide(rxJavaStreamId, obs);
        return rxJavaStreamId;
    }
}
