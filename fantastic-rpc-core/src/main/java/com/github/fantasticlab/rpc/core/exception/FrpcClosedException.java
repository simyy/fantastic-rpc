package com.github.fantasticlab.rpc.core.exception;

public class FrpcClosedException extends FrpcException {
    public FrpcClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
