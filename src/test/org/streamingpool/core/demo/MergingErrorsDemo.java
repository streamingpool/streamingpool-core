package org.streamingpool.core.demo;

import io.reactivex.Flowable;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamid.DerivedStreamId;
import org.streamingpool.core.service.streamid.MergedErrorStreamId;
import org.streamingpool.core.support.RxStreamSupport;
import org.streamingpool.core.testing.AbstractStreamTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.streamingpool.core.service.streamid.DerivedStreamId.derive;

@Ignore("just a try")
public class MergingErrorsDemo extends AbstractStreamTest implements RxStreamSupport {

    @Test
    public void testDependencyGraphIsCorrectlyCreated() throws IOException {
        StreamId<Long> SOURCE_1 = source();
        DerivedStreamId<Long, String> DERIVED_1_A = derive(SOURCE_1, s -> {
            if(s % 2 == 0)
                throw new RuntimeException("Error A");
            return s + " derived 1 A";
        });
        DerivedStreamId<String, String> LEAF_1 = derive(DERIVED_1_A, s -> s + " derived 2 A");

        StreamId<Long> SOURCE_2 = source();
        DerivedStreamId<Long, String> DERIVED_2_A = derive(SOURCE_2, s -> {
            if(s % 2 == 0)
                throw new RuntimeException("Error B");
            return s + " derived 1 B";
        });
        DerivedStreamId<String, String> LEAF_2 = derive(DERIVED_2_A, s -> s + " derived 2 B");

        rxFrom(LEAF_1).subscribe(System.out::println);
        rxFrom(LEAF_2).subscribe(System.out::println);

        rxFrom(MergedErrorStreamId.mergeErrorsStartingFrom(LEAF_1)).subscribe(System.err::println);
        rxFrom(MergedErrorStreamId.mergeErrorsStartingFrom(LEAF_2)).subscribe(System.err::println);

        Assertions.assertThat(getAncestorsFrom(LEAF_1)).containsOnlyOnce(SOURCE_1, DERIVED_1_A, LEAF_1);
        Assertions.assertThat(getAncestorsFrom(LEAF_2)).containsOnlyOnce(SOURCE_2, DERIVED_2_A, LEAF_2);

        System.in.read();
    }

    private StreamId<Long> source() {
        return provide(Flowable.interval(1, TimeUnit.SECONDS)).withUniqueStreamId();
    }

}
