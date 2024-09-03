package com.olegastakhov.microservices.healthcheck;

import com.olegastakhov.microservices.healthcheck.config.Config;
import com.olegastakhov.microservices.healthcheck.config.ConfigParserBuilder;
import com.olegastakhov.microservices.healthcheck.exception.ConfigurationException;
import com.olegastakhov.microservices.healthcheck.processors.ModeImplementerInstances;
import com.olegastakhov.microservices.healthcheck.processors.ModeProcessor;

/**
 * See usage instructions inside ConfigParserBuilder
 */

public class HealthCheckApp {
    private static final int DOCKER_ERROR_EXIT_CODE = 1;  // Docker treats only code 1 as unhealthy indicator
    private static final int SUCCESS_EXIT_CODE = 0;

    public static void main(String[] args) {
        try {
            process(args);
            System.exit(SUCCESS_EXIT_CODE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(DOCKER_ERROR_EXIT_CODE);
        }
    }

    private static void process(String[] args) {
        final Config config = new ConfigParserBuilder().build(args);

        final ModeProcessor processor = ModeImplementerInstances.getInstance().getProcessors().stream()
                .filter(it -> it.getModeId().equals(config.getMode()))
                .findFirst()
                .orElseThrow(() -> new ConfigurationException(String.format("Unhandled mode: %s", config.getMode())));

        processor.process(config);
    }
}