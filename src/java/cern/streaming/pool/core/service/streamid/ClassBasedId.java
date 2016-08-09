/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.service.streamid;

import cern.streaming.pool.core.service.StreamId;

public class ClassBasedId <T> implements StreamId<T> {
    
    private final Class<?> targetClass;

    private ClassBasedId(Class<?> targetClass) {
        this.targetClass = targetClass;
    }
    
    public static <T> ClassBasedId<T> of(Class<?> targetClass) {
        return new ClassBasedId<>(targetClass);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((targetClass == null) ? 0 : targetClass.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        ClassBasedId other = (ClassBasedId) obj;
        if (targetClass == null) {
            if (other.targetClass != null)
                return false;
        } else if (!targetClass.equals(other.targetClass))
            return false;
        return true;
    }
    

}
