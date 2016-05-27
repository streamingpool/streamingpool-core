/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stream.impl.LazyPool;

@Configuration
public class InProcessPoolConfiguration {

    @Bean
    public LazyPool createPool() {
        return new LazyPool();
    }
}
