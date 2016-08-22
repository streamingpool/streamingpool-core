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

        ConnectableObservable<U> startStream = rxFrom(discoveryService.discover(startId)).publish();
        ConnectableObservable<U> endStream = rxFrom(discoveryService.discover(endId)).publish();
        ConnectableObservable<T> sourceStream = rxFrom(discoveryService.discover(sourceId)).publish();

        Observable<List<T>> bufferStream = sourceStream.buffer(startStream,
                opening -> closingStreamFor(opening, endStream, timeout));

        sourceStream.connect();
        endStream.connect();
        startStream.connect();

        return fromRx(bufferStream);
    }

    @Override
    public boolean canCreate(StreamId id) {
        return id instanceof OverlapBufferStreamId;
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
