/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package conf;

import static stream.util.MoreCollections.emptyIfNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import stream.StreamFactory;
import stream.impl.CreatorStreamFactory;
import stream.impl.IdentifiedStreamCreator;
import stream.impl.LazyPool;

/**
 * The spring configuration which shall be used in any application that will have the spring pool embedded. It provides
 * a pool which will pick up the following beans automatically:
 * <ul>
 * <li>Any additional Stream factory (class implementing the interface {@link StreamFactory}) will be automatically
 * plugged into the pool to be used for stream discovery.
 * <li>Any id/streamCreator - pair (Any instance of {@link IdentifiedStreamCreator}) will be automatically plugged into
 * the {@link CreatorStreamFactory}, so that streams declared this way from spring contexts can be created at discovery
 * time.
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

    /**
     * The List of id-streamCreator pairs, which will be automatically collected by spring. We have to be a bit more
     * careful here, because it can happen that there is none, in which case spring would set this to null.
     */
    @Autowired(required = false)
    private List<IdentifiedStreamCreator<?>> identifiedStreamCreators;

    @Bean
    public LazyPool pool() {
        return new LazyPool(streamFactories);
    }

    @Bean
    public CreatorStreamFactory creatorStreamFactory() {
        return new CreatorStreamFactory(emptyIfNull(identifiedStreamCreators));
    }
}
