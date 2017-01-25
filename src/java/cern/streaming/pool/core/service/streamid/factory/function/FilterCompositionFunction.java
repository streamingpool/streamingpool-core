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
package cern.streaming.pool.core.service.streamid.factory.function;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

/**
 * Created by timartin on 06/10/2016.
 */
public class FilterCompositionFunction<X> implements Function<List<Publisher<X>>, Publisher<X>> {

    private final Predicate<X> predicate;

    public FilterCompositionFunction(Predicate<X> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.predicate = predicate;
    }

    @Override
    public Publisher<X> apply(List<Publisher<X>> reactiveStreams) {
        return Flowable.fromPublisher(reactiveStreams.get(0)).filter(predicate::test);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FilterCompositionFunction<?> that = (FilterCompositionFunction<?>) o;

        return predicate.equals(that.predicate);

    }

    @Override
    public int hashCode() {
        return predicate.hashCode();
    }
}
