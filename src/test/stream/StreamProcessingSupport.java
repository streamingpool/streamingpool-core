/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author acalia
 */
public abstract class StreamProcessingSupport {

    @Autowired
    protected DiscoveryService discoveryService;
    @Autowired
    protected ProvidingService providingService;

    protected ReactStream<Integer> discover(final StreamId<Integer> id) {
        return discoveryService.discover(id);
    }

    protected <T> OngoingProviding<T> provide(ReactStream<T> reactStream) {
        return new OngoingProviding<>(providingService, reactStream);
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