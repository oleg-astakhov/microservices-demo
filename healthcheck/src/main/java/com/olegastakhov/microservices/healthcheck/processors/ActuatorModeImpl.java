package com.olegastakhov.microservices.healthcheck.processors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olegastakhov.microservices.healthcheck.config.Config;
import com.olegastakhov.microservices.healthcheck.exception.NotHealthyException;
import com.olegastakhov.microservices.healthcheck.Util;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ActuatorModeImpl implements ModeProcessor {
    public static final String MODE_ID = "actuator";

    @Override
    public String getModeId() {
        return MODE_ID;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public void process(final Config config) {
        final ObjectMapper objectMapper = new ObjectMapper();

        try (HttpClient client = HttpClient.newHttpClient()) {
            final HttpResponse<String> response = Util.getInstance().getHttpResponse(config, client);
            if (response.statusCode() != config.getStatusCode()) {
                throw new NotHealthyException(String.format("HTTP status code not satisfied. Expecting: %s. Got: %s", config.getStatusCode(), response.statusCode()));
            }

            final String body = response.body();
            final Map<String, Object> map;
            try {
                map = objectMapper.readValue(body, new TypeReference<HashMap<String, Object>>() {
                });
            } catch (JsonProcessingException e) {
                throw new NotHealthyException(String.format("Could not parse JSON response from: %s", config.getHealthCheckUrl()));
            }

            final String status = (String) map.get("status");
            if (null == status) {
                throw new NotHealthyException("Property 'status' is not found in parsed JSON");
            }

            final String expectedStatus = "UP";

            if (!expectedStatus.equals(status)) {
                throw new NotHealthyException(String.format("Non-expected status. Expecting: %s. Got: %s", expectedStatus, status));
            }

            System.out.println("Service is UP");
        }
    }
}
