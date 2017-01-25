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

package org.streamingpool.core.service.streamid;

import static java.util.Objects.requireNonNull;

import java.time.Duration;

import org.streamingpool.core.service.StreamId;
import org.streamingpool.core.service.streamfactory.DelayedStreamFactory;

/**
 * Delay the items emitted by the stream created with the target {@link StreamId} by the specified {@link Duration}
 * 
 * @see DelayedStreamFactory
 * @author acalia
 * @param <T> type of the original data stream
 */
public class DelayedStreamId<T> implements StreamId<T> {

    private final StreamId<T> target;
    private final Duration delay;

    public static <T> DelayedStreamId<T> delayBy(StreamId<T> target, Duration delay) {
        return new DelayedStreamId<>(target, delay);
    }

    public DelayedStreamId(StreamId<T> target, Duration delay) {
        this.target = requireNonNull(target, "target of the delay must not be null");
        this.delay = requireNonNull(delay, "delay must not be null");
    }

    public StreamId<T> getTarget() {
        return target;
    }

    public Duration getDelay() {
        return delay;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delay == null) ? 0 : delay.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DelayedStreamId<?> other = (DelayedStreamId<?>) obj;
        if (delay == null) {
            if (other.delay != null) {
                return false;
            }
        } else if (!delay.equals(other.delay)) {
            return false;
        }
        if (target == null) {
            if (other.target != null) {
                return false;
            }
        } else if (!target.equals(other.target)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DelayedStreamId [target=" + target + ", delay=" + delay + "]";
    }

}
