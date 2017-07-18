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

package org.streamingpool.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;
import org.streamingpool.core.incubation.akka.IdBasedSource;
import org.streamingpool.core.service.impl.LocalPool;

import com.google.common.collect.Lists;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Attributes;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import io.reactivex.Flowable;

public class AkkaIdSourceTest {

    private static final StreamId<Integer> STREAM_ID = new StreamId<Integer>() {
        @Override
        public String toString() {
            return "testIntegerStreamId";
        }
    };

    private static final LocalPool SIMPLE_POOL = new LocalPool();

    static {
        SIMPLE_POOL.provide(STREAM_ID, Flowable.range(0, 1000));
    }

    private static class TestIdBasedSource extends IdBasedSource<Integer> {

        TestIdBasedSource() {
            super(STREAM_ID);
        }

        @Override
        protected DiscoveryService getDiscoveryService(Attributes attributes) {
            return SIMPLE_POOL;
        }
    }

    @Test
    public void createIdSource() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1000);

        ActorSystem system = ActorSystem.create("AkkaIdSourceTest");
        Materializer materializer = ActorMaterializer.create(system);

        IdBasedSource<Integer> sourceGraph = new TestIdBasedSource();

        List<Integer> results = new ArrayList<>(1000);
        Source.fromGraph(sourceGraph).runForeach(i -> {
            results.add(i);
            latch.countDown();
        }, materializer);

        latch.await();

        List<Integer> expected = Lists.newArrayList(Flowable.range(0, 1000).blockingIterable());

        assertThat(results).hasSize(1000).containsExactlyElementsOf(expected);
    }
}
