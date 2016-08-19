/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.List;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.OverlapBufferStreamId;
import rx.Observable;
import rx.observables.ConnectableObservable;

/**
 * Factory for {@link OverlapBufferStreamId}
 * 
 * @see OverlapBufferStreamId
 * @author acalia 
 * @param <T> type of the stream data items
 * @param <U> type of the start and stop streams
 */
public class OverlapBufferStreamFactory <T, U> implements StreamFactory <List<T>, OverlapBufferStreamId<T, U>> {

    @Override
    public ReactiveStream<List<T>> create(OverlapBufferStreamId id, DiscoveryService discoveryService) {

        StreamId<U> startId = id.startId();
        StreamId<U> endId = id.endId();
        StreamId<T> sourceId = id.sourceId();
        
        Duration timeout = id.timeout();

        ConnectableObservable<U> startStream = rxFrom(discoveryService.discover(startId)).share().publish();
        ConnectableObservable<U> endStream = rxFrom(discoveryService.discover(endId)).share().publish();
        ConnectableObservable<T> expressionStream = rxFrom(discoveryService.discover(sourceId)).publish();

        Observable<List<T>> bufferStream = expressionStream.buffer(startStream,
                opening -> closingStreamFor(opening, endStream, timeout));

        expressionStream.connect();
        endStream.connect();
        startStream.connect();

        return fromRx(bufferStream);
    }

    @Override
    public boolean canCreate(StreamId id) {
        return id instanceof OverlapBufferStreamId;
    }

    private Observable<Object> closingStreamFor(Object opening, Observable<U> endStream, Duration timeout) {
        Observable<U> mathingEndStream = endStream.filter(opening::equals);
        Observable<Long> timeoutStream = Observable.timer(timeout.toMillis(), MILLISECONDS);
        return Observable.merge(mathingEndStream, timeoutStream).take(1);
    }
}
