/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import stream.akka.AkkaStreamFactory;

@Configuration
public class AkkaStreamingConfiguration {

    @Bean(destroyMethod = "terminate")
    public ActorSystem createActorSystem() {
        return ActorSystem.create("sys");
    }

    @Bean(destroyMethod = "shutdown")
    public ActorMaterializer createActorMaterializer(ActorSystem actorSystem) {
        return ActorMaterializer.create(actorSystem);
    }

    @Bean
    public AkkaStreamFactory akkaSourceProvidingService(Materializer materializer) {
        return new AkkaStreamFactory(materializer);
    }

}
