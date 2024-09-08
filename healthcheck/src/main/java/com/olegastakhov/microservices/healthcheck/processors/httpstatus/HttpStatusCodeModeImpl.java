package com.olegastakhov.microservices.healthcheck.processors.httpstatus;

import com.olegastakhov.microservices.healthcheck.exception.ConfigurationException;
import com.olegastakhov.microservices.healthcheck.exception.NotHealthyException;
import com.olegastakhov.microservices.healthcheck.Util;
import com.olegastakhov.microservices.healthcheck.processors.ModeProcessor;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;

public class HttpStatusCodeModeImpl implements ModeProcessor {
    public static final String MODE_ID = "http_status";
    private final static int DEFAULT_STATUS_CODE = 200;
    private static final String USAGE_INSTRUCTIONS = String.format("Usage: <cmd> %s <url> <status_code int>", MODE_ID);

    @Override
    public String getModeId() {
        return MODE_ID;
    }


    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public void process(List<String> arguments) {
        final Config config = parseConfig(arguments);
        assertStatusCode(config);
    }

    private void assertStatusCode(final Config config) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            final HttpResponse<String> response = Util.getInstance().getHttpResponse(config.getHealthCheckUrl(), client);
            if (response.statusCode() != config.getStatusCode()) {
                throw new NotHealthyException(String.format("HTTP status code not satisfied. Expecting: %s. Got: %s", config.getStatusCode(), response.statusCode()));
            }
        }
    }

    private Config parseConfig(List<String> arguments) {
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
