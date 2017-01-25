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
package org.streamingpool.core.service.streamid.factory.function;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

/**
 * Created by timartin on 06/10/2016.
 */
public class FlatMapCompositionFunction<X, T> implements Function<List<Publisher<X>>, Publisher<T>> {

    private final Function<X, Publisher<T>> flatMapConversion;

    public FlatMapCompositionFunction(Function<X, Publisher<T>> flatMapConversion) {
        Objects.requireNonNull(flatMapConversion);
        this.flatMapConversion = flatMapConversion;
    }

    @Override
    public Publisher<T> apply(List<Publisher<X>> reactiveStreams) {
        return Flowable.fromPublisher(reactiveStreams.get(0)).flatMap(flatMapConversion::apply);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FlatMapCompositionFunction<?, ?> that = (FlatMapCompositionFunction<?, ?>) o;

        return flatMapConversion.equals(that.flatMapConversion);

    }

    @Override
    public int hashCode() {
        return flatMapConversion.hashCode();
    }
}
