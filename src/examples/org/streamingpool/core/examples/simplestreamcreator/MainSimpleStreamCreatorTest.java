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

package org.streamingpool.core.examples.simplestreamcreator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.core.examples.simplestreamcreator.LiveDeviceIds.LIVE_DEVICE_ID;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.streamingpool.core.conf.EmbeddedPoolConfiguration;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamCreator;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

/**
 * Sometimes we have specific streams that are unique. For example, you can
 * imagine a device in a control system. We know that we want to access the
 * readings of that particular device. A strategy for this is to create a
 * {@link StreamId} for the device as constant in the project. The stream id is
 * not that important anymore, since it doesn't need to carry information of the
 * stream to be created, because we know that a priori. In such a cases, we can
 * use the {@link StreamCreator} feature. A {@link StreamCreator} is a
 * specialization of a {@link StreamFactory} that knows how to create a
 * particular {@link StreamId}. In this example the {@link StreamId} is a
 * constant {@link LiveDeviceIds#LIVE_DEVICE_ID} and the stream creator is a
 * {@link LiveDeviceStreamCreator} (it can be inlined directly in the
 * configuration in most of the cases since it is a functional interface). In
 * the configuration ({@link LiveDeviceConfiguration}) we glue together the
 * {@link StreamId} and the {@link StreamCreator}.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class, LiveDeviceConfiguration.class })
public class MainSimpleStreamCreatorTest {

	@Autowired
	private DiscoveryService discovery;

	@Test
	public void test() throws InterruptedException {
		// We use the test subscriber of RxJava 2
		TestSubscriber<LiveDeviceReading> subscriber = TestSubscriber.create();

		// Any stream discovered using streaming pool is a Publisher, thus can
		// be used with any reactive stream technology, like RxJava 2
		Flowable.fromPublisher(discovery.discover(LIVE_DEVICE_ID)).take(2).subscribe(subscriber);

		subscriber.await(5, TimeUnit.SECONDS);

		List<LiveDeviceReading> values = subscriber.values();
		List<LiveDeviceReading> expectedValues = Arrays.asList(new LiveDeviceReading(0L), new LiveDeviceReading(1L));

		assertThat(values).containsExactlyElementsOf(expectedValues);
	}

}
