package com.olegastakhov.microservices.healthcheck;

import com.olegastakhov.microservices.healthcheck.processors.ProcessorFinder;
import com.olegastakhov.microservices.healthcheck.processors.ModeProcessor;

import java.util.List;

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
        final List<String> arguments = List.of(args);
        final ModeProcessor processor = new ProcessorFinder().getProcessor(arguments);
        processor.process(arguments);
    }
}