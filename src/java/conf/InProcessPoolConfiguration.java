/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package conf;

import static stream.util.MoreCollections.emptyIfNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stream.StreamFactory;
import stream.impl.CreatorStreamFactory;
import stream.impl.IdentifiedStreamCreator;
import stream.impl.LazyPool;

@Configuration
public class InProcessPoolConfiguration {

    @Autowired(required = false)
    private List<StreamFactory> streamFactories;

    @Autowired(required = false)
    private List<IdentifiedStreamCreator<?>> identifiedStreamCreators;

    @Bean
    public LazyPool pool() {
        return new LazyPool(emptyIfNull(streamFactories));
    }

    @Bean
    public CreatorStreamFactory creatorStreamFactory() {
        return new CreatorStreamFactory(emptyIfNull(identifiedStreamCreators));
    }
}
