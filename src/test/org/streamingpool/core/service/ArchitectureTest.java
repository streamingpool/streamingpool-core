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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.reactivestreams.Publisher;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;
import org.streamingpool.core.testing.NamedStreamId;

import io.reactivex.Flowable;

public class ArchitectureTest extends AbstractStreamTest implements RxStreamSupport {

    private static final String ANY_NAME = "";
    private static final List<Integer> INTEGER_SOURCE_ITEMS = Arrays.asList(1, 3, 5, 11);

    @Test
    public void testElementsAreSentAndReceived() {
        final StreamId<Integer> id = new NamedStreamId<Integer>(ANY_NAME);

        Publisher<Integer> reactStream = prepareRxStreamWith(INTEGER_SOURCE_ITEMS);
        provide(reactStream).as(id);

        final int result = rxFrom(id).reduce(Math::addExact).blockingGet();
        final int expected = INTEGER_SOURCE_ITEMS.stream().mapToInt(i -> i.intValue()).sum();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testRepublish() {
        final StreamId<Integer> idA = new NamedStreamId<Integer>("idA");
        final StreamId<Integer> idB = new NamedStreamId<Integer>("idB");

        // Original stream
        Publisher<Integer> reactSourceStream = prepareRxStreamWith(INTEGER_SOURCE_ITEMS);
        provide(reactSourceStream).as(idA);

        // Discover + re-provide
        Publisher<Integer> reactStreamB = rxFrom(idA).map(value -> value * 2);
        provide(reactStreamB).as(idB);

        // Discover
        final int result = rxFrom(idB).reduce(Math::addExact).blockingGet();
        final int expected = INTEGER_SOURCE_ITEMS.stream().mapToInt(i -> i.intValue()).sum() * 2;

        assertThat(result).isEqualTo(expected);

    }

    @Test
    public void test() throws InterruptedException {

        // This object is used as a kind of semaphore in order to block the main thread until the stream finishes
        // Rx uses deamon threads inside, so we have to block the main thread because it would kill Rx ones when it
        // finishes
        CountDownLatch counter = new CountDownLatch(1);

        List<Long> results = new ArrayList<>();

        Flowable.interval(100, TimeUnit.MILLISECONDS) // Generate a value every 100 milliseconds
                .filter(value -> value % 2 == 0) // Pass down only the values that are even
                .take(3) // Just accept 3 values
                .doAfterTerminate(counter::countDown) // When the stream ends (or there is an error) tell the counter to
                                                      // decrease
                .subscribe(results::add); // Subscribe to the stream in order to get the values (in this case
                                          // print them)

        // The counter works like this: it is initiated with a value (1 in this case) and this value can be decreased.
        // When it reaches 0, is considered done. In this case, the await() waits for the counter to reach 0 and then
        // unblock
        // the calling thread (the main in this case)
        counter.await();

        assertThat(results).hasSize(3).allMatch(value -> value % 2 == 0);
    }

    private static <T> Flowable<T> prepareRxStreamWith(List<T> items) {
        return Flowable.fromIterable(items);
    }

}
