package com.olegastakhov.microservices.quiz.infrastructure.validation;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
