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

import static org.streamingpool.core.util.MoreCollections.emptyIfNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.streamingpool.core.service.StreamFactory;
import org.streamingpool.core.service.TypedStreamFactory;
import org.streamingpool.core.service.impl.LocalPool;

/**
 * The spring configuration which shall be used in any application that will have the spring pool embedded. It provides
 * a pool which will pick up the following beans automatically:
 * <p>
 * Dependency injection:
 * <ul>
 * <li>{@link TypedStreamFactory}: Any additional Stream factory will be automatically plugged into the pool to be used for
 * stream discovery.
 * </ul>
 *
 * @author kfuchsbe
 */
@Configuration
public class EmbeddedPoolConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedPoolConfiguration.class);

    /**
     * A list of stream factories which will be automatically collected by Spring. Since there will be at least one (the
     * below created factory) we can keep the required=true (default).
     */
    @Autowired(required = false)
    private List<StreamFactory> streamFactories;

    @Value( "${bufferSize}" )
    private int bufferSize;

    @Bean
    public String post(){
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAA "+bufferSize);
        return "marek";
    }

    @Bean
    public LocalPool pool() {
       LOGGER.error("AAAAAAAAAAAAAAAAAAAAAAA "+bufferSize);
        return new LocalPool(emptyIfNull(streamFactories));
    }

}
