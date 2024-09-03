package com.olegastakhov.microservices.quiz.infrastructure;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentServiceImpl {
    @Value("${spring.profiles.active:}")
    private String activeProfile;
    private AppEnvironment currentEnvironment;

    @PostConstruct
    void init() {
        currentEnvironment = getCurrentEnvironment();
    }

    public AppEnvironment getCurrentEnvironment() {
        if (null != currentEnvironment) {
            return currentEnvironment;
        }

        if (StringUtils.isBlank(activeProfile)) {
            throw new RuntimeException("Active profile is not set when expected");
        }
        final String profile = activeProfile.trim().toLowerCase();

        currentEnvironment = AppEnvironment.fromString(profile);
        return currentEnvironment;
    }

    public boolean isCurrentEnvironmentEndToEndTests() {
        return getCurrentEnvironment().isComposeEndToEndTest();
    }
}
