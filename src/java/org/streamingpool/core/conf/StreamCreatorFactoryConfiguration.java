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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.streamingpool.core.service.impl.IdentifiedStreamCreator;
import org.streamingpool.core.service.impl.ImmutableIdentifiedStreamCreator;
import org.streamingpool.core.service.streamfactory.CreatorStreamFactory;

/**
 * This configuration can be used together with a pool configuration and provides and additional factory for streams
 * which is based on so-called creators. A stream-creator is useful, if streams shall be created only on the first
 * lookup time and not at the start time of the application context. While creators can be registered at runtime into,
 * this configuration picks up the following beans automatically on startup:
 * <p>
 * Dependency injection:
 * <ul>
 * <li>All instances of {@link ImmutableIdentifiedStreamCreator}. These are passed on the the created factory and are available
 * for lookups from then on. This mechanism provides a simple way to organize (lazy initializing) streams in spring
 * configurations.
 * </ul>
 * 
 * @author kfuchsbe
 */
public class StreamCreatorFactoryConfiguration {

    @Autowired(required = false)
    private List<IdentifiedStreamCreator<?>> identifiedStreamCreators;

    @Bean
    public CreatorStreamFactory creatorStreamFactory() {
        return new CreatorStreamFactory(emptyIfNull(identifiedStreamCreators));
    }

}
