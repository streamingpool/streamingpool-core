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

import org.streamingpool.core.service.StreamId;

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
