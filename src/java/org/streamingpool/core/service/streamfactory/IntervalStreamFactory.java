/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.service.streamfactory;

import org.reactivestreams.Publisher;
import org.streamingpool.core.domain.ErrorDeflector;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.IntervalStreamId;

import io.reactivex.Flowable;

public class IntervalStreamFactory implements StreamFactory {

    @SuppressWarnings("unchecked")
    @Override
    public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {

        if (!(id instanceof IntervalStreamId)) {
            return ErrorStreamPair.empty();
        }

        IntervalStreamId typedId = (IntervalStreamId) id;
        ErrorDeflector ed = ErrorDeflector.create();

        Publisher<Long> dataPublisher = Flowable.interval(typedId.getPeriod(), typedId.getPeriodTimeUnit())
                .delay(typedId.getInitialDelay(), typedId.getInitialDelayTimeUnit());
        return ed.stream((Publisher<T>) dataPublisher);
    }

}
