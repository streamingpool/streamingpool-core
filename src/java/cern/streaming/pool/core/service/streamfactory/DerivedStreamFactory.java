/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.DerivedStreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;

public class DerivedStreamFactory <T> implements StreamFactory<T, DerivedStreamId<?, T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DerivedStreamFactory.class);

    @Override
    public ReactiveStream<T> create(DerivedStreamId<?, T> id, DiscoveryService discoveryService) {
        return createDerivedStream(id, discoveryService);
    }

    private <S> ReactiveStream<T> createDerivedStream(DerivedStreamId<S, T> id, DiscoveryService discoveryService) {
        ReactiveStream<S> sourceStream = discoveryService.discover(id.sourceStreamId());
        Observable<T> derivedStream = rxFrom(sourceStream).map((val) -> {
            try {
                return id.conversion().apply(val);
            } catch (Exception e) {
                LOGGER.error("Error while converting '" + val + "' by derived stream id '" + id + "'.", e);
                return null;
            }
        }).filter(v -> (v != null));
        return ReactiveStreams.fromRx(derivedStream);
    }

}
