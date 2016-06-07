/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.test;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import cern.streaming.pool.core.conf.EmbeddedPoolConfiguration;
import cern.streaming.pool.core.conf.StreamCreatorFactoryConfiguration;
import cern.streaming.pool.core.service.support.AbstractStreamSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { EmbeddedPoolConfiguration.class,
        StreamCreatorFactoryConfiguration.class }, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractStreamTest extends AbstractStreamSupport {
    /* Nothing to do here, only the context configuration */

    @Before
    public void clean() {
        super.unregisterAllStreams();
    }
}