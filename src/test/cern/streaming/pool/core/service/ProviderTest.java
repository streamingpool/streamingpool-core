/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cern.streaming.pool.core.testing.AbstractStreamTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class ProviderTest extends AbstractStreamTest {

    private static final Publisher ANY_REACTIVE_STREAM = mock(Publisher.class);
    private static final StreamId ANY_STREAM_ID = mock(StreamId.class);
    
    @Autowired
    ProvidingService providingService;
    @Autowired
    DiscoveryService discoveryService;

    @Test
    public void testProvidedStreamCanBeDiscovered() {
        providingService.provide(ANY_STREAM_ID, ANY_REACTIVE_STREAM);
        assertThat(discoveryService.discover(ANY_STREAM_ID)).isNotNull().isEqualTo(ANY_REACTIVE_STREAM);
    }

    @Test
    public void testProvidedUsingHelpersIsDiscovered() {
        provide(ANY_REACTIVE_STREAM).as(ANY_STREAM_ID);
        assertThat(discover(ANY_STREAM_ID)).isNotNull().isEqualTo(ANY_REACTIVE_STREAM);
    }
}
