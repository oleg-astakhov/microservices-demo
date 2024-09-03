package com.olegastakhov.microservices.healthcheck.exception;

public class NotHealthyException extends RuntimeException {
    public NotHealthyException(String message) {
        super(message);
    }
}
