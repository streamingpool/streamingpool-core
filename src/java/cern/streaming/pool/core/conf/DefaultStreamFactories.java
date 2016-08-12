/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.service.streamfactory.CombineWithLatestStreamIdStreamFactory;
import cern.streaming.pool.core.service.streamfactory.DelayedStreamIdStreamFactory;
import cern.streaming.pool.core.service.streamfactory.DerivedStreamIdStreamFactory;

@Configuration
public class DefaultStreamFactories {

    @Bean
    public CombineWithLatestStreamIdStreamFactory combineWithLatestStreamIdStreamFactory() {
        return new CombineWithLatestStreamIdStreamFactory();
    }

    @Bean
    public DelayedStreamIdStreamFactory delayedStreamIdStreamFactory() {
        return new DelayedStreamIdStreamFactory();
    }

    @Bean
    public DerivedStreamIdStreamFactory derivedStreamIdStreamFactory() {
        return new DerivedStreamIdStreamFactory();
    }

}
