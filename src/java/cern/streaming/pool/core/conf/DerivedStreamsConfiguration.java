/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.service.impl.DerivedStreamIdStreamFactory;

@Configuration
public class DerivedStreamsConfiguration {

    @Bean
    public DerivedStreamIdStreamFactory derivedStreamFactory() {
        return new DerivedStreamIdStreamFactory();
    }

}
