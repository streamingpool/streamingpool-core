package org.streamingpool.core.service.streamfactory;

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.streamingpool.core.domain.StreamDependencyTree;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.InstrumentationService;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.diagnostic.ErrorStreamId;
import org.streamingpool.core.service.streamid.MergedErrorStreamId;

import java.util.Set;
import java.util.stream.Collectors;

public class MergedErrorStreamFactory implements StreamFactory {

    private final InstrumentationService instrumentationService;

    public MergedErrorStreamFactory(InstrumentationService instrumentationService) {
        this.instrumentationService = instrumentationService;
    }

    @Override
    public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
        if (!(id instanceof MergedErrorStreamId)) {
            return ErrorStreamPair.empty();
        }

        MergedErrorStreamId streamId = (MergedErrorStreamId) id;

        StreamDependencyTree dependencies = instrumentationService.dependencyTree();
        Set<Publisher<Throwable>> dependenciesOfSource = dependencies.getAncestorsFrom(streamId.getSourceStreamId())
                .stream()
                .map(ErrorStreamId::of)
                .map(discoveryService::discover)
                .collect(Collectors.toSet());


        Flowable<Throwable> merge = Flowable.merge(dependenciesOfSource);

        return (ErrorStreamPair<T>) ErrorStreamPair.ofData(merge);
    }

}
