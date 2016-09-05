/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.DerivedStreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import rx.Observable;

public class DerivedStreamFactory implements StreamFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DerivedStreamFactory.class);

    @Override
    public <T> Optional<ReactiveStream<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof DerivedStreamId)) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        DerivedStreamId<?, T> derivedStreamId = (DerivedStreamId<?, T>) id;
        return Optional.of(createDerivedStream(derivedStreamId, discoveryService));
    }

    private <S, T> ReactiveStream<T> createDerivedStream(DerivedStreamId<S, T> id, DiscoveryService discoveryService) {
        Observable<S> sourceStream = rxFrom(discoveryService.discover(id.sourceStreamId()));
        Observable<T> derivedStream = sourceStream.map(val -> {
            try {
                return Optional.<T> of(id.conversion().apply(val));
            } catch (Exception e) {
                LOGGER.error("Error while converting '" + val + "' by derived stream id '" + id + "'.", e);
                return Optional.<T> empty();
            }
        }).filter(Optional::isPresent).map(Optional::get);
        return ReactiveStreams.fromRx(derivedStream);
    }
}
