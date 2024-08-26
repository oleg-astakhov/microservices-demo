package com.olegastakhov.microservices.gamification.infrastructure.validation;


import com.olegastakhov.microservices.gamification.infrastructure.localization.LocalizationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String JSON_MESSAGE_KEY = "message";

    @Autowired
    LocalizationServiceImpl localization;

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleBindingValidationExceptions(final WebExchangeBindException ex) {

        final BindingResult bindingResult = ex.getBindingResult();
        final Map<String, String> errors = new HashMap<>();

        if (bindingResult.hasFieldErrors()) {
            final FieldError fieldError = bindingResult.getFieldErrors().getFirst();
            errors.put(JSON_MESSAGE_KEY, fieldError.getDefaultMessage());
            return Mono.just(new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST));
        }

        if (bindingResult.hasGlobalErrors()) {
            errors.put(JSON_MESSAGE_KEY, bindingResult.getGlobalErrors().getFirst().getDefaultMessage());
            return Mono.just(new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST));
        }

        return Mono.just(new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> notFoundException(Exception ex, ServerWebExchange exchange, Locale locale) {
        String requestPath = exchange.getRequest().getPath().pathWithinApplication().value();
        log.warn(String.format("Non existing path [%s] has been requested", requestPath));
        String errorMessage = localization.getLocalizedMessage("common.warn.resourceNotFound", locale, requestPath);
        final Map<String, String> errors = new HashMap<>();
        errors.put(JSON_MESSAGE_KEY, errorMessage);
        return Mono.just(new ResponseEntity<>(errors, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGenericException(Exception ex, ServerWebExchange exchange, Locale locale) {
        log.error(ex.getMessage(), ex);
        String errorMessage = localization.getLocalizedMessage("common.error.runtimeException", locale);
        final Map<String, String> errors = new HashMap<>();
        errors.put(JSON_MESSAGE_KEY, errorMessage);
        return Mono.just(new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
