/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.names;

/**
 * Indicates a repository of names for any object
 */
@FunctionalInterface
public interface NameRepository {

    /**
     * Returns for the specified object.
     * 
     * @param object the object for which to retrieve the nam
     * @return a proper name for the given object, or {@code null} if the object is not known by the repository.
     */
    String nameFor(Object object);

}
