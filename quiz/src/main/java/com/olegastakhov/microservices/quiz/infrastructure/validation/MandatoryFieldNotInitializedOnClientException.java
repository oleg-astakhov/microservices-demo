package com.olegastakhov.microservices.quiz.infrastructure.validation;

/**
 * Throw this exception when you receive a request with uninitialized
 * fields that the user has no control over. This could happen if a
 * frontend developer forgot to initialize a mandatory property or
 * misnamed it. The error message the user receives will be generic,
 * avoiding any implication that the user is at fault for the missing
 * field.
 *
 * HTTP status code for such error will be 400: BAD_REQUEST
 */
public class MandatoryFieldNotInitializedOnClientException extends RuntimeException {
    public MandatoryFieldNotInitializedOnClientException(String message) {
        super(message);
    }
}
