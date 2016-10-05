package cern.streaming.pool.core.service.streamfactory;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.ReactiveStream;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.CompositionStreamId;
import cern.streaming.pool.core.support.RxStreamSupport;
import cern.streaming.pool.core.testing.AbstractStreamTest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CompositionStreamFactory}.
 *
 * @author timartin
 */
public class CompositionStreamFactoryTest extends AbstractStreamTest implements RxStreamSupport {

    @Autowired
    private CompositionStreamFactory compositionStreamFactory;

    @Test
    public void testCreateWithNullStreamId() {
        DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        assertThat(compositionStreamFactory.create(null, discoveryService)).isEmpty();
    }

    @Test(expected = NullPointerException.class)
    public void testCreateWithNullDiscoveryService() {
        StreamId<Object> streamId = Mockito.mock(StreamId.class);
        compositionStreamFactory.create(streamId, null);
    }

    @Test
    public void testCreateWithWrongStreamIdType() {
        StreamId<Object> streamId = Mockito.mock(StreamId.class);
        DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        Optional<ReactiveStream<Object>> optionalReactiveStream = compositionStreamFactory.create(streamId,
                discoveryService);
        assertThat(optionalReactiveStream).isEmpty();
    }

    @Test
    public void testCreate() {
        DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        StreamId<Object> sourceStreamId = Mockito.mock(StreamId.class);

        ReactiveStream<Object> sourceReactiveStream = Mockito.mock(ReactiveStream.class);
        ReactiveStream<Object> newReactiveStream = Mockito.mock(ReactiveStream.class);
        Mockito.when(discoveryService.discover(sourceStreamId)).thenReturn(sourceReactiveStream);

        Function<List<ReactiveStream<Object>>, ReactiveStream<Object>> transformationFunction = Mockito.mock(
                Function.class);
        Mockito.when(transformationFunction.apply(Collections.singletonList(sourceReactiveStream)))
                .thenReturn(newReactiveStream);

        CompositionStreamId<Object, Object> compositionStreamId = new CompositionStreamId<>(sourceStreamId,
                transformationFunction);
        Optional<ReactiveStream<Object>> optionalCompositionReactiveStream = compositionStreamFactory.create(
                compositionStreamId, discoveryService);

        assertThat(optionalCompositionReactiveStream).isPresent().contains(newReactiveStream);
        Mockito.verify(transformationFunction).apply(Collections.singletonList(sourceReactiveStream));
    }
}
