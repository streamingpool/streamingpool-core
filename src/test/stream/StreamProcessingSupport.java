/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import conf.InProcessPoolConfiguration;
import rx.Observable;
import stream.impl.SimplePool;

/**
 * @author acalia
 */
@ContextConfiguration(classes = InProcessPoolConfiguration.class, loader = AnnotationConfigContextLoader.class)
public abstract class StreamProcessingSupport {

    @Autowired
    private DiscoveryService discoveryService;
    @Autowired
    private ProvidingService providingService;

    /**
     * TODO Find a better way to unregister streams
     */
    protected void unregisterAllStreams() {
        ((SimplePool) discoveryService).clearPool();
    }

    protected <T> ReactStream<T> discover(final StreamId<T> id) {
        return discoveryService.discover(id);
    }

    protected <T> OngoingProviding<T> provide(ReactStream<T> reactStream) {
        return new OngoingProviding<>(providingService, reactStream);
    }

    protected <T> Publisher<T> publisherFrom(StreamId<T> id) {
        return ReactStreams.publisherFrom(discover(id));
    }

    protected <T> Observable<T> rxFrom(StreamId<T> id) {
        return ReactStreams.rxFrom(discover(id));
    }

    protected <T> Source<T, NotUsed> sourceFrom(StreamId<T> id) {
        return ReactStreams.sourceFrom(discover(id));
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