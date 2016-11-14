/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.conf;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.names.ConstantsNameContainer;
import cern.streaming.pool.core.names.NameRepositories;
import cern.streaming.pool.core.names.impl.ImmutableNameRepository;

@Configuration
public class NameRepositoryConfiguration {

    @Autowired
    private List<ConstantsNameContainer> expressionConstantsContainers;

    @Bean
    public ImmutableNameRepository nameRepository() {
        return NameRepositories.fromConstantContainers(expressionConstantsContainers);
    }

}
