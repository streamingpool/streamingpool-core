/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.service;

/**
 * This is interface for adding intercept and fallback factories. this should be use only for test purpose. Intercept
 * means that the factory will have a possibility to create publisher before standard injected factories. Fallback means
 * that the factory will act after all others stream factories.
 */
public interface StreamFactoryRegistry {

    /**
     * Add a given factory as an interceptor.Intercept means that the factory will have a possibility to create
     * publisher before standard injected factories.
     * 
     * @param factory
     */
    void addIntercept(StreamFactory factory);

    /**
     * Add a given factory as a falback. Fallback means that the factory will act after all others stream factories.
     * 
     * @param factory
     */
    void addFallback(StreamFactory factory);
}
