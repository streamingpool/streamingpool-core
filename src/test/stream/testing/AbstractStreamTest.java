/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.testing;

import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import conf.InProcessPoolConfiguration;
import stream.DiscoveryService;
import stream.LazyProvidingService;
import stream.ProvidingService;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamId;
import stream.impl.SimplePool;
import stream.support.StreamSupport;

@ContextConfiguration(classes = InProcessPoolConfiguration.class, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractStreamTest implements StreamSupport {

    @Autowired
    private DiscoveryService discoveryService;
    @Autowired
    private ProvidingService providingService;
    @Autowired
    private LazyProvidingService lazyProvidingService;

    /*
     * TODO Find a better way to unregister streams
     */
    public void unregisterAllStreams() {
        ((SimplePool) discoveryService).clearPool();
    }

    @Override
    public <T> ReactStream<T> discover(StreamId<T> id) {
        return discoveryService.discover(id);
    }

    @Override
    public <T> OngoingProviding<T> provide(ReactStream<T> reactStream) {
        return new OngoingProviding<>(providingService, reactStream);
    }

    @Override
    public <T> OngoingLazyProviding<T> provide(Supplier<ReactStream<T>> reactStream) {
        return new OngoingLazyProviding<>(lazyProvidingService, reactStream);
    }

    @Override
    public <T> Publisher<T> publisherFrom(StreamId<T> id) {
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

    public static class OngoingLazyProviding<T> {
        private final Supplier<ReactStream<T>> reactStream;
        private final LazyProvidingService providingService;

        private OngoingLazyProviding(LazyProvidingService providingService, Supplier<ReactStream<T>> reactStream) {
            this.providingService = providingService;
            this.reactStream = reactStream;
        }

        public void as(StreamId<T> id) {
            providingService.provide(id, reactStream);
        }

    }
}