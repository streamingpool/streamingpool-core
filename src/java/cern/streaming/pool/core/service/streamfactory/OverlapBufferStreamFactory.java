/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static rx.Observable.never;
import static rx.Observable.timer;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.BufferSpecification;
import cern.streaming.pool.core.service.streamid.BufferSpecification.EndStreamMatcher;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;
import rx.observables.ConnectableObservable;

/**
 * Factory for {@link OverlapBufferStreamId}
 * 
 * @see OverlapBufferStreamId
 * @author acalia
 */
public class OverlapBufferStreamFactory implements StreamFactory {

    @Override
    public <T> Optional<ReactiveStream<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof OverlapBufferStreamId)) {
            return Optional.empty();
        }

        OverlapBufferStreamId<?> analysisId = (OverlapBufferStreamId<?>) id;

        BufferSpecification bufferSpecification = analysisId.bufferSpecification();

        StreamId<?> startId = bufferSpecification.startId();
        StreamId<?> sourceId = analysisId.sourceId();

        Duration timeout = bufferSpecification.timeout();

        ConnectableObservable<?> startStream = rxFrom(discoveryService.discover(startId)).publish();
        ConnectableObservable<?> sourceStream = rxFrom(discoveryService.discover(sourceId)).publish();

        Set<EndStreamMatcher<?, ?>> matchers = bufferSpecification.endStreamMatchers();
        @SuppressWarnings("unchecked")
        Map<EndStreamMatcher<Object, Object>, ConnectableObservable<?>> endStreams = matchers.stream()
                .collect(Collectors.toMap(m -> (EndStreamMatcher<Object, Object>) m,
                        m -> rxFrom(discoveryService.discover(m.endStreamId())).publish()));

        Observable<?> bufferStream = sourceStream.buffer(startStream,
                opening -> closingStreamFor(opening, endStreams, timeout));

        sourceStream.connect();
        for (ConnectableObservable<?> stream : endStreams.values()) {
            stream.connect();
        }
        startStream.connect();

        @SuppressWarnings("unchecked")
        ReactiveStream<T> resultingStream = (ReactiveStream<T>) ReactiveStreams.fromRx(bufferStream);
        return Optional.of(resultingStream);
    }

    private Observable<?> closingStreamFor(Object opening,
            Map<EndStreamMatcher<Object, Object>, ConnectableObservable<?>> endStreams, Duration timeout) {
        Observable<?> timeoutStream = timeoutStreamOf(timeout);

        Set<Observable<?>> matchingEndStreams = endStreams.entrySet().stream()
                .map(e -> e.getValue().filter(v -> e.getKey().matching().test(opening, v))).collect(Collectors.toSet());

        matchingEndStreams.add(timeoutStream);

        return Observable.merge(matchingEndStreams).take(1);
    }

    private Observable<?> timeoutStreamOf(Duration timeout) {
        if (timeout.isNegative()) {
            return never();
        }
        return timer(timeout.toMillis(), MILLISECONDS);
    }
}
