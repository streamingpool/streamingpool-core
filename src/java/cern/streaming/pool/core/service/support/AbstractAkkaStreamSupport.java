/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.support;

import org.springframework.beans.factory.annotation.Autowired;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import cern.streaming.pool.core.service.akka.AkkaSourceProvidingService;

/**
 * @author kfuchsbe
 */
public abstract class AbstractAkkaStreamSupport extends AbstractStreamSupport implements AkkaStreamSupport {

    @Autowired
    private AkkaSourceProvidingService sourceProvidingService;
    @Autowired
    private ActorMaterializer materializer;

    @Override
    public Materializer materializer() {
        return materializer;
    }

    @Override
    public AkkaSourceProvidingService sourceProvidingService() {
        return sourceProvidingService;
    }

}