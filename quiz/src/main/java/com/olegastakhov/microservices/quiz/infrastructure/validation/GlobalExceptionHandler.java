package com.olegastakhov.microservices.quiz.infrastructure.validation;

import com.olegastakhov.microservices.quiz.infrastructure.localization.LocalizationServiceImpl;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
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
    private LocalizationServiceImpl localizationService;

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

    @ExceptionHandler(ServiceValidationException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleServiceValidationException(ServiceValidationException ex, Locale locale) {
        String errorMessage = localizationService.getLocalizedMessage(ex.getMessageKey(), locale, ex.getArgs());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(JSON_MESSAGE_KEY, errorMessage);
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ExceptionHandler(MandatoryFieldNotInitializedOnClientException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleMandatoryFieldNotInitializedOnClientException(MandatoryFieldNotInitializedOnClientException ex, Locale locale) {
        log.error(ex.getMessage(), ex);
        String errorMessage = localizationService.getLocalizedMessage("common.error.runtimeException", locale);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put(JSON_MESSAGE_KEY, errorMessage);
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> notFoundException(Exception ex, ServerWebExchange exchange, Locale locale) {
        String requestPath = exchange.getRequest().getPath().pathWithinApplication().value();
        log.warn(String.format("Non existing path [%s] has been requested", requestPath));
        String errorMessage = localizationService.getLocalizedMessage("common.warn.resourceNotFound", locale, requestPath);
        final Map<String, String> errors = new HashMap<>();
        errors.put(JSON_MESSAGE_KEY, errorMessage);
        return Mono.just(new ResponseEntity<>(errors, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler({
            AmqpConnectException.class,
            ServiceUnavailableException.class,
            CallNotPermittedException.class})
    public Mono<ResponseEntity<Map<String, String>>> serviceUnavailable(Exception ex, ServerWebExchange exchange, Locale locale) {
        log.error(ex.getMessage(), ex);
        String errorMessage = localizationService.getLocalizedMessage("common.error.serviceUnavailable", locale);
        final Map<String, String> errors = new HashMap<>();
        errors.put(JSON_MESSAGE_KEY, errorMessage);
        return Mono.just(new ResponseEntity<>(errors, HttpStatus.SERVICE_UNAVAILABLE));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGenericException(Exception ex, ServerWebExchange exchange, Locale locale) {
        log.error(ex.getMessage(), ex);
        String errorMessage = localizationService.getLocalizedMessage("common.error.runtimeException", locale);
        final Map<String, String> errors = new HashMap<>();
        errors.put(JSON_MESSAGE_KEY, errorMessage);
        return Mono.just(new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
