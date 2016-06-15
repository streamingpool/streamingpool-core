/**
 * Copyright (c) 2016 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package cern.streaming.pool.core.exception;

/**
 * Specific exception that indicates that a dependency cycle is detected when discovering a stream.
 * 
 * @author acalia
 */
public class CycleInStreamDiscoveryDetectedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CycleInStreamDiscoveryDetectedException() {
        super();
    }

    public CycleInStreamDiscoveryDetectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CycleInStreamDiscoveryDetectedException(String message) {
        super(message);
    }

    public CycleInStreamDiscoveryDetectedException(Throwable cause) {
        super(cause);
    }

}
