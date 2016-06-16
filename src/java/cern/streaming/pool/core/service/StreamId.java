/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

/**
 * Interface for defining custom stream identifiers. Each stream is identified with one {@link StreamId} so a custom,
 * domain-specific, implementation is expected to be provided.
 * </p>
 * For example, a stream of random numbers may have a special implementation of {@link StreamId} that includes the seed
 * to be used in the number generation. Also, another implementation may include information about the range in which
 * the numbers need to be scaled.
 * 
 * @see ReactStream
 * @param <T> the type of the data that the a stream created from this id contains
 */
public interface StreamId<T> {
    /* Marker interface */
}
