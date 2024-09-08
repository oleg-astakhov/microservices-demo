package com.olegastakhov.microservices.healthcheck.processors;

import java.util.List;

public interface ModeProcessor {
    void process(List<String> arguments);
    String getModeId();
    boolean isDefault();
}
