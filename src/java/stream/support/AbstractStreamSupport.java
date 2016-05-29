/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.support;

import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import stream.DiscoveryService;
import stream.LazyProvidingService;
import stream.ProvidingService;
import stream.ReactStream;
import stream.ReactStreams;
import stream.StreamId;
import stream.impl.SimplePool;

/**
 * @author kfuchsbe
 */
public class AbstractStreamSupport implements StreamSupport {

    @Autowired
    private DiscoveryService discoveryService;
    @Autowired
    private ProvidingService providingService;
    @Autowired
    private LazyProvidingService lazyProvidingService;

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

    @Override
    public ProvidingService providingService() {
        return providingService;
    }

}