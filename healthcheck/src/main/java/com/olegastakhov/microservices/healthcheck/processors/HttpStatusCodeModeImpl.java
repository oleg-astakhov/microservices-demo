package com.olegastakhov.microservices.healthcheck.processors;

import com.olegastakhov.microservices.healthcheck.config.Config;
import com.olegastakhov.microservices.healthcheck.exception.NotHealthyException;
import com.olegastakhov.microservices.healthcheck.Util;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class HttpStatusCodeModeImpl implements ModeProcessor {
    public static final String MODE_ID = "http_status";

    @Override
    public String getModeId() {
        return MODE_ID;
    }


    @Override
    public boolean isDefault() {
        return false;
    }

    @Override
    public void process(final Config config) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            final HttpResponse<String> response = Util.getInstance().getHttpResponse(config, client);
            if (response.statusCode() != config.getStatusCode()) {
                throw new NotHealthyException(String.format("HTTP status code not satisfied. Expecting: %s. Got: %s", config.getStatusCode(), response.statusCode()));
            }
        }
    }
}
