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

public class StreamCreatorFactoryConfiguration {

    /**
     * The List of id-streamCreator pairs, which will be automatically collected by spring. We have to be a bit more
     * careful here, because it can happen that there is none, in which case spring would set this to null.
     */
    /* */
    @Autowired(required = false)
    private List<IdentifiedStreamCreator<?>> identifiedStreamCreators;

    @Bean
    public CreatorStreamFactory creatorStreamFactory() {
        return new CreatorStreamFactory(emptyIfNull(identifiedStreamCreators));
    }

}
