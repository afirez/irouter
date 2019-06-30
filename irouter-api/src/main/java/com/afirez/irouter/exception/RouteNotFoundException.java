package com.afirez.irouter.exception;

public class RouteNotFoundException extends RuntimeException {

    public RouteNotFoundException() {
    }

    public RouteNotFoundException(String message) {
        super(message);
    }
}
