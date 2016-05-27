/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import conf.InProcessPoolConfiguration;

/**
 * @author acalia
 */
@ContextConfiguration(classes = InProcessPoolConfiguration.class, loader = AnnotationConfigContextLoader.class)
public abstract class StreamProcessingSupport {

    @Autowired
    protected DiscoveryService discoveryService;
    @Autowired
    protected ProvidingService providingService;

    protected <T> ReactStream<T> discover(final StreamId<T> id) {
        return discoveryService.discover(id);
    }

    protected <T> OngoingProviding<T> provide(ReactStream<T> reactStream) {
        return new OngoingProviding<>(providingService, reactStream);
    }

    protected Publisher<Integer> publisherFrom(StreamId<Integer> id) {
        return ReactStreams.publisherFrom(discover(id));
    }

    public static class OngoingProviding<T> {
        private final ReactStream<T> reactStream;
        private final ProvidingService providingService;

        private OngoingProviding(ProvidingService providingService, ReactStream<T> reactStream) {
            this.providingService = providingService;
            this.reactStream = reactStream;
        }

        public void as(StreamId<T> id) {
            providingService.provide(id, reactStream);
        }

    }

}