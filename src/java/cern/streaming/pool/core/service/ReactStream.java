/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import cern.streaming.pool.core.util.ReactStreams;

/**
 * Marker interface for referring to reactive streams in the context of the streaming-pool project. This interface
 * exists for referring to a reactive stream ( <a href="http://www.reactive-streams.org/">reactive-streams.org/</a>)
 * without specifying the underneath implementation. Utility methods are provided to transform this stream into other
 * reactive stream technologies ({@link ReactStreams})
 * 
 * @see StreamId
 * @see ReactStreams
 * @param <T> the type of the data that the stream contains. This type must match the type of the associated StreamId
 */
public interface ReactStream<T> {
    /* Marker interface */
}