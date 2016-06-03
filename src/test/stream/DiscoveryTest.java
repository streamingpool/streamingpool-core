/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import stream.impl.CycleInStreamDiscoveryDetectedException;
import stream.impl.LazyPool;

public class DiscoveryTest {

 // TODO move to another test
    @SuppressWarnings("unchecked")
    @Test(expected = CycleInStreamDiscoveryDetectedException.class)
    public void testDiscoveryLoop() {
        StreamFactory factory = mock(StreamFactory.class);
        when(factory.create(any(), any())).thenAnswer((args) -> {
            final StreamId<Object> streamId = args.getArgumentAt(0, StreamId.class);
            final DiscoveryService discovery = args.getArgumentAt(1, DiscoveryService.class);

            return discovery.discover(streamId);
        });

        new LazyPool(singletonList(factory)).discover(mock(StreamId.class));
    }
    
    class DummyStream implements ReactStream<Object> {
        private final Optional<ReactStream<?>> parent;
        private final String name;
        
        DummyStream(String name) {
            this.parent = Optional.empty();
            this.name = name;
        }

        DummyStream(ReactStream<?> parent, String name) {
            this.parent = Optional.of(parent);
            this.name = name;
        }
        
        @Override
        public String toString() {
            return parent.map(Object::toString).map(str -> str + " -> ").orElse("") + name;
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testDiscoveryXYZ() {
        StreamId<?> idX = mock(StreamId.class);
        StreamId<?> idY = mock(StreamId.class);
        StreamId<?> idZ = mock(StreamId.class);
        
        StreamFactory factoryA = mock(StreamFactory.class);
        when(factoryA.create(any(), any())).thenAnswer((args) -> {
            StreamId<Object> streamId = args.getArgumentAt(0, StreamId.class);
            if(streamId != idY) {
                return null;
            }
            final DiscoveryService discovery = args.getArgumentAt(1, DiscoveryService.class);
            return new DummyStream(discovery.discover(idZ), "Y");
        });
        
        StreamFactory factoryB = mock(StreamFactory.class);
        when(factoryB.create(any(), any())).thenAnswer((args) -> {
            StreamId<Object> streamId = args.getArgumentAt(0, StreamId.class);
            DiscoveryService discovery = args.getArgumentAt(1, DiscoveryService.class);
            if(streamId == idY) {
                return null;
            } else if(streamId == idX) {
                return new DummyStream(discovery.discover(idY), "X");
            } else {
                return new DummyStream("Z");
            }
        });
        
        ReactStream<?> result = new LazyPool(Arrays.asList(factoryA, factoryB)).discover(idX);
        
        assertEquals("Z -> Y -> X", result.toString());
    }

}
