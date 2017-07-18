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

import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProviderTest extends AbstractStreamTest implements RxStreamSupport{
    private static final Publisher ANY_REACTIVE_STREAM = Flowable.just(1);
    private static final StreamId ANY_STREAM_ID = mock(StreamId.class);
    
    @Autowired
    ProvidingService providingService;

    @Test
    public void testProvidedStreamCanBeDiscovered() {
        providingService.provide(ANY_STREAM_ID, ANY_REACTIVE_STREAM);

        TestSubscriber test = rxFrom(ANY_STREAM_ID).test();
        test.awaitTerminalEvent();
        test.assertValueCount(1);
        test.assertValue(1);
    }

    @Test
    public void testProvidedUsingHelpersIsDiscovered() {
        provide(ANY_REACTIVE_STREAM).as(ANY_STREAM_ID);
        TestSubscriber test = rxFrom(ANY_STREAM_ID).test();
        test.awaitTerminalEvent();
        test.assertValueCount(1);
        test.assertValue(1);
    }
}
