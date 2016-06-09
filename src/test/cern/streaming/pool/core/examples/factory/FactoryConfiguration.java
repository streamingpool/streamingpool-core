/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.factory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.service.StreamFactory;

@Configuration
public class FactoryConfiguration {

    @Bean
    public StreamFactory factory() {
        return new IntegerStreamFactory();
    }
}
