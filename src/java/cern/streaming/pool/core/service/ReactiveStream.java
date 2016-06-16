/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service;

import cern.streaming.pool.core.service.util.ReactiveStreams;

/**
 * Marker interface for referring to reactive streams in the context of the streaming-pool project. The definition of a
 * reactive stream is the same provided by the Reactive Manifesto
 * <a href="http://www.reactive-streams.org/">reactive-streams.org/</a>. This interface does not force or specify the
 * underneath implementation. Utility methods are provided to transform this stream into other reactive stream
 * technologies ({@link ReactiveStreams})
 * 
 * @see StreamId
 * @see ReactiveStreams
 * @param <T> the type of the data that the stream contains. This type must match the type of the associated StreamId
 */
public interface ReactiveStream<T> {
    /* Marker interface */
}
