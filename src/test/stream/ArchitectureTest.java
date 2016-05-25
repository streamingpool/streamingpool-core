/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package stream;

import static org.assertj.core.api.Assertions.assertThat;
import static stream.ReactStreams.fromRx;
import static stream.ReactStreams.rxFrom;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import conf.SpringContext;
import rx.Observable;
import stream.impl.NamedStreamId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContext.class, loader = AnnotationConfigContextLoader.class)
public class ArchitectureTest extends StreamProcessingSupport {

    private static final String ANY_NAME = "";
    private static final List<Integer> INTEGER_SOURCE_ITEMS = Arrays.asList(1, 3, 5, 11);

    @Test
    public void testElementsAreSentAndReceived() {
        final StreamId<Integer> id = new NamedStreamId<Integer>(ANY_NAME);

        ReactStream<Integer> reactStream = fromRx(prepareRxStreamWith(INTEGER_SOURCE_ITEMS));
        provide(reactStream).as(id);

        final int result = rxFrom(discover(id)).reduce(Math::addExact).toBlocking().single();
        final int expected = INTEGER_SOURCE_ITEMS.stream().mapToInt(i -> i.intValue()).sum();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testRepublish() {
        final StreamId<Integer> idA = new NamedStreamId<Integer>("idA");
        final StreamId<Integer> idB = new NamedStreamId<Integer>("idB");

        // Original stream
        Observable<Integer> sourceStream = prepareRxStreamWith(INTEGER_SOURCE_ITEMS);
        ReactStream<Integer> reactSourceStream = fromRx(sourceStream);
        provide(reactSourceStream).as(idA);

        // Discover + re-provide
        ReactStream<Integer> reactStreamB = fromRx(rxFrom(discover(idA)).map(value -> value * 2));
        provide(reactStreamB).as(idB);

        // Discover
        final int result = rxFrom(discover(idB)).reduce(Math::addExact).toBlocking().single();
        final int expected = INTEGER_SOURCE_ITEMS.stream().mapToInt(i -> i.intValue()).sum() * 2;

        assertThat(result).isEqualTo(expected);

    }

    private static <T> Observable<T> prepareRxStreamWith(List<T> items) {
        return Observable.from(items);
    }

}
