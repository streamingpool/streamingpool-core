/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

public interface StreamFactory {

    /***
     * @param id the id of the stream to create
     * @return the new stream or {@code null} if this factory cannot create the stream of the given id
     */
    <T> ReactStream<T> create(StreamId<T> id);

}
