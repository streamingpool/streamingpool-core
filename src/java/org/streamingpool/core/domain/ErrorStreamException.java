/**
 * Copyright (c) 2017 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.streamingpool.core.domain;

public class ErrorStreamException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ErrorStreamException() {
        super();
    }

    public ErrorStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorStreamException(String message) {
        super(message);
    }

    public ErrorStreamException(Throwable cause) {
        super(cause);
    }

}
