// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package cern.streaming.pool.core.support;

import static akka.stream.javadsl.AsPublisher.WITH_FANOUT;

import org.reactivestreams.Publisher;

import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import cern.streaming.pool.core.service.ProvidingService;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.akka.AkkaSourceProvidingService;

/**
 * Support interface for working with Akka streams. It is preferable to use {@link AbstractAkkaStreamSupport} because it
 * provides automatic discovery of {@link ActorMaterializer} and {@link AkkaSourceProvidingService}.
 * 
 * @see AbstractAkkaStreamSupport
 * @author acalia
 */
public interface AkkaStreamSupport extends StreamSupport {

    Materializer materializer();

    AkkaSourceProvidingService sourceProvidingService();

    default <Out, Mat> Publisher<Out> streamFrom(Source<Out, Mat> akkaSource) {
        return publisherFrom(akkaSource);
    }

    default <Out, Mat> OngoingAkkaSourceProviding<Out, Mat> provide(Source<Out, Mat> akkaSource) {
        return new OngoingAkkaSourceProviding<>(sourceProvidingService(), providingService(), akkaSource,
                materializer());
    }

    default <T, U> Publisher<T> publisherFrom(Source<T, U> source) {
        Sink<T, Publisher<T>> akkaSink = defaultPublisherSink();
        return source.runWith(akkaSink, materializer());
    }

    default <T> Source<T, NotUsed> sourceFrom(StreamId<T> id) {
        return Source.fromPublisher(discover(id));
    }

    static <T> Sink<T, Publisher<T>> defaultPublisherSink() {
        return Sink.<T> asPublisher(WITH_FANOUT).withAttributes(Attributes.inputBuffer(1, 1));
    }

    class OngoingAkkaSourceProviding<Out, Mat> {
        private final Source<Out, Mat> akkaSource;
        private final AkkaSourceProvidingService sourceProvidingService;
        private final ProvidingService providingService;
        private final Materializer materializer;

        public OngoingAkkaSourceProviding(AkkaSourceProvidingService sourceProvidingService,
                ProvidingService providingService, Source<Out, Mat> akkaSource, Materializer materializer) {
            this.sourceProvidingService = sourceProvidingService;
            this.providingService = providingService;
            this.akkaSource = akkaSource;
            this.materializer = materializer;
        }

        public OngoingUnmaterializedAkkaSourceProviding<Out> unmaterialized() {
            return new OngoingUnmaterializedAkkaSourceProviding<>(sourceProvidingService, akkaSource);
        }

        public OngoingMaterializedAkkaSourceProviding<Out, Mat> materialized() {
            return new OngoingMaterializedAkkaSourceProviding<>(providingService, akkaSource, materializer);
        }

        public void as(StreamId<Out> id) {
            unmaterialized().as(id);
        }
    }

    class OngoingUnmaterializedAkkaSourceProviding<Out> {
        private final Source<Out, ?> akkaSource;
        private final AkkaSourceProvidingService sourceProvidingService;

        public OngoingUnmaterializedAkkaSourceProviding(AkkaSourceProvidingService sourceProvidingService,
                Source<Out, ?> akkaSource) {
            this.sourceProvidingService = sourceProvidingService;
            this.akkaSource = akkaSource;
        }

        public void as(StreamId<Out> id) {
            sourceProvidingService.provide(id, akkaSource);
        }
    }

    class OngoingMaterializedAkkaSourceProviding<Out, Mat> {

        private final ProvidingService providingService;
        private final Source<Out, Mat> akkaSource;
        private final Materializer materializer;

        public OngoingMaterializedAkkaSourceProviding(ProvidingService providingService, Source<Out, Mat> akkaSource,
                Materializer materializer) {
            super();
            this.providingService = providingService;
            this.akkaSource = akkaSource;
            this.materializer = materializer;
        }

        public Mat as(StreamId<Out> id) {
            RunnableGraph<Pair<Mat, Publisher<Out>>> graph = akkaSource.toMat(defaultPublisherSink(), Keep.both());
            Pair<Mat, Publisher<Out>> materializedPair = graph.run(materializer);
            providingService.provide(id, materializedPair.second());
            return materializedPair.first();
        }

    }

}
