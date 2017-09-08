package org.streamingpool.core.service.streamfactory;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import org.reactivestreams.Publisher;
import org.streamingpool.core.domain.ErrorDeflector;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.ZippedStreamId;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import io.reactivex.functions.Function;

/**
 * Factory for {@link ZippedStreamId}
 *
 * @see ZippedStreamId
 * @author jado
 */
public class ZippedStreamFactory implements StreamFactory {

    @Override
    public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        Objects.requireNonNull(discoveryService, "discoveryService");
        if (!(id instanceof ZippedStreamId)) {
            return ErrorStreamPair.empty();
        }
        @SuppressWarnings("unchecked")
        ZippedStreamId<?, ?, T> zippedId = (ZippedStreamId<?, ?, T>) id;

        return createStream(zippedId, discoveryService);
    }

    private <S1, S2, T> ErrorStreamPair<T> createStream(ZippedStreamId<S1, S2,  T> id, DiscoveryService discoveryService) {

        Publisher<S1> publisher1 = discoveryService.discover(id.sourceStreamId1());
        Publisher<S2> publisher2 = discoveryService.discover(id.sourceStreamId2());
        BiFunction<S1, S2, Optional<T>> function = id.function();

        ErrorDeflector ed = ErrorDeflector.create();
        return ed.streamNonEmpty(Flowable.zip(publisher1, publisher2, function));

    }


}
