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

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Optional;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.FilteredStreamId;

import io.reactivex.Flowable;

/**
 * {@link StreamFactory} for the {@link FilteredStreamId}
 * 
 * @see FilteredStreamId
 * @author acalia
 */
public class FilteredStreamFactory implements StreamFactory {

    @Override
    public <T> Optional<Publisher<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof FilteredStreamId)) {
            return empty();
        }
        FilteredStreamId<T> filteredId = (FilteredStreamId<T>) id;

        StreamId<T> source = filteredId.sourceStreamId();
        Predicate<T> predicate = filteredId.predicate();

        return of(Flowable.fromPublisher(discoveryService.discover(source)).filter(predicate::test));
    }

}
