/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.support;

import org.springframework.beans.factory.annotation.Autowired;

import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import cern.streaming.pool.core.service.akka.AkkaSourceProvidingService;

/**
 * Support class for working with Akka streams. It automatically discovers {@link AkkaSourceProvidingService} and
 * {@link ActorMaterializer} in order to fulfill the requirements of {@link AkkaStreamSupport}.
 * </p>
 * Dependency injection:
 * <ul>
 * <li>{@link AkkaSourceProvidingService}</li>
 * <li>{@link ActorMaterializer}</li>
 * </ul>
 * 
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