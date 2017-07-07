/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.service;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.streamingpool.core.conf.EmbeddedPoolConfiguration;
import org.streamingpool.core.domain.ErrorStreamPair;

import com.oracle.webservices.internal.impl.internalspi.encoding.StreamDecoder;

import io.reactivex.Flowable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class })
@Ignore
public class StreamFactoryRegistryTest {

    private static final MyStreamId ID = new MyStreamId();
    @Autowired
    private DiscoveryService discoveryService;

    // also autowire the new interface

    @Test
    public void test() {
        
        // registry.addIntercept(new MyFactory())
        
        Publisher<String> stream = discoveryService.discover(ID);

        fail("Not yet implemented");
    }



    private static class MyStreamId implements StreamId<String> {

    }
    
    private static class MyFactory implements StreamFactory {

        @Override
        public <T> ErrorStreamPair<T> create(StreamId<T> id, DiscoveryService discoveryService) {
            if (ID.equals(id)) {
                return ErrorStreamPair.ofData(Flowable.never());
            }
            return ErrorStreamPair.empty();
        }
        
    }

}
