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
package cern.streaming.pool.core.demo.akka;

import java.util.concurrent.CompletionStage;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.function.Function3;
import akka.japi.function.Function4;
import akka.stream.ActorMaterializer;
import akka.stream.ClosedShape;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.SinkShape;
import akka.stream.ThrottleMode;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.Broadcast;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.concurrent.duration.FiniteDuration;

/**
 * Created by mgalilee on 25/05/2016.
 *
 * A demo with 1 fast producer (60 elements / second) and 3 consumers, 2 fast and 1 slow (1 element / second).
 * Its purpose is to demonstrate backpressure regulation, by having the slow consumer drop elements it cant handle, without slowing down the fast consumer.
 * The fast consumers are expected to get all the elements of the producer (1 to 250) whereas the slow consumer will only get some.
 */
abstract class AbstractDifferentRatesDemo implements Runnable {

    @Override
    public void run() {
        /* boilerplate code for the akka stream context */
        ActorSystem system = ActorSystem.create("demo");
        Materializer materializer = ActorMaterializer.create(system);

        /* the source of ints from 1 to 250 */
        Source<Integer, NotUsed> src = Source.range(1, 250);

        /* a throttler to have the source produce elements at the desired rate (60 / sec) */
        Flow<Integer, Integer, NotUsed> throttler = Flow.of(Integer.class)
                .throttle(60, FiniteDuration.apply(1, "second"), 1, ThrottleMode.shaping());

        /* 2 fast consumers */
        Sink<Integer, CompletionStage<Done>> sink1 = asyncSink(1, 1);

        Sink<Integer, CompletionStage<Done>> sink2 = asyncSink(2, 1);

        /* 1 slow, dropping consumers */
        Sink<Integer, CompletionStage<Done>> sink3 = droppySink(asyncSink(3, 1000));

        /* shape: from source through throttler, broadcast to all sinks  */
        Function4<GraphDSL.Builder<CompletionStage<Done>>, SinkShape<Integer>, SinkShape<Integer>, SinkShape<Integer>, ClosedShape> shapeFunction = (builder, s1, s2, s3) -> {
            int outputCount = 3;
            UniformFanOutShape<Integer, Integer> bcast = builder.add(Broadcast.create(outputCount));

            builder.from(builder.add(src))
                    .via(builder.add(throttler))
                    .toFanOut(bcast);
            builder.from(bcast)
                    .to(s1);
            builder.from(bcast)
                    .to(s2);
            builder.from(bcast)
                    .to(s3);

            return ClosedShape.getInstance();
        };

        /* completion occurs when the 3rd sink completes */
        Function3<CompletionStage<Done>, CompletionStage<Done>, CompletionStage<Done>, CompletionStage<Done>> fct3 = (m1, m2, m3) -> m3;

        Graph<ClosedShape, CompletionStage<Done>> graph = GraphDSL.create3(sink1, sink2, sink3, fct3, shapeFunction);

        RunnableGraph.fromGraph(graph)
                .run(materializer)
                .thenRunAsync(system::terminate, system.dispatcher()); /* shutdown akka system on completion */
    }

    abstract protected <T, U> Sink<T, CompletionStage<U>> droppySink(Sink<T, CompletionStage<U>> sink);

    private Sink<Integer, CompletionStage<Done>> asyncSink(int id, long delay) {
        Sink<Integer, CompletionStage<Done>> delayedSink = Sink.foreach(i -> {
            Thread.sleep(delay);
            System.out.println(id + ":\t" + (1000 + i)); /* + 1000 to able to sort output conveniently later on */
        });
        return delayedSink.async();
    }
}
