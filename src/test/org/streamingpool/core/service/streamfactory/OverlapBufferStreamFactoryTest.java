// @formatter:off
/*
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
package org.streamingpool.core.service.streamfactory;

import static io.reactivex.Flowable.fromPublisher;
import static java.util.Collections.singleton;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.when;
import static org.streamingpool.core.service.streamid.BufferSpecification.ofStartEnd;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.BufferSpecification;
import org.streamingpool.core.service.streamid.BufferSpecification.EndStreamMatcher;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

import io.reactivex.Flowable;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;

/**
 * Unit tests for {@link OverlapBufferStreamFactory}.
 */
@SuppressWarnings("unchecked")
public class OverlapBufferStreamFactoryTest extends AbstractStreamTest implements RxStreamSupport {

    @Autowired
    private OverlapBufferStreamFactory overlapBufferStreamFactory;

    /**
     * This test ensures that the first element of the source stream is not ignored, and that the correct amount of
     * values are buffered.
     */
    @Test
    public void testBufferWithInterval() {
        TestScheduler testScheduler = new TestScheduler();
        Flowable<Long> source = Flowable.interval(0, 2, SECONDS, testScheduler);
        Flowable<Long> start = Flowable.interval(0, 20, SECONDS, testScheduler);
        Flowable<Long> end = start.delay(11, SECONDS, testScheduler);

        DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        StreamId<Long> sourceId = Mockito.mock(StreamId.class);
        StreamId<Long> startStreamId = Mockito.mock(StreamId.class);
        StreamId<Long> endStreamId = Mockito.mock(StreamId.class);
        EndStreamMatcher<?, ?> endStreamMatcher = EndStreamMatcher.endingOnEvery(endStreamId);
        BufferSpecification bufferSpecification = ofStartEnd(startStreamId, singleton(endStreamMatcher));
        OverlapBufferStreamId<?> streamId = OverlapBufferStreamId.of(sourceId, bufferSpecification);

        when(discoveryService.discover(sourceId)).thenReturn(source);
        when(discoveryService.discover(startStreamId)).thenReturn(start);
        when(discoveryService.discover(endStreamId)).thenReturn(end);

        Flowable<Object> data = fromPublisher(overlapBufferStreamFactory.create(streamId, discoveryService).data());
        TestSubscriber<Object> testSubscriber = data.test();

        List<Long> firstExpectedResult = Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L);
        List<Long> secondExpectedResult = Arrays.asList(10L, 11L, 12L, 13L, 14L, 15L);
        List<Long> thirdExpectedResult = Arrays.asList(20L, 21L, 22L, 23L, 24L, 25L);

        testScheduler.advanceTimeBy(11, SECONDS);
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValueAt(0, firstExpectedResult::equals);

        testScheduler.advanceTimeBy(21, SECONDS);
        testSubscriber.assertValueCount(2);
        testSubscriber.assertValueAt(1, secondExpectedResult::equals);

        testScheduler.advanceTimeBy(10, SECONDS);
        testSubscriber.assertValueCount(2);
        testScheduler.advanceTimeBy(11, SECONDS);

        testSubscriber.assertValueCount(3);
        testSubscriber.assertValueAt(2, thirdExpectedResult::equals);
    }
}
