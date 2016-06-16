/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static cern.streaming.pool.core.examples.creators.InjectionIds.INJECTION_CONTROL_SYSTEM;
import static cern.streaming.pool.core.service.util.ReactiveStreams.fromRx;
import static cern.streaming.pool.core.service.util.ReactiveStreams.publisherFrom;
import static cern.streaming.pool.core.service.util.ReactiveStreams.rxFrom;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cern.streaming.pool.core.conf.EmbeddedPoolConfiguration;
import cern.streaming.pool.core.conf.StreamCreatorFactoryConfiguration;
import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class, StreamCreatorFactoryConfiguration.class,
        InjectionConfiguration.class })
public class InjectionExampleTest {

    @Autowired
    private DiscoveryService discovery;

    @Test
    public void testInjectionUsingStreamCreator() {

        BlockingTestSubscriber<InjectionDomainObject> subscriber = BlockingTestSubscriber.ofName("Subscriber");

        publisherFrom(fromRx(rxFrom(discovery.discover(INJECTION_CONTROL_SYSTEM)).limit(2))).subscribe(subscriber);

        subscriber.await(5, TimeUnit.SECONDS);

        assertThat(subscriber.getValues()).hasSize(2);
        assertThat(subscriber.getValues().stream().map(injDomain -> injDomain.getInjectionName()).collect(toList()))
                .contains("Injection number 0", "Injection number 1");
    }

}
