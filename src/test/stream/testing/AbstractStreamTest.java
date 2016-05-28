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
    public <T> StreamSupport.OngoingProviding<T> provide(ReactStream<T> reactStream) {
        return new StreamSupport.OngoingProviding<>(providingService, reactStream);
    }

    @Override
    public <T> StreamSupport.OngoingLazyProviding<T> provide(Supplier<ReactStream<T>> reactStream) {
        return new StreamSupport.OngoingLazyProviding<>(lazyProvidingService, reactStream);
    }

    @Override
    public <T> Publisher<T> publisherFrom(StreamId<T> id) {
        return ReactStreams.publisherFrom(discover(id));
    }
}