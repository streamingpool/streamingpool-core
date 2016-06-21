/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.impl;

import cern.streaming.pool.core.service.StreamCreator;
import cern.streaming.pool.core.service.StreamId;

/**
 * Interface that associates a {@link StreamId} with a {@link StreamCreator}.
 * 
 * @see StreamId
 * @see StreamCreator
 * @param <T> the type of the data that the stream created using the {@link StreamCreator} will have
 */
public interface IdentifiedStreamCreator<T> {

    StreamId<T> getId();

    StreamCreator<T> getCreator();

}