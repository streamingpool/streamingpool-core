/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import cern.streaming.pool.core.service.akka.AkkaSourceProvidingService;
import cern.streaming.pool.core.service.akka.AkkaStreamFactory;

/**
 * This spring configuration provides the beans which are required to provide akka streams (sources) into the streaming
 * pool. The core bean to use here is the {@link AkkaSourceProvidingService} on which akka sources can be registered
 * which either are materialized directly, or at discovery time.
 * 
 * @author kfuchsbe
 */
@Configuration
public class AkkaStreamingConfiguration {

    @Bean(destroyMethod = "terminate")
    public ActorSystem actorSystem() {
        return ActorSystem.create("sys");
    }

    @Bean(destroyMethod = "shutdown")
    public ActorMaterializer actorMaterializer(ActorSystem actorSystem) {
        return ActorMaterializer.create(actorSystem);
    }

    @Bean
    public AkkaStreamFactory akkaSourceProvidingService(Materializer materializer) {
        return new AkkaStreamFactory(materializer);
    }

}
