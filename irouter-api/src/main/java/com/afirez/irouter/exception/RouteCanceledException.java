package com.afirez.irouter.exception;

public class RouteCanceledException extends RuntimeException {

    public RouteCanceledException() {
    }

    public RouteCanceledException(String message) {
        super(message);
    }
}
