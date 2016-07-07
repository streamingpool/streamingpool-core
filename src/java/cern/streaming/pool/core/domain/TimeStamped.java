/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.domain;

import static java.util.Objects.requireNonNull;

import java.time.Instant;

public class TimeStamped<T> {

    private final T value;
    private final Instant instant;

    private TimeStamped(Instant instant, T value) {
        this.value = requireNonNull(value, "value must not be null");
        this.instant = requireNonNull(instant, "instant must not be null");
    }

    public static <T> TimeStamped<T> atOf(Instant instant, T value) {
        return new TimeStamped<>(instant, value);
    }

    public static <T> TimeStamped<T> nowOf(T value) {
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
        TimeStamped<?> other = (TimeStamped<?>) obj;
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
