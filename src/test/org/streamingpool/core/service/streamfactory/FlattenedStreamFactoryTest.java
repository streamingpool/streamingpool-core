package org.streamingpool.core.service.streamfactory;

import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.reactivestreams.Publisher;
import org.streamingpool.core.domain.ErrorStreamPair;
import org.streamingpool.core.service.DiscoveryService;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.FlattenedStreamId;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link FlattenedStreamFactory}.
 *
 * @author timartin
 */
@RunWith(MockitoJUnitRunner.class)
public class FlattenedStreamFactoryTest {
    private static final List<Integer> VALUES_WITHOUT_NULL = Arrays.asList(1, 2, 3, 4);
    private static final List<Integer> VALUES_WITH_NULL = Arrays.asList(1, 2, null, 4);
    private final TestSubscriber<Integer> testValuesSubscriber = new TestSubscriber<>();
    private final TestSubscriber<Throwable> testErrorsSubscriber = new TestSubscriber<>();
    private final FlattenedStreamFactory flattenedStreamFactory = new FlattenedStreamFactory();

    @Mock
    private StreamId<Iterable<? extends Integer>> sourceStreamId;
    @Mock
    private DiscoveryService discoveryService;

    @Test
    public void testCreateStream() {
        Publisher<Iterable<Integer>> sourceStream = Flowable.just(VALUES_WITHOUT_NULL);
        doReturn(sourceStream).when(discoveryService).discover(sourceStreamId);
        StreamId<Integer> flattenedStreamId = FlattenedStreamId.flatten(sourceStreamId);
        ErrorStreamPair<Integer> errorStreamPair = flattenedStreamFactory.create(flattenedStreamId, discoveryService);

        errorStreamPair.data().subscribe(testValuesSubscriber);
        errorStreamPair.error().subscribe(testErrorsSubscriber);
        testValuesSubscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);

        testValuesSubscriber.assertComplete();
        testValuesSubscriber.assertValueCount(VALUES_WITHOUT_NULL.size());
        testValuesSubscriber.assertValues(VALUES_WITHOUT_NULL.toArray(new Integer[VALUES_WITHOUT_NULL.size()]));
        testErrorsSubscriber.assertNoValues();
        testErrorsSubscriber.assertNotTerminated();
    }

    @Test
    public void testCreateStreamWithNullSourceValues() {
        Flowable<Iterable<Integer>> sourceStream = Flowable.just(VALUES_WITH_NULL);
        doReturn(sourceStream).when(discoveryService).discover(sourceStreamId);
        StreamId<Integer> flattenedStreamId = FlattenedStreamId.flatten(sourceStreamId);
        ErrorStreamPair<Integer> errorStreamPair = flattenedStreamFactory.create(flattenedStreamId, discoveryService);

        errorStreamPair.data().subscribe(testValuesSubscriber);
        errorStreamPair.error().subscribe(testErrorsSubscriber);
        testValuesSubscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);

        List<Integer> emittedValues = VALUES_WITH_NULL.stream().filter(Objects::nonNull).collect(Collectors.toList());
        testValuesSubscriber.assertComplete();
        testValuesSubscriber.assertValueCount(emittedValues.size());
        testValuesSubscriber.assertValues(emittedValues.toArray(new Integer[emittedValues.size()]));
        testErrorsSubscriber.assertNoValues();
        testErrorsSubscriber.assertNotTerminated();
    }

    @Test
    public void testOnErrorOnSourceStream() {
        Throwable throwable = new RuntimeException();
        Flowable<Integer> sourceStream = Flowable.error(throwable);
        doReturn(sourceStream).when(discoveryService).discover(sourceStreamId);
        StreamId<Integer> flattenedStreamId = FlattenedStreamId.flatten(sourceStreamId);
        ErrorStreamPair<Integer> errorStreamPair = flattenedStreamFactory.create(flattenedStreamId, discoveryService);

        errorStreamPair.data().subscribe(testValuesSubscriber);
        errorStreamPair.error().subscribe(testErrorsSubscriber);
        testValuesSubscriber.awaitTerminalEvent(1, TimeUnit.SECONDS);

        testValuesSubscriber.assertNoValues();
        testValuesSubscriber.assertError(throwable);
        testErrorsSubscriber.assertNoValues();
        testErrorsSubscriber.assertNotTerminated();
    }

    @Test
    public void testWrongStreamIdType() {
        ErrorStreamPair errorStreamPair = flattenedStreamFactory.create(mock(StreamId.class), discoveryService);
        assertThat(errorStreamPair.isPresent()).isFalse();
    }
}
