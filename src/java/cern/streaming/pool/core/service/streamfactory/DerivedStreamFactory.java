/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import java.util.Optional;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.DerivedStreamId;
import io.reactivex.Flowable;

public class DerivedStreamFactory implements StreamFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DerivedStreamFactory.class);

    @Override
    public <T> Optional<Publisher<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof DerivedStreamId)) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        DerivedStreamId<?, T> derivedStreamId = (DerivedStreamId<?, T>) id;
        return Optional.of(createDerivedStream(derivedStreamId, discoveryService));
    }

    private <S, T> Flowable<T> createDerivedStream(DerivedStreamId<S, T> id, DiscoveryService discoveryService) {
        Flowable<S> sourceStream = Flowable.fromPublisher(discoveryService.discover(id.sourceStreamId()));
        return sourceStream.map(val -> {
            try {
                return Optional.<T> of(id.conversion().apply(val));
            } catch (Exception e) {
                LOGGER.error("Error while converting '" + val + "' by derived stream id '" + id + "'.", e);
                return Optional.<T> empty();
            }
        }).filter(Optional::isPresent).map(Optional::get);
    }
}
