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
import stream.impl.SimpleStreamId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringContext.class, loader = AnnotationConfigContextLoader.class)
public class RegistryTest extends StreamProcessingSupport {

    @Test
    public void test() {
        final StreamId<Integer> id = new SimpleStreamId<Integer>("Propertyname");

        List<Integer> sourceValues = Arrays.asList(1, 3, 5, 11);
        Observable<Integer> source = Observable.from(sourceValues);
        ReactStream<Integer> reactStream = fromRx(source);
        provide(reactStream).as(id);

        
        final int result = rxFrom(discover(id))
                .reduce(Math::addExact)
                .toBlocking()
                .single();
        final int expected = sourceValues.stream()
                .mapToInt(i -> i.intValue())
                .sum();

        assertThat(result).isEqualTo(expected);
    }

}
