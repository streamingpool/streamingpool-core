/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.domain;

public class StreamException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public StreamException() {
        super();
    }

    public StreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public StreamException(String message) {
        super(message);
    }

    public StreamException(Throwable cause) {
        super(cause);
    }

}
