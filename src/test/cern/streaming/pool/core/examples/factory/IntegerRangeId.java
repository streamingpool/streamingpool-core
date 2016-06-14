/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.factory;

import cern.streaming.pool.core.service.StreamId;

public class IntegerRangeId implements StreamId<Integer> {

    private final int from;
    private final int to;

    public IntegerRangeId(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

}
