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

package cern.streaming.pool.core.domain;

import static java.util.Objects.requireNonNull;

import java.time.Instant;

public class Timestamped<T> {

    private final T value;
    private final Instant instant;

    private Timestamped(Instant instant, T value) {
        this.value = requireNonNull(value, "value must not be null");
        this.instant = requireNonNull(instant, "instant must not be null");
    }

    public static <T> Timestamped<T> atOf(Instant instant, T value) {
        return new Timestamped<>(instant, value);
    }

    public static <T> Timestamped<T> nowOf(T value) {
        return atOf(Instant.now(), value);
    }

    public T value() {
        return value;
    }

    public Instant instant() {
        return instant;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((instant == null) ? 0 : instant.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Timestamped<?> other = (Timestamped<?>) obj;
        if (instant == null) {
            if (other.instant != null) {
                return false;
            }
        } else if (!instant.equals(other.instant)) {
            return false;
        }
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return value + "@" + instant;
    }

}
