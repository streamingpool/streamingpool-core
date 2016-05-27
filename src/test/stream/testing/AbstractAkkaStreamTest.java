/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import conf.AkkaStreamingConfiguration;
import stream.support.AkkaStreamSupport;

@ContextConfiguration(classes = AkkaStreamingConfiguration.class, loader = AnnotationConfigContextLoader.class)
public abstract class AbstractAkkaStreamTest extends AbstractStreamTest implements AkkaStreamSupport {

    @Autowired
    private ActorMaterializer materializer;

    @Override
    public Materializer materializer() {
        return materializer;
    }
}
