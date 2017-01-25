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

package org.streamingpool.core.service.stream;

import static io.reactivex.Flowable.just;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.streamingpool.core.service.streamid.FilteredStreamId.filterBy;

import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.FilteredStreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

import io.reactivex.subscribers.TestSubscriber;

public class FilteredStreamTest extends AbstractStreamTest implements RxStreamSupport {

    @Test
    public void streamFiltered() throws InterruptedException {
        StreamId<Integer> sourceId = provide(just(1, 2, 3, 4)).withUniqueStreamId();
        FilteredStreamId<Integer> filterId = filterBy(sourceId, value -> value % 2 == 0);
        
        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        discover(filterId).subscribe(subscriber);
        subscriber.await();
        
        assertThat(subscriber.values()).hasSize(2).containsOnly(2, 4);
    }
    
    @Test(expected = NullPointerException.class)
    public void testNullSource() {
        discover(filterBy(null, any -> false));
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testNullPredicate() {
        discover(filterBy(mock(StreamId.class), null));
    }
}
