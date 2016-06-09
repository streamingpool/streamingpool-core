/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.examples.creators;

public class InjectionDomainObject {

    private final String injectionName;

    public InjectionDomainObject(String injectionName) {
        this.injectionName = injectionName;
    }

    public String getInjectionName() {
        return injectionName;
    }

    @Override
    public String toString() {
        return "InjectionDomainObject [injectionName=" + injectionName + "]";
    }
    
}
