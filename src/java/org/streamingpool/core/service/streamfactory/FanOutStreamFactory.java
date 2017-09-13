package org.streamingpool.core.service.streamfactory;

import io.reactivex.Flowable;
import org.streamingpool.core.domain.ErrorDeflector;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.FanOutStreamId;

public class FanOutStreamFactory implements StreamFactory {

    @Override
    public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof FanOutStreamId)) {
            return ErrorStreamPair.empty();
        }

        FanOutStreamId<T> fanOutId = (FanOutStreamId<T>) id;

        ErrorDeflector ed = ErrorDeflector.create();

        Flowable<T> targetStream = Flowable.fromPublisher(discoveryService.discover(fanOutId.target())).share();
        return ed.stream(targetStream);
    }
}
