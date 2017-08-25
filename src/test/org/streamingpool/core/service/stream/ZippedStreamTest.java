package org.streamingpool.core.service.stream;

import io.reactivex.subscribers.TestSubscriber;
import org.junit.Before;
import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.ZippedStreamId;

import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;
import io.reactivex.functions.Function;

import java.util.Arrays;
import java.util.Optional;

import static io.reactivex.Flowable.just;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;

public class ZippedStreamTest extends AbstractStreamTest implements RxStreamSupport {
    private StreamId<Integer> sourceStreamId1;
    private StreamId<Integer> sourceStreamId2;
    private Function< Object[], Optional<Integer>> function;

    @Before
    public void setUp(){
       function = new Function<Object[], Optional<Integer>>() {
            @Override
            public Optional<Integer> apply(Object[] objects) throws Exception {
                return Optional.of((Integer) objects[0] +(Integer) objects[1]);
            }
        };
    }

    @Test
    public void testZippedStreamWithZipThatAlwaysReturns() {
        sourceStreamId1 = provide(just(1, 3)).withUniqueStreamId();
        sourceStreamId2 = provide(just(2, 4)).withUniqueStreamId();

        StreamId<Integer> zipId = ZippedStreamId.zip(Arrays.asList(sourceStreamId1, sourceStreamId2), function);
        TestSubscriber<Integer> subscriber = createSubscriberAndWait(zipId);
        assertThat(subscriber.values()).hasSize(2).containsExactly(3, 7);
    }

    @Test
    public void testZippedStreamWithZipThatDoesNotAlwaysReturns() {
        sourceStreamId1 = provide(just(3)).withUniqueStreamId();
        sourceStreamId2 = provide(just(4)).withUniqueStreamId();
        StreamId<Integer> zipId = ZippedStreamId.zip(Arrays.asList(sourceStreamId1, sourceStreamId2), function);
        TestSubscriber<Integer> subscriber = createSubscriberAndWait(zipId);

        assertThat(subscriber.values()).hasSize(1).containsExactly(7);
    }

    @Test
    public void testEqualityOfZippedStreamIdOnEqualStreams() {
        StreamId<Integer> sourceStreamId1 = mock(StreamId.class);
        StreamId<Integer> sourceStreamId2 =mock(StreamId.class);
        StreamId<Integer> zip1 = ZippedStreamId.zip(Arrays.asList(sourceStreamId1, sourceStreamId2), function);
        StreamId<Integer> zip2 = ZippedStreamId.zip(Arrays.asList(sourceStreamId1, sourceStreamId2), function);
        assertThat(zip1).isEqualTo(zip2);
    }

    @Test(expected = NullPointerException.class)
    public void testNullStreamId() {
        ZippedStreamId.zip(Arrays.asList(null), mock(Function.class));
    }

    @Test(expected = NullPointerException.class)
    public void testNullZipCompositionFunction() {
        ZippedStreamId.zip(Arrays.asList(mock(StreamId.class), mock(StreamId.class)), null);
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
