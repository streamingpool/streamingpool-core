package cern.streaming.pool.core.service.streamfactory;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.CompositionStreamId;

import java.util.*;

/**
 * {@link StreamFactory} which provides a flexible way to create {@link ReactiveStream}s based on composition of
 * streams.
 *
 * @author timartin
 */
public final class CompositionStreamFactory implements StreamFactory {
    @Override
    public <T> Optional<ReactiveStream<T>> create(StreamId<T> id, DiscoveryService discoveryService) {
        Objects.requireNonNull(discoveryService, "discoveryService");
        if (!(id instanceof CompositionStreamId)) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        CompositionStreamId<?, T> compositionStreamId = (CompositionStreamId<?, T>) id;
        return Optional.of(createStream(compositionStreamId, discoveryService));
    }

    private <X, T> ReactiveStream<T> createStream(CompositionStreamId<X, T> id, DiscoveryService discoveryService) {
        List<ReactiveStream<X>> extractedStreams = extractStreams(id.getSourceStreamIds(), discoveryService);
        return id.getTransformation().apply(extractedStreams);
    }

    private <X> List<ReactiveStream<X>> extractStreams(Collection<StreamId<X>> streamIds,
                                                       DiscoveryService discoveryService) {
        List<ReactiveStream<X>> sourceReactiveStreams = new ArrayList<>();
        for (StreamId<X> streamId : streamIds) {
            sourceReactiveStreams.add(discoveryService.discover(streamId));
        }
        return sourceReactiveStreams;
    }
}
