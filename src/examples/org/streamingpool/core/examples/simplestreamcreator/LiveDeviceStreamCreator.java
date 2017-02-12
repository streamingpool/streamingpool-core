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

import static java.util.concurrent.TimeUnit.SECONDS;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamCreator;
import org.streamingpool.core.service.StreamFactory;

import io.reactivex.Flowable;

/**
 * This {@link StreamCreator} is a specialization of a {@link StreamFactory}
 * that knows and is able to create one type of stream. In this case, we
 * simulate a reading from a device using a
 * {@link Flowable#interval(long, java.util.concurrent.TimeUnit)} and then we
 * transform it to a specific domain object.
 *
 */
public class LiveDeviceStreamCreator implements StreamCreator<LiveDeviceReading> {

	@Override
	public Publisher<LiveDeviceReading> createWith(DiscoveryService discoveryService) {
		return Flowable.interval(1, SECONDS).map(LiveDeviceReading::new);
	}

}
