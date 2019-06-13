package org.streamingpool.core.domain;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.streamingpool.core.service.StreamId;

public class DependencyGraphTest {

    @Test
    public void test() {
        DependencyGraphImpl graph = new DependencyGraphImpl();
        StreamId SOURCE = Mockito.mock(StreamId.class);
        StreamId ANCESTOR_1 = Mockito.mock(StreamId.class);
        StreamId ANCESTOR_2 = Mockito.mock(StreamId.class);
        graph.addDependency(SOURCE, ANCESTOR_1);
        graph.addDependency(SOURCE, ANCESTOR_2);

        Assertions.assertThat(graph.getSubgraphStartingFrom(SOURCE)).containsOnlyOnce(SOURCE, ANCESTOR_1, ANCESTOR_2);
    }

}