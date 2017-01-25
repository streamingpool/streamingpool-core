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
package org.streamingpool.core.demo.akka;

import java.util.concurrent.CompletionStage;

import akka.japi.function.Function2;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;

/**
 * Created by mgalilee on 19/05/2016.
 *
 * Demo of different consumer rates, with the slower one using a conflating mechanism not to backpressure the producer.
 * When run, we can see the 3rd consumer gets only a few elements of the stream, and always the latest.
 * The conflation is also printed, stating which elements are merged.
 */
public class DifferentRatesDemo2 extends AbstractDifferentRatesDemo {

    public static void main(String[] args) {
        new DifferentRatesDemo2().run();
    }

    @Override
    protected <T, U> Sink<T, CompletionStage<U>> droppySink(Sink<T, CompletionStage<U>> sink) {
        Function2<T, T, T> function2 = (older, newer) -> {
            System.out.println("conflating " + older + " and " + newer + " to " + newer);
            return newer;
        };
        return Flow.<T>create()
                .conflate(function2)
                .toMat(sink, Keep.right());
    }
}
