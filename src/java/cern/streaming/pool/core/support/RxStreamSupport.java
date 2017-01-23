/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.support;

import cern.streaming.pool.core.service.StreamId;
import io.reactivex.Flowable;

/**
 * Support interface for working with RxJava streams.
 * 
 * @author acalia
 */
public interface RxStreamSupport extends StreamSupport {

    default <T> Flowable<T> rxFrom(StreamId<T> id) {
        return Flowable.fromPublisher(discover(id));
    }

}
