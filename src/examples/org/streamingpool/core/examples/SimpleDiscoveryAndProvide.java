/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.examples;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.streamingpool.core.conf.EmbeddedPoolConfiguration;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.ProvidingService;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.testing.NamedStreamId;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

/**
 * Simple example of the {@link DiscoveryService} and {@link ProvidingService}
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class })
public class SimpleDiscoveryAndProvide {

    @Autowired
    private DiscoveryService discoveryService;

    @Autowired
    private ProvidingService providingService;

    @Test
    public void test() throws InterruptedException {
        // The values we want to publish on the stream
        Iterable<Integer> streamValues = Arrays.asList(1, 2, 3, 4, 5);
        // The actual stream. NOTE: it can be any implementation of a Publisher interface
        Flowable<Integer> stream = Flowable.fromIterable(streamValues);
        // The key for referring to the stream
        StreamId<Integer> streamId = NamedStreamId.ofName("Any stream id");

        // This will register the stream in the Streaming Pool system
        providingService.provide(streamId, stream);

        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        // With a test subscriber from RxJava2 we receives the values
        discoveryService.discover(streamId).subscribe(subscriber);

        subscriber.await();
        subscriber.assertValueSequence(streamValues);
    }

}
