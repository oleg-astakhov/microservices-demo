package com.olegastakhov.microservices.healthcheck.processors.actuator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegastakhov.microservices.healthcheck.exception.ConfigurationException;
import com.olegastakhov.microservices.healthcheck.exception.NotHealthyException;
import com.olegastakhov.microservices.healthcheck.Util;
import com.olegastakhov.microservices.healthcheck.processors.ModeProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActuatorModeImpl implements ModeProcessor {
    private static final Logger log = LoggerFactory.getLogger(ActuatorModeImpl.class);

    public static final String MODE_ID = "actuator";
    private final static String DEFAULT_URL = "http://localhost:8080/actuator/health";
    private final static int DEFAULT_STATUS_CODE = 200;

    private static final String USAGE_INSTRUCTIONS = String.format("Usage: <cmd> %s <url> <status_code int>", MODE_ID);

    @Override
    public String getModeId() {
        return MODE_ID;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public void process(final List<String> arguments) {
        final Config config = parseConfig(arguments);
        final String body = getHttpResponseBody(config);
        final Map<String, Object> map = parseJsonAsMap(body, config);
        assertStatusUp(map);
    }

    private void assertStatusUp(Map<String, Object> map) {
        final String status = (String) map.get("status");
        if (null == status) {
            throw new NotHealthyException("Property 'status' is not found in parsed JSON");
        }

        final String expectedStatus = "UP";

        if (!expectedStatus.equals(status)) {
            throw new NotHealthyException(String.format("Non-expected status. Expecting: %s. Got: %s", expectedStatus, status));
        }
        log.info("Service is UP");
    }

    private Map<String, Object> parseJsonAsMap(final String body,
                                               final Config config) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(body, new TypeReference<HashMap<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new NotHealthyException(String.format("Could not parse JSON response from: %s", config.getHealthCheckUrl()));
        }
    }

    private String getHttpResponseBody(final Config config) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            final HttpResponse<String> response = Util.getInstance().getHttpResponse(config.getHealthCheckUrl(), client);
            if (response.statusCode() != config.getStatusCode()) {
                throw new NotHealthyException(String.format("HTTP status code not satisfied. Expecting: %s. Got: %s", config.getStatusCode(), response.statusCode()));
            }
            return response.body();
        }
    }

    private Config parseConfig(List<String> arguments) {
        if (arguments.isEmpty()) {
            log.info("Using default url [{}]. To override: %{}", DEFAULT_URL, USAGE_INSTRUCTIONS);
            return new Config()
                    .setHealthCheckUrl(DEFAULT_URL)
                    .setStatusCode(DEFAULT_STATUS_CODE);
        }

        final boolean applicable = arguments.size() >= 2 && arguments.size() <= 3;
        if (!applicable) {
            throw new ConfigurationException(String.format("Expecting 2 or 3 parameters. Received %s: %s. %s", arguments.size(), arguments, USAGE_INSTRUCTIONS));
        }

        String healthCheckUrl = arguments.get(1);
        int statusCode = arguments.size() == 2 ? DEFAULT_STATUS_CODE : Integer.valueOf(arguments.get(2));

        return new Config()
                .setHealthCheckUrl(healthCheckUrl)
                .setStatusCode(statusCode);
    }

    private class Config {
        private String healthCheckUrl;
        private int statusCode;

        public String getHealthCheckUrl() {
            return healthCheckUrl;
        }

        public Config setHealthCheckUrl(String healthCheckUrl) {
            this.healthCheckUrl = healthCheckUrl;
            return this;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public Config setStatusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }
    }
}
