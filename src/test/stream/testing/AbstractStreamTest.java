/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.testing;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import conf.InProcessPoolConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InProcessPoolConfiguration.class, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractStreamTest extends AbstractStreamSupport {
    /* Nothing to do here, only the context configuration */
}