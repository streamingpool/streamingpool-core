package cern.streaming.pool.core.service.streamfactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.reactivestreams.Publisher;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.CompositionStreamId;

/**
 * EXPERIMENTAL
 * {@link StreamFactory} which provides a flexible way to create {@link ReactiveStream}s based on composition of
 * streams.
 *
 * @author timartin
 */
public final class CompositionStreamFactory implements StreamFactory {
    @Override
    public <T> Optional<Publisher<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        Objects.requireNonNull(discoveryService, "discoveryService");
        if (!(id instanceof CompositionStreamId)) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        CompositionStreamId<?, T> compositionStreamId = (CompositionStreamId<?, T>) id;
        return Optional.of(createStream(compositionStreamId, discoveryService));
    }

    private <X, T> Publisher<T> createStream(CompositionStreamId<X, T> id, DiscoveryService discoveryService) {
        List<Publisher<X>> extractedStreams = extractStreams(id.sourceStreamIds(), discoveryService);
        return id.transformation().apply(extractedStreams);
    }

    private <X> List<Publisher<X>> extractStreams(Collection<StreamId<X>> streamIds,
                                                       DiscoveryService discoveryService) {
        List<Publisher<X>> sourceReactiveStreams = new ArrayList<>();
        for (StreamId<X> streamId : streamIds) {
            sourceReactiveStreams.add(discoveryService.discover(streamId));
        }
        return sourceReactiveStreams;
    }
}
