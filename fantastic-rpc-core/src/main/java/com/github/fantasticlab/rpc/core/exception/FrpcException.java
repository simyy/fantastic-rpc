package com.github.fantasticlab.rpc.core.exception;

public class FrpcException extends RuntimeException {

    public FrpcException(String message, Throwable cause) {
        super(message, cause);
    }
}
