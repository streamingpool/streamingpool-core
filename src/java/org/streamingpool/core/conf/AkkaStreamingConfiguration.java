// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on

package org.streamingpool.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.streamingpool.core.service.akka.AkkaSourceProvidingService;
import org.streamingpool.core.service.akka.AkkaStreamFactory;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;

/**
 * This spring configuration provides the beans which are required to provide Akka streams (sources) into the streaming
 * pool. The core bean to use here is the {@link AkkaSourceProvidingService} on which Akka sources can be registered
 * which either are materialized directly, or at discovery time.
 * 
 * The {@link ActorSystem} is created with the name {@link AkkaStreamingConfiguration#DEFAULT_ACTOR_SYSTEM_NAME}
 * 
 * @author kfuchsbe
 */
@Configuration
public class AkkaStreamingConfiguration {

    private static final String DEFAULT_ACTOR_SYSTEM_NAME = "sys";

    @Bean(destroyMethod = "terminate")
    public ActorSystem actorSystem() {
        return ActorSystem.create(DEFAULT_ACTOR_SYSTEM_NAME);
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
