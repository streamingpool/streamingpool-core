/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.conf;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.names.ConstantsContainer;
import cern.streaming.pool.core.names.NameRepositories;
import cern.streaming.pool.core.names.NameRepository;
import cern.streaming.pool.core.names.resolve.Chains;
import cern.streaming.pool.core.names.resolve.Names;

@Configuration
public class NameRepositoryConfiguration {

    @Autowired
    private List<ConstantsContainer> expressionConstantsContainers;

    @Bean
    public NameRepository nameRepository() {
        return NameRepositories.newFromConstantContainers(expressionConstantsContainers);
    }

    @Bean
    public Function<Object, String> streamIdNameMapping(NameRepository nameRepository) {
        // @formatter:off
        return Chains
                .<String>chain().or(nameRepository::nameFor)
                .or(Names::fromNameMethod)
                .or(Names::fromGetNameMethod)
                .or(Names::fromOverriddenToString)
                .or(Names::fromSimpleClassName)
                .orElseNull();
        // @formatter:on
    }

}
