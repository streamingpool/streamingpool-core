/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static io.reactivex.Flowable.never;
import static io.reactivex.Flowable.timer;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.BufferSpecification;
import cern.streaming.pool.core.service.streamid.BufferSpecification.EndStreamMatcher;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
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
