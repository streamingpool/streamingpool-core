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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

/**
 * Created by timartin on 06/10/2016.
 */
public class DelayCompositionFunction<X> implements Function<List<Publisher<X>>, Publisher<X>> {

    private final Duration duration;

    public DelayCompositionFunction(Duration duration) {
        Objects.requireNonNull(duration, "duration");
        this.duration = duration;
    }

    @Override
    public Publisher<X> apply(List<Publisher<X>> reactiveStreams) {
        return Flowable.fromPublisher(reactiveStreams.get(0)).delay(duration.toMillis(), MILLISECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DelayCompositionFunction<?> that = (DelayCompositionFunction<?>) o;

        return duration.equals(that.duration);

    }

    @Override
    public int hashCode() {
        return duration.hashCode();
    }
}
