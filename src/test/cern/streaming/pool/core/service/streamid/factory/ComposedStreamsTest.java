package cern.streaming.pool.core.service.streamid.factory;

import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.util.ReactiveStreams;
import cern.streaming.pool.core.support.RxStreamSupport;
import cern.streaming.pool.core.testing.AbstractStreamTest;
import cern.streaming.pool.core.testing.subscriber.BlockingTestSubscriber;
import org.junit.Test;
import rx.Observable;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link ComposedStreams}.
 *
 * @author timartin
 */
public class ComposedStreamsTest extends AbstractStreamTest implements RxStreamSupport {

    private static final StreamId<Object> DUMMY_STREAM_ID_1 = new StreamId<Object>() {
        /* FOR TESTINF PURPOSES */
    };

    private static final StreamId<Object> DUMMY_STREAM_ID_2 = new StreamId<Object>() {
        /* FOR TESTINF PURPOSES */
    };

    @Test(expected = NullPointerException.class)
    public void testMappedStreamWithNullSourceStreamId() {
        ComposedStreams.mappedStream(null, val -> Optional.of(val));
    }

    @Test(expected = NullPointerException.class)
    public void testMappedStreamWithNullConversionFunction() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> empty()).withUniqueStreamId();
        ComposedStreams.mappedStream(sourceStreamId, null);
    }

    @Test
    public void testMappedStreamWithConversionThatAlwaysReturns() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> mappedStreamId = ComposedStreams.mappedStream(sourceStreamId,
                val -> val + 1);
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(mappedStreamId);
        assertThat(subscriber.getValues()).hasSize(2).containsExactly(2, 4);
    }

    @Test
    public void testMappedStreamWithConversionThatDoesNotAlwaysReturns() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> mappedStreamId = ComposedStreams.mappedStream(sourceStreamId,
                val -> (val == 1) ? val : null);
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(mappedStreamId);
        assertThat(subscriber.getValues()).hasSize(1).containsExactly(1);
    }

    @Test
    public void testEqualityOfMappedStreamIdOnEqualStreams() {
        StreamId<Object> sourceStreamId = DUMMY_STREAM_ID_1;
        Function<Object, Object> conversion = Optional::of;
        StreamId<Object> mappedStream1 = ComposedStreams.mappedStream(sourceStreamId, conversion);
        StreamId<Object> mappedStream2 = ComposedStreams.mappedStream(sourceStreamId, conversion);
        assertThat(mappedStream1).isEqualTo(mappedStream2);
    }

    @Test
    public void testEqualityOfMappedStreamIdOnNotEqualConversion() {
        StreamId<Object> sourceStreamId = DUMMY_STREAM_ID_1;
        StreamId<Object> mappedStream1 = ComposedStreams.mappedStream(sourceStreamId, Optional::of);
        StreamId<Object> mappedStream2 = ComposedStreams.mappedStream(sourceStreamId, Optional::of);
        assertThat(mappedStream1).isNotEqualTo(mappedStream2);
    }

    @Test
    public void testEqualityOfMappedStreamIdOnNotEqualStreamSources() {
        Function<Object, Object> conversion = Optional::of;
        StreamId<Object> mappedStream1 = ComposedStreams.mappedStream(DUMMY_STREAM_ID_1, conversion);
        StreamId<Object> mappedStream2 = ComposedStreams.mappedStream(DUMMY_STREAM_ID_2, conversion);
        assertThat(mappedStream1).isNotEqualTo(mappedStream2);
    }

    @Test(expected = NullPointerException.class)
    public void testFlatMappedStreamWithNullSourceStreamId() {
        ComposedStreams.flatMappedStream(null, val -> ReactiveStreams.fromRx(Observable.just(val)));
    }

    @Test(expected = NullPointerException.class)
    public void testFlatMappedStreamWithNullConversionFunction() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> empty()).withUniqueStreamId();
        ComposedStreams.flatMappedStream(sourceStreamId, null);
    }

    @Test
    public void testFlatMappedStreamWithConversionAlwaysReturns() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> flatMappedStreamId = ComposedStreams.flatMappedStream(sourceStreamId,
                val -> ReactiveStreams.fromRx(Observable.<Integer> just(val, val)));
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(flatMappedStreamId);
        assertThat(subscriber.getValues()).hasSize(4).containsExactly(1, 1, 3, 3);
    }

    @Test
    public void testFlatMappedStreamWithConversionThatDoesNotAlwaysReturns() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> flatMappedStreamId = ComposedStreams.flatMappedStream(sourceStreamId,
                val -> (val == 1) ? ReactiveStreams.fromRx(Observable.<Integer> just(val, val))
                        : ReactiveStreams.fromRx(Observable.<Integer> empty()));
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(flatMappedStreamId);
        assertThat(subscriber.getValues()).hasSize(2).containsExactly(1, 1);
    }

    @Test
    public void testEqualityOfFlatMappedStreamIdOnEqualStreams() {
        StreamId<Object> sourceStreamId = DUMMY_STREAM_ID_1;
        Function<Object, ReactiveStream<Object>> conversion = o -> ReactiveStreams.fromRx(Observable.empty());
        StreamId<Object> flatMappedStream1 = ComposedStreams.flatMappedStream(sourceStreamId, conversion);
        StreamId<Object> flatMappedStream2 = ComposedStreams.flatMappedStream(sourceStreamId, conversion);
        assertThat(flatMappedStream1).isEqualTo(flatMappedStream2);
    }

    @Test
    public void testEqualityOfFlatMappedStreamIdOnNotEqualStreamSources() {
        Function<Object, ReactiveStream<Object>> conversion = o -> ReactiveStreams.fromRx(Observable.empty());
        StreamId<Object> flatMappedStream1 = ComposedStreams.flatMappedStream(DUMMY_STREAM_ID_1, conversion);
        StreamId<Object> flatMappedStream2 = ComposedStreams.flatMappedStream(DUMMY_STREAM_ID_2, conversion);
        assertThat(flatMappedStream1).isNotEqualTo(flatMappedStream2);
    }

    @Test
    public void testEqualityOfFlatMappedStreamIdOnNotEqualConversions() {
        StreamId<Object> sourceStreamId = DUMMY_STREAM_ID_1;
        StreamId<Object> flatMappedStream1 = ComposedStreams.flatMappedStream(sourceStreamId,
                o -> ReactiveStreams.fromRx(Observable.empty()));
        StreamId<Object> flatMappedStream2 = ComposedStreams.flatMappedStream(sourceStreamId,
                o -> ReactiveStreams.fromRx(Observable.empty()));
        assertThat(flatMappedStream1).isNotEqualTo(flatMappedStream2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergedStreamWithNullSourceStreamIds() {
        ComposedStreams.mergedStream(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergedStreamWithEmptySourceStreamIds() {
        ComposedStreams.mergedStream(Collections.emptyList());
    }

    @Test
    public void testMergedStreamWithSingleSourceStreamId() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> mergedStreamId = ComposedStreams.mergedStream(Arrays.asList(sourceStreamId));
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(mergedStreamId);
        assertThat(subscriber.getValues()).hasSize(2).containsExactly(1, 3);
    }

    @Test
    public void testMergedStreamWithMultipleSourceStreamIds() {
        StreamId<Integer> sourceStreamId1 = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> sourceStreamId2 = provide(Observable.<Integer> just(2, 4)).withUniqueStreamId();
        StreamId<Integer> mergedStreamId = ComposedStreams
                .mergedStream(Arrays.asList(sourceStreamId1, sourceStreamId2));
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(mergedStreamId);
        assertThat(subscriber.getValues()).hasSize(4).contains(1, 2, 3, 4);
    }

    @Test
    public void testEqualityOfMergedStreamIdOnEqualStreams() {
        StreamId<Object> sourceStreamId1 = DUMMY_STREAM_ID_1;
        StreamId<Object> sourceStreamId2 = DUMMY_STREAM_ID_2;
        StreamId<Object> mergedStream1 = ComposedStreams.mergedStream(Arrays.asList(sourceStreamId1, sourceStreamId2));
        StreamId<Object> mergedStream2 = ComposedStreams.mergedStream(Arrays.asList(sourceStreamId1, sourceStreamId2));
        assertThat(mergedStream1).isEqualTo(mergedStream2);
    }

    @Test
    public void testEqualityOfMergedStreamIdOnNotEqualStreamSources() {
        StreamId<Object> sourceStreamId1 = DUMMY_STREAM_ID_1;
        StreamId<Object> sourceStreamId2 = DUMMY_STREAM_ID_2;
        StreamId<Object> mergedStream1 = ComposedStreams.mergedStream(Arrays.asList(sourceStreamId1, sourceStreamId1));
        StreamId<Object> mergedStream2 = ComposedStreams.mergedStream(Arrays.asList(sourceStreamId2, sourceStreamId2));
        assertThat(mergedStream1).isNotEqualTo(mergedStream2);
    }

    @Test(expected = NullPointerException.class)
    public void testFilteredStreamWithNullSourceStreamId() {
        ComposedStreams.filteredStream(null, val -> true);
    }

    @Test(expected = NullPointerException.class)
    public void testFilteredStreamWithNullPredicate() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> empty()).withUniqueStreamId();
        ComposedStreams.filteredStream(sourceStreamId, null);
    }

    @Test
    public void testFilteredStreamWithCorrectValues() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> filteredStreamId = ComposedStreams.filteredStream(sourceStreamId, val -> val == 1);
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(filteredStreamId);
        assertThat(subscriber.getValues()).hasSize(1).contains(1);
    }

    @Test
    public void testEqualityOfFilteredStreamIdOnEqualStreams() {
        StreamId<Object> sourceStreamId = DUMMY_STREAM_ID_1;
        Predicate<Object> predicate = o -> true;
        StreamId<Object> filteredStream1 = ComposedStreams.filteredStream(sourceStreamId, predicate);
        StreamId<Object> filteredStream2 = ComposedStreams.filteredStream(sourceStreamId, predicate);
        assertThat(filteredStream1).isEqualTo(filteredStream2);
    }

    @Test
    public void testEqualityOfFilteredStreamIdOnNotEqualStreamSources() {
        Predicate<Object> predicate = o -> true;
        StreamId<Object> filteredStream1 = ComposedStreams.filteredStream(DUMMY_STREAM_ID_1, predicate);
        StreamId<Object> filteredStream2 = ComposedStreams.filteredStream(DUMMY_STREAM_ID_2, predicate);
        assertThat(filteredStream1).isNotEqualTo(filteredStream2);
    }

    @Test
    public void testEqualityOfFilteredStreamIdOnNotEqualPredicates() {
        StreamId<Object> sourceStreamId = DUMMY_STREAM_ID_1;
        StreamId<Object> filteredStream1 = ComposedStreams.filteredStream(sourceStreamId, o -> true);
        StreamId<Object> filteredStream2 = ComposedStreams.filteredStream(sourceStreamId, o -> true);
        assertThat(filteredStream1).isNotEqualTo(filteredStream2);
    }

    @Test(expected = NullPointerException.class)
    public void testDelayedStreamWithNullSourceStreamId() {
        ComposedStreams.delayedStream(null, Duration.ZERO);
    }

    @Test(expected = NullPointerException.class)
    public void testDelayedStreamWithNullDuration() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> empty()).withUniqueStreamId();
        ComposedStreams.delayedStream(sourceStreamId, null);
    }

    @Test
    public void testDelayedStreamWithCorrectValues() {
        final long delay = 2000;
        final long deltaDelay = 500;
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> just(1)).withUniqueStreamId();
        StreamId<Integer> delayedStreamId = ComposedStreams.delayedStream(sourceStreamId, Duration.ofMillis(delay));
        BlockingTestSubscriber<Integer> subscriber = BlockingTestSubscriber.ofName("subscriber");
        publisherFrom(delayedStreamId).subscribe(subscriber);

        Instant before = Instant.now();
        subscriber.await();
        Instant after = Instant.now();

        assertThat(subscriber.getValues()).hasSize(1).containsExactly(1);
        assertThat(Duration.between(before, after).toMillis()).isBetween(delay - deltaDelay, delay + deltaDelay);
    }

    @Test
    public void testEqualityOfDelayedStreamIdOnEqualStreams() {
        StreamId<Object> sourceStreamId = DUMMY_STREAM_ID_1;
        StreamId<Object> delayedStream1 = ComposedStreams.delayedStream(sourceStreamId, Duration.ZERO);
        StreamId<Object> delayedStream2 = ComposedStreams.delayedStream(sourceStreamId, Duration.ZERO);
        assertThat(delayedStream1).isEqualTo(delayedStream2);
    }

    @Test
    public void testEqualityOfDelayedStreamIdOnNotEqualStreamSources() {
        StreamId<Object> delayedStream1 = ComposedStreams.delayedStream(DUMMY_STREAM_ID_1, Duration.ZERO);
        StreamId<Object> delayedStream2 = ComposedStreams.delayedStream(DUMMY_STREAM_ID_2, Duration.ZERO);
        assertThat(delayedStream1).isNotEqualTo(delayedStream2);
    }

    @Test
    public void testEqualityOfDelayedStreamIdOnNotEqualDurations() {
        StreamId<Object> sourceStreamId = DUMMY_STREAM_ID_1;
        StreamId<Object> delayedStream1 = ComposedStreams.delayedStream(sourceStreamId, Duration.ofNanos(0));
        StreamId<Object> delayedStream2 = ComposedStreams.delayedStream(sourceStreamId, Duration.ofNanos(1));
        assertThat(delayedStream1).isNotEqualTo(delayedStream2);
    }

    @Test(expected = NullPointerException.class)
    public void testZippedStreamWithNullLeftStreamSourceId() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> empty()).withUniqueStreamId();
        ComposedStreams.zippedStream(null, sourceStreamId, (val1, val2) -> Optional.empty());
    }

    @Test(expected = NullPointerException.class)
    public void testZippedStreamWithNullRightStreamSourceId() {
        StreamId<Integer> sourceStreamId = provide(Observable.<Integer> empty()).withUniqueStreamId();
        ComposedStreams.zippedStream(sourceStreamId, null, (val1, val2) -> Optional.empty());
    }

    @Test(expected = NullPointerException.class)
    public void testZippedStreamWithNullZipFunction() {
        StreamId<Integer> sourceStreamId1 = provide(Observable.<Integer> empty()).withUniqueStreamId();
        StreamId<Integer> sourceStreamId2 = provide(Observable.<Integer> empty()).withUniqueStreamId();
        ComposedStreams.zippedStream(sourceStreamId1, sourceStreamId2, null);
    }

    @Test
    public void testZippedStreamWithZipThatAlwaysReturns() {
        StreamId<Integer> sourceStreamId1 = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> sourceStreamId2 = provide(Observable.<Integer> just(2, 4)).withUniqueStreamId();
        StreamId<Integer> zippedStreamId = ComposedStreams.zippedStream(sourceStreamId1, sourceStreamId2,
                (val1, val2) -> Optional.<Integer> of(val1 + val2));
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(zippedStreamId);
        assertThat(subscriber.getValues()).hasSize(2).containsExactly(3, 7);
    }

    @Test
    public void testZippedStreamWithZipThatDoesNotAlwaysReturns() {
        StreamId<Integer> sourceStreamId1 = provide(Observable.<Integer> just(1, 3)).withUniqueStreamId();
        StreamId<Integer> sourceStreamId2 = provide(Observable.<Integer> just(2, 4)).withUniqueStreamId();
        StreamId<Integer> zippedStreamId = ComposedStreams.zippedStream(sourceStreamId1, sourceStreamId2,
                (val1, val2) -> (val1 == 1) ? Optional.<Integer> empty() : Optional.<Integer> of(val1 + val2));
        BlockingTestSubscriber<Integer> subscriber = createSubscriberAndWait(zippedStreamId);
        assertThat(subscriber.getValues()).hasSize(1).containsExactly(7);
    }

    @Test
    public void testEqualityOfZippedStreamIdOnEqualStreams() {
        StreamId<Object> sourceStreamId1 = DUMMY_STREAM_ID_1;
        StreamId<Object> sourceStreamId2 = DUMMY_STREAM_ID_2;
        BiFunction<Object, Object, Optional<Object>> conversion = (o1, o2) -> Optional.empty();
        StreamId<Object> zippedStream1 = ComposedStreams.zippedStream(sourceStreamId1, sourceStreamId2, conversion);
        StreamId<Object> zippedStream2 = ComposedStreams.zippedStream(sourceStreamId1, sourceStreamId2, conversion);
        assertThat(zippedStream1).isEqualTo(zippedStream2);
    }

    @Test
    public void testEqualityOfZippedStreamIdOnNotEqualStreamSources() {
        BiFunction<Object, Object, Optional<Object>> conversion = (o1, o2) -> Optional.empty();
        StreamId<Object> zippedStream1 = ComposedStreams.zippedStream(DUMMY_STREAM_ID_1, DUMMY_STREAM_ID_1, conversion);
        StreamId<Object> zippedStream2 = ComposedStreams.zippedStream(DUMMY_STREAM_ID_2, DUMMY_STREAM_ID_2, conversion);
        assertThat(zippedStream1).isNotEqualTo(zippedStream2);
    }

    @Test
    public void testEqualityOfZippedStreamIdOnNotEqualConverions() {
        StreamId<Object> sourceStreamId1 = DUMMY_STREAM_ID_1;
        StreamId<Object> sourceStreamId2 = DUMMY_STREAM_ID_2;
        StreamId<Object> zippedStream1 = ComposedStreams.zippedStream(sourceStreamId1, sourceStreamId2,
                (o1, o2) -> Optional.empty());
        StreamId<Object> zippedStream2 = ComposedStreams.zippedStream(sourceStreamId1, sourceStreamId2,
                (o1, o2) -> Optional.empty());
        assertThat(zippedStream1).isNotEqualTo(zippedStream2);
    }

    private final BlockingTestSubscriber<Integer> createSubscriberAndWait(StreamId<Integer> sourceStreamId) {
        BlockingTestSubscriber<Integer> subscriber = BlockingTestSubscriber.ofName("subscriber");
        publisherFrom(sourceStreamId).subscribe(subscriber);
        subscriber.await();
        return subscriber;
    }
}
