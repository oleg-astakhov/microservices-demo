package com.olegastakhov.microservices.healthcheck.config;

import com.olegastakhov.microservices.healthcheck.exception.ConfigurationException;
import com.olegastakhov.microservices.healthcheck.processors.ModeImplementerInstances;
import com.olegastakhov.microservices.healthcheck.processors.ModeProcessor;

import java.util.List;

public class ConfigParserBuilder {

    private static final List<String> SUPPORTED_MODES = ModeImplementerInstances.getInstance().getProcessors().stream()
            .map(ModeProcessor::getModeId).toList();
    private static final String USAGE_INSTRUCTIONS = String.format("Usage: <cmd> <mode> <url> <status_code int>, where mode is one of: %s", SUPPORTED_MODES);

    public Config build(String[] args) {
        final String defaultUrl = "http://localhost:8080/actuator/health";
        final String defaultMode = ModeImplementerInstances.getInstance().getProcessors().stream()
                .filter(ModeProcessor::isDefault)
                .map(ModeProcessor::getModeId)
                .findAny().orElseThrow(() -> new ConfigurationException("Default mode processor has not been set"));
        final int defaultStatusCode = 200;

        String healthCheckUrl = defaultUrl;
        String mode = defaultMode;
        int statusCode = defaultStatusCode;

        final List<String> arguments = List.of(args);
        if (arguments.isEmpty()) {
            System.out.println(String.format("Using default url [%s]. To override: %s", defaultUrl, USAGE_INSTRUCTIONS));
        } else {
            if (arguments.size() != 3) {
                throw new ConfigurationException(String.format("Expecting 3 parameters or none. Received %s: %s. %s", arguments.size(), arguments, USAGE_INSTRUCTIONS));
            }

            mode = arguments.getFirst();
            if (!SUPPORTED_MODES.contains(mode)) {
                throw new ConfigurationException(String.format("Received non-supported mode %s. Supporter modes are: %s", mode, SUPPORTED_MODES.toString()));
            }

            healthCheckUrl = arguments.get(1);
            statusCode = Integer.valueOf(arguments.get(2));
        }

        return new Config()
                .setHealthCheckUrl(healthCheckUrl)
                .setMode(mode)
                .setStatusCode(statusCode);
    }
}
