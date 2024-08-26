package com.olegastakhov.microservices.quiz.infrastructure.validation;

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
