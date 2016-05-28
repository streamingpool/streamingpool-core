/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.testing;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import conf.AkkaStreamingConfiguration;
import conf.InProcessPoolConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { InProcessPoolConfiguration.class,
        AkkaStreamingConfiguration.class }, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractAkkaStreamTest extends AbstractAkkaStreamSupport {
}
