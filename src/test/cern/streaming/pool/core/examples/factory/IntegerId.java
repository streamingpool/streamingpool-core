/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.factory;

import cern.streaming.pool.core.service.StreamId;

public class IntegerId implements StreamId<Integer> {

    private final int from;
    private final int to;

    public IntegerId(int from, int to) {
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
