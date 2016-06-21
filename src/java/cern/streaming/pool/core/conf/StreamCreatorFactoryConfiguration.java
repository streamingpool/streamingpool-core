/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.conf;

import static cern.streaming.pool.core.util.MoreCollections.emptyIfNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import cern.streaming.pool.core.service.impl.CreatorStreamFactory;
import cern.streaming.pool.core.service.impl.IdentifiedStreamCreator;
import cern.streaming.pool.core.service.impl.ImmutableIdentifiedStreamCreator;

/**
 * This configuration can be used together with a pool configuration and provides and additional factory for streams
 * which is based on so-called creators. A stream-creator is useful, if streams shall be created only on the first
 * lookup time and not at the start time of the application context. While creators can be registered at runtime into,
 * this configuration picks up the following beans automatically on startup:
 * </p>
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
