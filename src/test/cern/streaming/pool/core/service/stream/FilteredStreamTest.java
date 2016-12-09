/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.stream;

import static cern.streaming.pool.core.service.streamid.FilteredStreamId.filterBy;
import static cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber.ofName;
import static io.reactivex.Flowable.just;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.FilteredStreamId;
import cern.streaming.pool.core.support.RxStreamSupport;
import cern.streaming.pool.core.testing.AbstractStreamTest;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;

public class FilteredStreamTest extends AbstractStreamTest implements RxStreamSupport {

    @Test
    public void streamFiltered() {
        StreamId<Integer> sourceId = provide(just(1, 2, 3, 4)).withUniqueStreamId();
        FilteredStreamId<Integer> filterId = filterBy(sourceId, value -> value % 2 == 0);
        
        BlockingTestSubscriber<Integer> subscriber = ofName("Subscriber");
        discover(filterId).subscribe(subscriber);
        subscriber.await();
        
        assertThat(subscriber.getValues()).hasSize(2).containsOnly(2, 4);
    }
    
    @Test(expected = NullPointerException.class)
    public void testNullSource() {
        discover(filterBy(null, any -> false));
    }
    
    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void testNullPredicate() {
        discover(filterBy(mock(StreamId.class), null));
    }
}
