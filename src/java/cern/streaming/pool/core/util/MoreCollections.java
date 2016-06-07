/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MoreCollections {

    private MoreCollections() {
        /* only static methods */
    }

    public static final <T> List<T> emptyIfNull(List<T> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }

    public static final <T> Set<T> emptyIfNull(Set<T> set) {
        if (set == null) {
            return Collections.emptySet();
        }
        return set;
    }

}
