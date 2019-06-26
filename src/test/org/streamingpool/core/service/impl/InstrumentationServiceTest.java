package org.streamingpool.core.service.impl;

import io.reactivex.Flowable;
import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.DerivedStreamId;
import org.streamingpool.core.testing.AbstractStreamTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.streamingpool.core.service.streamid.DerivedStreamId.derive;

public class InstrumentationServiceTest extends AbstractStreamTest {

    @Test
    public void testDependencyGraphIsCorrectlyCreated() {
        StreamId<Long> SOURCE_1 = source();
        DerivedStreamId<Long, String> DERIVED_1_A = derive(SOURCE_1, s -> s + " derived 1 A");
        DerivedStreamId<String, String> LEAF_1 = derive(DERIVED_1_A, s -> s + " derived 2 A");

        StreamId<Long> SOURCE_2 = source();
        DerivedStreamId<Long, String> DERIVED_2_A = derive(SOURCE_2, s -> s + " derived 1 B");
        DerivedStreamId<String, String> LEAF_2 = derive(DERIVED_2_A, s -> s + " derived 2 B");

        discover(LEAF_1);
        discover(LEAF_2);

        assertThat(getAncestorsFrom(LEAF_1)).containsOnlyOnce(SOURCE_1, DERIVED_1_A, LEAF_1);
        assertThat(getAncestorsFrom(LEAF_2)).containsOnlyOnce(SOURCE_2, DERIVED_2_A, LEAF_2);
    }

    @Test
    public void testDependencyGraphWithTheSharedSourceIsCorrectlyCreated() {
        StreamId<Long> SOURCE = source();
        DerivedStreamId<Long, String> DERIVED_1_A = derive(SOURCE, s -> s + " derived 1 A");
        DerivedStreamId<String, String> LEAF_1 = derive(DERIVED_1_A, s -> s + " derived 2 A");


        DerivedStreamId<Long, String> DERIVED_2_A = derive(SOURCE, s -> s + " derived 1 B");
        DerivedStreamId<String, String> LEAF_2 = derive(DERIVED_2_A, s -> s + " derived 2 B");

        discover(LEAF_1);
        discover(LEAF_2);

        assertThat(getAncestorsFrom(LEAF_1)).containsOnlyOnce(SOURCE, DERIVED_1_A, LEAF_1);
        assertThat(getAncestorsFrom(LEAF_2)).containsOnlyOnce(SOURCE, DERIVED_2_A, LEAF_2);
    }

    private StreamId<Long> source() {
        return provide(Flowable.just(1L)).withUniqueStreamId();
    }

}
