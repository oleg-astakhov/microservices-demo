package com.olegastakhov.microservices.healthcheck.processors;

import com.olegastakhov.microservices.healthcheck.exception.ConfigurationException;

import java.util.List;

public class ProcessorFinder {

    private static final List<String> SUPPORTED_MODES = ModeImplementerInstances.getInstance().getProcessors().stream()
            .map(ModeProcessor::getModeId).toList();
    private static final String USAGE_INSTRUCTIONS = String.format("Usage: <cmd> <mode> <args>, where mode is one of: %s", SUPPORTED_MODES);

    public ModeProcessor getProcessor(List<String> arguments) {
        if (arguments.isEmpty()) {
            final ModeProcessor defaultProcessor = ModeImplementerInstances.getInstance().getProcessors().stream()
                    .filter(ModeProcessor::isDefault)
                    .findAny().orElseThrow(() -> new ConfigurationException("Default mode processor has not been set"));

            System.out.println(String.format("Using default processor [%s]. To override: %s", defaultProcessor.getModeId(), USAGE_INSTRUCTIONS));
            return defaultProcessor;
        }

        String mode = arguments.getFirst();

        return ModeImplementerInstances.getInstance().getProcessors().stream()
                .filter(it -> it.getModeId().equals(mode))
                .findAny()
                .orElseThrow(() -> new ConfigurationException(String.format("Received non-supported mode %s. Supporter modes are: %s", mode, SUPPORTED_MODES.toString())));
    }
}
