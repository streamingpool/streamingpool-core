/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.service;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Flowable;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.streamingpool.core.conf.EmbeddedPoolConfiguration;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.testing.NamedStreamId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class })
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class StreamFactoryRegistryTest {

    private static final StreamId<String> ID = NamedStreamId.ofName("STREAM_ID");
    private static final String ANY_VALUE_1 = new String("VALUE_1");
    private static final String ANY_VALUE_2 = new String("VALUE_2");

    @Autowired
    private DiscoveryService service;

    @Autowired
    private StreamFactoryRegistry factoryRegistry;

    @Test(expected = IllegalArgumentException.class)
    public void test() {
        service.discover(ID);
    }

    @Test
    public void testInterceptor() {
        factoryRegistry.addIntercept(new InterceptStreamFactory());
        Publisher<String> publisher = service.discover(ID);
        Flowable.fromPublisher(publisher).test().awaitCount(1).assertValue(ANY_VALUE_1);
    }

    @Test
    public void testFallback() {
        factoryRegistry.addFallback(new FallbackStreamFactory());
        Publisher<String> publisher = service.discover(ID);
        Flowable.fromPublisher(publisher).test().awaitCount(1).assertValue(ANY_VALUE_2);
    }

    @Test
    public void testInterceptorFirst() {
        factoryRegistry.addIntercept(new InterceptStreamFactory());
        factoryRegistry.addFallback(new FallbackStreamFactory());
        Publisher<String> publisher = service.discover(ID);
        Flowable.fromPublisher(publisher).test().awaitCount(1).assertValue(ANY_VALUE_1);
    }

    private static class InterceptStreamFactory implements StreamFactory {

        @SuppressWarnings("unchecked")
        @Override
        public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
            if (!id.equals(ID)) {
                return ErrorStreamPair.empty();
            }

            return (ErrorStreamPair<T>) ErrorStreamPair.ofData(Flowable.just(ANY_VALUE_1));
        }

    }

    private static class FallbackStreamFactory implements StreamFactory {

        @SuppressWarnings("unchecked")
        @Override
        public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
            if (!id.equals(ID)) {
                return ErrorStreamPair.empty();
            }

            return (ErrorStreamPair<T>) ErrorStreamPair.ofData(Flowable.just(ANY_VALUE_2));
        }

    }
}
