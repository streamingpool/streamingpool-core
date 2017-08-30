package org.streamingpool.core.service.streamfactory;

import io.reactivex.Flowable;
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
        ZippedStreamId<?, T> zippedId = (ZippedStreamId<?, T>) id;

        return createStream(zippedId, discoveryService);
    }

    private <S, T> ErrorStreamPair<T> createStream( ZippedStreamId<S,  T> id, DiscoveryService discoveryService) {

        Iterable<Publisher<S>> publishers = StreamSupport.stream(id.sourceStreamIds().spliterator(), false)
                .map(discoveryService::discover)
                .collect(Collectors.toList());
        final Function< Object[], Optional<T>> function = id.function();

        ErrorDeflector ed = ErrorDeflector.create();
        return ed.streamNonEmpty(Flowable.zip(publishers, function));

    }


}
