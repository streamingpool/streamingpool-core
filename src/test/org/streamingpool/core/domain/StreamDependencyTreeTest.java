package org.streamingpool.core.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.streamingpool.core.service.StreamId;

public class StreamDependencyTreeTest {

    @Test
    public void test() {
        StreamStreamDependencyTreeImpl graph = new StreamStreamDependencyTreeImpl();
        StreamId SOURCE = Mockito.mock(StreamId.class);
        StreamId ANCESTOR_1 = Mockito.mock(StreamId.class);
        StreamId ANCESTOR_2 = Mockito.mock(StreamId.class);
        graph.addDependency(SOURCE, ANCESTOR_1);
        graph.addDependency(SOURCE, ANCESTOR_2);

        Assertions.assertThat(graph.getAncestorsFrom(SOURCE)).containsOnlyOnce(SOURCE, ANCESTOR_1, ANCESTOR_2);
    }

    @Test
    public void test2(){
        StreamStreamDependencyTreeImpl graph = new StreamStreamDependencyTreeImpl();
        StreamId SOURCE_1 = Mockito.mock(StreamId.class);
        StreamId SOURCE_2 = Mockito.mock(StreamId.class);
        StreamId ANCESTOR = Mockito.mock(StreamId.class);
        graph.addDependency(SOURCE_1, ANCESTOR);
        graph.addDependency(SOURCE_2, ANCESTOR);

        Assertions.assertThat(graph.getAncestorsFrom(SOURCE_1)).doesNotContain(SOURCE_2);
        Assertions.assertThat(graph.getAncestorsFrom(SOURCE_2)).doesNotContain(SOURCE_1);
    }

}