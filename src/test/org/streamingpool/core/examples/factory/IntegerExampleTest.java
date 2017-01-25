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

package org.streamingpool.core.examples.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.streamingpool.core.conf.EmbeddedPoolConfiguration;
import org.streamingpool.core.service.DiscoveryService;

import io.reactivex.subscribers.TestSubscriber;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class, IntegerStreamFactory.class })
public class IntegerExampleTest {

    @Autowired
    private DiscoveryService discovery;
    
    @Test
    public void test() throws InterruptedException {
        IntegerRangeId streamId = new IntegerRangeId(0, 10);
        
        TestSubscriber<Integer> subscriber = TestSubscriber.create();        
        
        discovery.discover(streamId).subscribe(subscriber);
        
        subscriber.await();
        
        List<Integer> values = subscriber.values();
        List<Integer> expectedValues = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        
        assertThat(values).hasSize(10).containsExactlyElementsOf(expectedValues);
    }

}
