/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import cern.streaming.pool.core.conf.AkkaStreamingConfiguration;
import cern.streaming.pool.core.conf.EmbeddedPoolConfiguration;
import cern.streaming.pool.core.conf.StreamCreatorFactoryConfiguration;
import cern.streaming.pool.core.service.CreatorProvidingService;
import cern.streaming.pool.core.service.support.AbstractAkkaStreamSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class, AkkaStreamingConfiguration.class,
        StreamCreatorFactoryConfiguration.class }, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractAkkaStreamTest extends AbstractAkkaStreamSupport {
    /* Nothing to do here. Only the contexts and the test runner */
}
