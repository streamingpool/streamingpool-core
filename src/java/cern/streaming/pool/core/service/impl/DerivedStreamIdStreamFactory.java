/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.streaming.pool.core.service.DerivedStreamId;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;

public class DerivedStreamIdStreamFactory implements StreamFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DerivedStreamIdStreamFactory.class);

    @Override
    public <T> ReactiveStream<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof DerivedStreamId)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        DerivedStreamId<?, T> derivedStreamId = (DerivedStreamId<?, T>) id;
        return createDerivedStream(derivedStreamId, discoveryService);
    }

    private <S, T> ReactiveStream<T> createDerivedStream(DerivedStreamId<S, T> id, DiscoveryService discoveryService) {
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
