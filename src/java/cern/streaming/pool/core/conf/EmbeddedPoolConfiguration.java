/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.conf;

import static cern.streaming.pool.core.util.MoreCollections.emptyIfNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cern.streaming.pool.core.service.StreamFactory;
import cern.streaming.pool.core.service.impl.LocalPool;

/**
 * The spring configuration which shall be used in any application that will have the spring pool embedded. It provides
 * a pool which will pick up the following beans automatically:
 * </p>
 * Dependency injection:
 * <ul>
 * <li>{@link StreamFactory}: Any additional Stream factory will be automatically plugged into the pool to be used for
 * stream discovery.
 * </ul>
 * 
 * @author kfuchsbe
 */
@Configuration
public class EmbeddedPoolConfiguration {

    /**
     * A list of stream factories which will be automatically collected by Spring. Since there will be at least one (the
     * below created factory) we can keep the required=true (default).
     */
    @Autowired(required = false)
    private List<StreamFactory> streamFactories;
    
    @Bean
    public LocalPool pool() {
        return new LocalPool(emptyIfNull(streamFactories));
    }

}
