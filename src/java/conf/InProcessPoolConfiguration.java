/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package conf;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stream.StreamFactory;
import stream.impl.LazyPool;
import stream.impl.LazyStreamFactory;

@Configuration
public class InProcessPoolConfiguration {

    @Autowired
    private List<StreamFactory> streamFactories;

    @Bean
    public LazyPool pool() {
        return new LazyPool(streamFactories);
    }

    @Bean
    public LazyStreamFactory lazyStreamFactory() {
        return new LazyStreamFactory();
    }
}
