/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static rx.Observable.merge;
import static rx.Observable.never;
import static rx.Observable.timer;

import java.time.Duration;
import java.util.Optional;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.BufferSpecification;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
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
        StreamId<?> endId = bufferSpecification.endId();
        StreamId<?> sourceId = analysisId.sourceId();

        Duration timeout = bufferSpecification.timeout();

        ConnectableObservable<?> startStream = rxFrom(discoveryService.discover(startId)).publish();
        ConnectableObservable<?> endStream = rxFrom(discoveryService.discover(endId)).publish();
        ConnectableObservable<?> sourceStream = rxFrom(discoveryService.discover(sourceId)).publish();

        Observable<?> bufferStream = sourceStream.buffer(startStream,
                opening -> closingStreamFor(opening, endStream, timeout));

        sourceStream.connect();
        endStream.connect();
        startStream.connect();

        @SuppressWarnings("unchecked")
        ReactiveStream<T> resultingStream = (ReactiveStream<T>) fromRx(bufferStream);
        return Optional.of(resultingStream);
    }

    private Observable<?> closingStreamFor(Object opening, Observable<?> endStream, Duration timeout) {
        Observable<?> matchingEndStream = endStream.filter(opening::equals);
        Observable<?> timeoutStream = timeoutStreamOf(timeout);

        return merge(matchingEndStream, timeoutStream).take(1);
    }

    private Observable<?> timeoutStreamOf(Duration timeout) {
        if (timeout.isNegative()) {
            return never();
        }
        return timer(timeout.toMillis(), MILLISECONDS);
    }
}
