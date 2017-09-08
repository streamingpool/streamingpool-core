package org.streamingpool.core.service.stream;

import io.reactivex.functions.BiFunction;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.ZippedStreamId;

import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

import java.util.Optional;

import static io.reactivex.Flowable.just;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public class ZippedStreamTest extends AbstractStreamTest implements RxStreamSupport {
    private StreamId<Integer> sourceStreamId1;
    private StreamId<Integer> sourceStreamId2;
    private BiFunction<Integer, Integer, Optional<Integer>> function;

    @Before
    public void setUp(){
       function = (Integer i, Integer j) -> Optional.of(i +j);
    }

    @Test
    public void testZippedStreamWithZipThatAlwaysReturns() {
        sourceStreamId1 = provide(just(1, 3)).withUniqueStreamId();
        sourceStreamId2 = provide(just(2, 4)).withUniqueStreamId();

        StreamId<Integer> zipId = ZippedStreamId.zip(sourceStreamId1, sourceStreamId2, function);
        TestSubscriber<Integer> subscriber = createSubscriberAndWait(zipId);
        assertThat(subscriber.values()).hasSize(2).containsExactly(3, 7);
    }

    @Test
    public void testZippedStreamWithZipThatDoesNotAlwaysReturns() {
        sourceStreamId1 = provide(just(3)).withUniqueStreamId();
        sourceStreamId2 = provide(just(4)).withUniqueStreamId();
        StreamId<Integer> zipId = ZippedStreamId.zip(sourceStreamId1, sourceStreamId2, function);
        TestSubscriber<Integer> subscriber = createSubscriberAndWait(zipId);

        assertThat(subscriber.values()).hasSize(1).containsExactly(7);
    }

    @Test
    public void testEqualityOfZippedStreamIdOnEqualStreams() {
        StreamId<Integer> sourceStreamId1 = mock(StreamId.class);
        StreamId<Integer> sourceStreamId2 = mock(StreamId.class);
        StreamId<Integer> zip1 = ZippedStreamId.zip(sourceStreamId1, sourceStreamId2, function);
        StreamId<Integer> zip2 = ZippedStreamId.zip(sourceStreamId1, sourceStreamId2, function);
        assertThat(zip1).isEqualTo(zip2);
    }

    @Test(expected = NullPointerException.class)
    public void testNullStreamId1() {
        ZippedStreamId.zip(null, mock(StreamId.class), mock(BiFunction.class));
    }

    @Test(expected = NullPointerException.class)
    public void testNullStreamId2() {
        ZippedStreamId.zip(mock(StreamId.class), null, mock(BiFunction.class));
    }

    @Test(expected = NullPointerException.class)
    public void testNullZipCompositionFunction() {
        ZippedStreamId.zip(mock(StreamId.class), mock(StreamId.class), null);
    }

    private TestSubscriber<Integer> createSubscriberAndWait(StreamId<Integer> sourceStreamId) {
        TestSubscriber<Integer> subscriber = TestSubscriber.create();
        discover(sourceStreamId).subscribe(subscriber);
        try {
            subscriber.await();
        } catch (InterruptedException e) {
            fail("Interrupted wait", e);
        }
        return subscriber;
    }

}
