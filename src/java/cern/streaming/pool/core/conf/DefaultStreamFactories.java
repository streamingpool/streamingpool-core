/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.service.TypedStreamFactory;
import cern.streaming.pool.core.service.streamfactory.CombineWithLatestStreamFactory;
import cern.streaming.pool.core.service.streamfactory.DelayedStreamFactory;
import cern.streaming.pool.core.service.streamfactory.DerivedStreamFactory;
import cern.streaming.pool.core.service.streamfactory.FilteredStreamFactory;
import cern.streaming.pool.core.service.streamfactory.OverlapBufferStreamFactory;

/**
 * Configuration for including the {@link TypedStreamFactory}s provided in the core project.
 * 
 * @author acalia
 */
@Configuration
public class DefaultStreamFactories {

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
