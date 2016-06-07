/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.demo.usecase.bis;

/**
 * Represents a user permit from the BIS
 * 
 * @author acalia
 */
final class UserPermit {
    private final RedundantPermitId label;
    private final boolean value;

    /**
     * @param permitId
     * @param value
     */
    public UserPermit(RedundantPermitId permitId, boolean value) {
        super();
        this.label = permitId;
        this.value = value;
    }

    public RedundantPermitId getPermitId() {
        return label;
    }

    public boolean isGiven() {
        return value;
    }

    @Override
    public String toString() {
        return "UserPermit [label=" + label + ", value=" + value + "]";
    }
}