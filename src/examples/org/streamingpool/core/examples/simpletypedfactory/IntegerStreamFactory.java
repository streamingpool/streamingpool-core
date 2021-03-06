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

package org.streamingpool.core.examples.simpletypedfactory;

import static io.reactivex.Flowable.range;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.TypedStreamFactory;

/**
 * The factory is in charge of creating the actual stream that the user receives
 * in case the specified {@link StreamId} is not found on the pool. Since we
 * know that the {@link StreamId} is an {@link IntegerRangeId} that produces
 * {@link Integer}s, we can use the {@link TypedStreamFactory}.
 *
 */
public class IntegerStreamFactory implements TypedStreamFactory<Integer, IntegerRangeId> {

	@Override
	public Publisher<Integer> createReactiveStream(IntegerRangeId id, DiscoveryService discoveryService) {
		int from = id.getFrom();
		int to = id.getTo();
		return range(from, to - from);
	}

	@Override
	public Class<IntegerRangeId> streamIdClass() {
		return IntegerRangeId.class;
	}
}
