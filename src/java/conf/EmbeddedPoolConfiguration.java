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

/**
 * The spring configuration which shall be used in any application that will have the spring pool embedded. It provides
 * a pool which will pick up the following beans automatically:
 * <ul>
 * <li>Any additional Stream factory (class implementing the interface {@link StreamFactory}) will be automatically
 * plugged into the pool to be used for stream discovery.
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
    @Autowired
    private List<StreamFactory> streamFactories;

    @Bean
    public LazyPool pool() {
        return new LazyPool(streamFactories);
    }

}
