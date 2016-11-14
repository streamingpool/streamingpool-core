/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names;

/**
 * Indicates a repository of names for any object
 */
public interface NameRepository {

    /**
     * Returns for the specified object. Implementations may have different way of getting the name of the object.
     */
    String nameFor(Object object);

}
