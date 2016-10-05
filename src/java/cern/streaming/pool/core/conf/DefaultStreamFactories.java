/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.conf;

import cern.streaming.pool.core.service.streamfactory.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.service.TypedStreamFactory;

/**
 * Configuration for including the {@link TypedStreamFactory}s provided in the core project.
 * 
 * @author acalia
 */
@Configuration
public class DefaultStreamFactories {

    @Bean
    public CompositionStreamFactory compositionStreamFactory() {
        return new CompositionStreamFactory();
    }

    @Bean
    public CombineWithLatestStreamFactory combineWithLatestStreamIdStreamFactory() {
        return new CombineWithLatestStreamFactory();
    }

    @Bean
    public DelayedStreamFactory delayedStreamIdStreamFactory() {
        return new DelayedStreamFactory();
    }

    @Bean
    public DerivedStreamFactory derivedStreamIdStreamFactory() {
        return new DerivedStreamFactory();
    }
    
    @Bean
    public OverlapBufferStreamFactory overlapBufferStreamFactory() {
        return new OverlapBufferStreamFactory();
    }

    @Bean
    public FilteredStreamFactory filteredStreamFactory() {
        return new FilteredStreamFactory();
    }
    
}
