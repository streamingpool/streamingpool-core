// @formatter:off
/**
*
* This file is part of streaming pool (http://www.streamingpool.org).
* 
* Copyright (c) 2017-present, CERN. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* 
*/
// @formatter:on
package cern.streaming.pool.core.service.streamfactory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;

import cern.streaming.pool.core.service.DiscoveryService;
import cern.streaming.pool.core.service.StreamId;
import cern.streaming.pool.core.service.streamid.CompositionStreamId;
import cern.streaming.pool.core.support.RxStreamSupport;
import cern.streaming.pool.core.testing.AbstractStreamTest;

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
    @SuppressWarnings("unchecked")
    public void testCreateWithNullDiscoveryService() {
        StreamId<Object> streamId = Mockito.mock(StreamId.class);
        compositionStreamFactory.create(streamId, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateWithWrongStreamIdType() {
        StreamId<Object> streamId = Mockito.mock(StreamId.class);
        DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        Optional<Publisher<Object>> optionalReactiveStream = compositionStreamFactory.create(streamId,
                discoveryService);
        assertThat(optionalReactiveStream).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreate() {
        DiscoveryService discoveryService = Mockito.mock(DiscoveryService.class);
        StreamId<Object> sourceStreamId = Mockito.mock(StreamId.class);

        Publisher<Object> sourceReactiveStream = Mockito.mock(Publisher.class);
        Publisher<Object> newReactiveStream = Mockito.mock(Publisher.class);
        Mockito.when(discoveryService.discover(sourceStreamId)).thenReturn(sourceReactiveStream);

        Function<List<Publisher<Object>>, Publisher<Object>> transformationFunction = Mockito.mock(
                Function.class);
        Mockito.when(transformationFunction.apply(Collections.singletonList(sourceReactiveStream)))
                .thenReturn(newReactiveStream);

        CompositionStreamId<Object, Object> compositionStreamId = new CompositionStreamId<>(sourceStreamId,
                transformationFunction);
        Optional<Publisher<Object>> optionalCompositionReactiveStream = compositionStreamFactory.create(
                compositionStreamId, discoveryService);

        assertThat(optionalCompositionReactiveStream).isPresent().contains(newReactiveStream);
        Mockito.verify(transformationFunction).apply(Collections.singletonList(sourceReactiveStream));
    }
}
