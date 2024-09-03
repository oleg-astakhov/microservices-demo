package com.olegastakhov.microservices.healthcheck.processors;

import com.olegastakhov.microservices.healthcheck.config.Config;

public interface ModeProcessor {
    void process(Config config);
    String getModeId();
    boolean isDefault();
}
