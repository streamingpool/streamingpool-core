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

package org.streamingpool.core.service.streamfactory;

import org.reactivestreams.Publisher;
import org.streamingpool.core.domain.Stream;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.CombineWithLatestStreamId;

import io.reactivex.Flowable;

/**
 * Factory for {@link CombineWithLatestStreamId}
 * 
 * @see CombineWithLatestStreamId
 * @author acalia
 */
public class CombineWithLatestStreamFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <Y> Stream<Y> create(StreamId<Y> id, DiscoveryService discoveryService) {
        if (!(id instanceof CombineWithLatestStreamId)) {
            return Stream.notCreated();
        }

        return Stream.ofData(combineWithLatestStream((CombineWithLatestStreamId<?, ?, Y>) id, discoveryService));
    }

    private <T, D, Y> Publisher<Y> combineWithLatestStream(CombineWithLatestStreamId<T, D, Y> streamId,
            DiscoveryService discoveryService) {
        Flowable<D> data = Flowable.fromPublisher(discoveryService.discover(streamId.dataStream()));
        Flowable<T> trigger = Flowable.fromPublisher(discoveryService.discover(streamId.triggerStream()));

        return trigger.withLatestFrom(data, streamId.combiner()::apply);
    }

}
