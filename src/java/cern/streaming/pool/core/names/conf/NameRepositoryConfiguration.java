/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names.conf;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.names.ConstantsNameContainer;
import cern.streaming.pool.core.names.NameRepositories;
import cern.streaming.pool.core.names.NameRepository;
import cern.streaming.pool.core.names.resolve.FunctionChain;
import cern.streaming.pool.core.names.resolve.NameFunctions;

@Configuration
public class NameRepositoryConfiguration {

    @Autowired
    private List<ConstantsNameContainer> expressionConstantsContainers;

    @Bean
    public NameRepository nameRepository() {
        return NameRepositories.newFromConstantContainers(expressionConstantsContainers);
    }

    @Bean
    public Function<Object, String> streamIdNameMapping(NameRepository nameRepository) {
        // @formatter:off
        return FunctionChain
                .chain(nameRepository::nameFor)
                .then(NameFunctions.nameMethod())
                .then(NameFunctions.getNameMethod())
                .then(NameFunctions.overriddenToString())
                .then(NameFunctions.simpleClassName())
                .orElseNull();
        // @formatter:on
    }

}
