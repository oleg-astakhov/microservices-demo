package com.olegastakhov.microservices.quiz.infrastructure.validation;

/**
 * Throw this exception for business-like errors. All errors thrown
 * using this class will be shown to the user. Do not throw
 * any technical errors that the user cannot do anything about.
 */

public class ServiceValidationException extends RuntimeException {
    private final String messageKey;
    private final String[] args;

    public ServiceValidationException(String messageKey, String... args) {
        this.messageKey = messageKey;
        this.args = args;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getArgs() {
        return args;
    }
}
