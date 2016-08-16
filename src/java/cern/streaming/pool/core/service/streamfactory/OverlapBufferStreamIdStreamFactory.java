/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
import rx.Observable;
import rx.observables.ConnectableObservable;

public class OverlapBufferStreamIdStreamFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> ReactiveStream<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof OverlapBufferStreamId)) {
            return null;
        }

        OverlapBufferStreamId<?, ?> analysisId = (OverlapBufferStreamId<?, ?>) id;

        StreamId<?> startId = analysisId.startId();
        StreamId<?> endId = analysisId.endId();
        StreamId<?> sourceId = analysisId.sourceId();
        
        Duration timeout = analysisId.timeout();

        ConnectableObservable<?> startStream = rxFrom(discoveryService.discover(startId)).share().publish();
        ConnectableObservable<?> endStream = rxFrom(discoveryService.discover(endId)).share().publish();
        ConnectableObservable<?> expressionStream = rxFrom(discoveryService.discover(sourceId)).publish();

        Observable<?> bufferStream = expressionStream.buffer(startStream,
                opening -> closingStreamFor(opening, endStream, timeout));

        expressionStream.connect();
        endStream.connect();
        startStream.connect();

        return (ReactiveStream<T>) fromRx(bufferStream);
    }

    private Observable<?> closingStreamFor(Object opening, Observable<?> endStream, Duration timeout) {
        Observable<?> mathingEndStream = endStream.filter(opening::equals);
        Observable<?> timeoutStream = Observable.timer(timeout.toMillis(), MILLISECONDS);

        return Observable.merge(mathingEndStream, timeoutStream).take(1);
    }
}
