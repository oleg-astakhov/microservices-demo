package com.olegastakhov.microservices.healthcheck;

import com.olegastakhov.microservices.healthcheck.exception.NotHealthyException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Util {
    private static Util INSTANCE;

    public static synchronized Util getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new Util();
        }
        return INSTANCE;
    }

    private Util() {
    }

    public HttpResponse<String> getHttpResponse(final String url,
                                                final HttpClient client) {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new NotHealthyException(String.format("Could not make connection to: %s", url));
        }
    }
}
