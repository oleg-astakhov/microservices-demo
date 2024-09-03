package com.olegastakhov.microservices.util

import com.olegastakhov.microservices.Environment
import com.olegastakhov.microservices.util.httpresponse.E2EMapResponseBody
import com.olegastakhov.microservices.util.httpresponse.StatusCodeAware
import org.apache.commons.lang3.StringUtils
import org.spockframework.util.Assert

import java.util.function.Supplier

class Helper {
    static String GENERIC_INTERNAL_ERROR_MESSAGE_TO_USER = "Ouch, we are sorry, something went wrong. We have been notified about this incident and our engineers are on it. Please try again at a later time or contact our tech support if the problem persists for a long time."

    static String extractCurrentNonBlankStringProperty(final String property,
                                                       final Map<String, Object> params) {
        String propertyValue = extractCurrentNonNullProperty(String, property, params)
        if (StringUtils.isBlank(propertyValue)) {
            throw new IllegalArgumentException("${property} is blank when not expected")
        }
        return propertyValue
    }

    static <T> T extractCurrentNonNullProperty(final Class<T> clazz,
                                               final String property,
                                               final Map<String, Object> params) {
        Assert.notNull(params, "params is null when not expected")
        T propertyValue = params[property]
        Assert.notNull(propertyValue, "${property} is null when not expected")
        return propertyValue
    }

    static void assertForStatus(final int statusCode,
                                final StatusCodeAware statusCodeAwareToCheck,
                                final Map<String, Object> params = new HashMap<>()) {
        final Boolean isAssertForStatus = isAssertForStatus(statusCode, params)
        if (isAssertForStatus) {
            if (200 == statusCode) {
                assert (statusCodeAwareToCheck.getStatusCode() == statusCode || statusCodeAwareToCheck.getStatusCode() == 204)
            } else {
                assert statusCodeAwareToCheck.getStatusCode() == statusCode
            }

        }
    }



    static void shutdownAllHttpClients(final Map<String, Object> params = new HashMap<>()) {
        shutdownHttpClientForAjaxJson(params)
        shutdownHttpClientsWhichAreBeingCopiedFromIntoWorkingPlaceholders(params)
    }

    static void shutdownHttpClientForAjaxJson(final Map<String, Object> params = new HashMap<>()) {
        shutdownHttpClient("ns_ajaxJsonHttpClient_instance", params)
    }

    static void shutdownHttpClientsWhichAreBeingCopiedFromIntoWorkingPlaceholders(final Map<String, Object> params = new HashMap<>()) {
        shutdownHttpClient("ns_public_ajaxJsonHttpClient_instance", params)
    }

    static void shutdownHttpClient(final String mapKey,
                                   final Map<String, Object> params = new HashMap<>()) {
        E2EHttpClient httpClient = params.get(mapKey) as E2EHttpClient
        if (null != httpClient) {
            httpClient.shutdown()
            params.remove(mapKey)
        }
    }

    static <T> T getByPolling(final Supplier<T> supplier) {
        ObjectWrapper<T> row = new ObjectWrapper<>(null)
        Environment.getInstance().getPollingConditions().eventually {
            T result = supplier.get()
            assert null != result
            row.setValue(result)
        }
        return row.getValue()
    }

    static void poll(Runnable runnable) {
        Environment.getInstance().getPollingConditions().eventually {
            runnable.run();
        }
    }

    static boolean isAssertForStatus(final int statusCode,
                                     final Map<String, Object> params = new HashMap<>()) {
        return putDefaultOrGetActual(params, "ns_httpStatus_assertFor" + statusCode, { Boolean.TRUE })
    }

    static putAsIndividualParams(final E2EMapResponseBody response,
                                 final Map<String, Object> params = new HashMap<>()) {
        // basically this is a JSON type of response
        params.put("ns_httpResponse_responseBodyAsMap", response.responseBody)
        params.put("ns_httpResponse_httpResponse", response.httpResponse)
        params.put("ns_httpResponse_httpResponseDecorator", response)
    }

    static <T> T putDefaultOrGetActual(final Map<String, T> params,
                                       final String key,
                                       final Supplier<T> valueSupplier) {
        Assert.notNull(params)
        final T existingValue = params.get(key)
        if (null != existingValue) {
            return existingValue
        }
        final T val = valueSupplier.get()
        params.putIfAbsent(key, val)
        return val
    }

    static String getRandomUsername() {
        return UUID.randomUUID().toString();
    }
}
