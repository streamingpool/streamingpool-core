/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

import static cern.streaming.pool.core.examples.creators.InjectionIds.INJECTION_CONTROL_SYSTEM;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.service.impl.IdentifiedStreamCreator;
import cern.streaming.pool.core.service.impl.ImmutableIdentifiedStreamCreator;

@Configuration
public class InjectionConfiguration {

    @Bean
    public IdentifiedStreamCreator<InjectionDomainObject> injectionStreamCreator() {
        return ImmutableIdentifiedStreamCreator.of(INJECTION_CONTROL_SYSTEM, new InjectionStreamCreator());
    }
}
