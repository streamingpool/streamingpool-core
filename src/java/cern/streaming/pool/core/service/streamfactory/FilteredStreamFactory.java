/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamfactory;

import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Optional;
import java.util.function.Predicate;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.FilteredStreamId;

/**
 * {@link StreamFactory} for the {@link FilteredStreamId}
 * 
 * @see FilteredStreamId
 * @author acalia
 */
public class FilteredStreamFactory implements StreamFactory {

    @Override
    public <T> Optional<ReactiveStream<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof FilteredStreamId)) {
            return empty();
        }
        FilteredStreamId<T> filteredId = (FilteredStreamId<T>) id;

        StreamId<T> source = filteredId.sourceStreamId();
        Predicate<T> predicate = filteredId.predicate();

        return of(fromRx(rxFrom(discoveryService.discover(source)).filter(predicate::test)));
    }

}
