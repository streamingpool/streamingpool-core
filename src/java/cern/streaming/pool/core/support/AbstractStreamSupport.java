/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.support;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import cern.streaming.pool.core.service.CreatorProvidingService;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ProvidingService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;

/**
 * This class provides support for the discovery and for providing streams.
 * </p>
 * Dependency injection:
 * <ul>
 * <li>{@link DiscoveryService}</li>
 * <li>{@link ProvidingService}</li>
 * <li>{@link CreatorProvidingService}</li>
 * </ul>
 * 
 * @author kfuchsbe
 */
public class AbstractStreamSupport implements StreamSupport {

    @Autowired
    private DiscoveryService discoveryService;
    @Autowired
    private ProvidingService providingService;
    @Autowired
    private CreatorProvidingService lazyProvidingService;

    @Override
    public <T> ReactiveStream<T> discover(StreamId<T> id) {
        return discoveryService.discover(id);
    }

    @Override
    public <T> StreamSupport.OngoingProviding<T> provide(ReactiveStream<T> reactStream) {
        return new StreamSupport.OngoingProviding<>(providingService, reactStream);
    }

    @Override
    public <T> StreamSupport.OngoingLazyProviding<T> provide(StreamCreator<T> reactStream) {
        return new StreamSupport.OngoingLazyProviding<>(lazyProvidingService, reactStream);
    }

    @Override
    public <T> Publisher<T> publisherFrom(StreamId<T> id) {
        return ReactiveStreams.publisherFrom(discover(id));
    }

    @Override
    public ProvidingService providingService() {
        return providingService;
    }

}