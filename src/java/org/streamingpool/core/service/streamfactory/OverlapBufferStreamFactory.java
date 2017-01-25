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

import static io.reactivex.Flowable.never;
import static io.reactivex.Flowable.timer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.BufferSpecification;
import org.streamingpool.core.service.streamid.OverlapBufferStreamId;
import org.streamingpool.core.service.streamid.BufferSpecification.EndStreamMatcher;

import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;

/**
 * Factory for {@link OverlapBufferStreamId}
 * 
 * @see OverlapBufferStreamId
 * @author acalia
 */
public class OverlapBufferStreamFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Publisher<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof OverlapBufferStreamId)) {
            return Optional.empty();
        }

        OverlapBufferStreamId<?> analysisId = (OverlapBufferStreamId<?>) id;

        BufferSpecification bufferSpecification = analysisId.bufferSpecification();

        StreamId<?> startId = bufferSpecification.startId();
        StreamId<?> sourceId = analysisId.sourceId();

        Duration timeout = bufferSpecification.timeout();

        ConnectableFlowable<?> startStream = Flowable.fromPublisher(discoveryService.discover(startId)).publish();
        ConnectableFlowable<?> sourceStream = Flowable.fromPublisher(discoveryService.discover(sourceId)).publish();

        Set<EndStreamMatcher<?, ?>> matchers = bufferSpecification.endStreamMatchers();
        Map<EndStreamMatcher<Object, Object>, ConnectableFlowable<?>> endStreams = matchers.stream()
                .collect(Collectors.toMap(m -> (EndStreamMatcher<Object, Object>) m,
                        m -> Flowable.fromPublisher(discoveryService.discover(m.endStreamId())).publish()));

        Flowable<?> bufferStream = sourceStream.buffer(startStream,
                opening -> closingStreamFor(opening, endStreams, timeout));

        sourceStream.connect();
        for (ConnectableFlowable<?> stream : endStreams.values()) {
            stream.connect();
        }
        startStream.connect();

        return Optional.of((Publisher<T>) bufferStream);
    }

    private Flowable<?> closingStreamFor(Object opening,
            Map<EndStreamMatcher<Object, Object>, ConnectableFlowable<?>> endStreams, Duration timeout) {
        Flowable<?> timeoutStream = timeoutStreamOf(timeout);

        Set<Flowable<?>> matchingEndStreams = endStreams.entrySet().stream()
                .map(e -> e.getValue().filter(v -> e.getKey().matching().test(opening, v))).collect(Collectors.toSet());

        matchingEndStreams.add(timeoutStream);

        return Flowable.merge(matchingEndStreams).take(1);
    }

    private Flowable<?> timeoutStreamOf(Duration timeout) {
        if (timeout.isNegative()) {
            return never();
        }
        return timer(timeout.toMillis(), MILLISECONDS);
    }
}
