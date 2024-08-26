package com.olegastakhov.microservices.healthcheck;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HealthCheckApp {

    public static void main(String[] args) {
        final String defaultUrl = "http://localhost:8080/actuator/health";
        final int dockerErrorExitCode = 1;  // Docker treats only code 1 as unhealthy indicator
        final List<String> arguments = List.of(args);

        String healthCheckUrl = defaultUrl;

        if (arguments.isEmpty()) {
            System.out.println(String.format("Using default url [%s]. Pass a command line argument to override, e.g. <cmd> <url>", defaultUrl));
        } else {
            if (arguments.size() > 1) {
                System.out.println(String.format("Expecting not more than 1 parameter. Received %s: %s", arguments.size(), arguments));
                System.exit(dockerErrorExitCode);
                return;
            }
            healthCheckUrl = arguments.getFirst();
        }

        final ObjectMapper objectMapper = new ObjectMapper();

        try (HttpClient client = HttpClient.newHttpClient()) {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(healthCheckUrl))
                    .build();
            final HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                System.out.println(String.format("Could not make connection to: %s", healthCheckUrl));
                System.exit(dockerErrorExitCode);
                return;
            }
            final String body = response.body();
            final Map<String, Object> map;
            try {
                map = objectMapper.readValue(body, new TypeReference<HashMap<String, Object>>() {
                });
            } catch (JsonProcessingException e) {
                System.out.println(String.format("Could not parse JSON response from: %s", healthCheckUrl));
                System.exit(dockerErrorExitCode);
                return;
            }

            final String status = (String) map.get("status");
            if (null == status) {
                System.out.println("Property 'status' is not found in parsed JSON");
                System.exit(dockerErrorExitCode);
                return;
            }

            if ("UP".equals(status)) {
                System.out.println("Service is UP");
                System.exit(0);
                return;
            }

            System.out.println(String.format("Status is %s", status));
            System.exit(dockerErrorExitCode);
        }
    }
}