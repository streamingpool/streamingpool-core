/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.conf;

import static cern.streaming.pool.core.util.MoreCollections.emptyIfNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.impl.CreatorStreamFactory;
import cern.streaming.pool.core.service.impl.IdentifiedStreamCreator;

/**
 * Spring configuration for using {@link IdentifiedStreamCreator} and {@link StreamCreator}. For each
 * {@link IdentifiedStreamCreator} a new {@link StreamFactory} is registered. It is necessary to include the
 * {@link EmbeddedPoolConfiguration} configuration in order to use this configuration, if not the created factories will
 * not be used to produce streams.
 * 
 */
public class StreamCreatorFactoryConfiguration {

    @Autowired(required = false)
    private List<IdentifiedStreamCreator<?>> identifiedStreamCreators;

    @Bean
    public CreatorStreamFactory creatorStreamFactory() {
        return new CreatorStreamFactory(emptyIfNull(identifiedStreamCreators));
    }

}
