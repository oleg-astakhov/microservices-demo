package com.olegastakhov.microservices.quiz.infrastructure;

import java.util.Optional;

public enum AppEnvironment {
    DEV("dev"),
    COMPOSE_DEV("composedev"),
    PRODUCTION("prod"),
    COMPOSE_END_TO_END_TEST("compose-e2e-test");

    AppEnvironment(String code) {
        this.code = code;
    }

    private String code;

    public String getCode() {
        return code;
    }

    public boolean isDev() {
        return DEV.equals(this);
    }

    public boolean isComposeDev() {
        return COMPOSE_DEV.equals(this);
    }

    public boolean isProduction() {
        return PRODUCTION.equals(this);
    }

    public boolean isComposeEndToEndTest() {
        return COMPOSE_END_TO_END_TEST.equals(this);
    }

    public static AppEnvironment fromString(String code) {
        return findFromString(code).orElseThrow(() -> {
            return new IllegalArgumentException(String.format("%s code [%s] is not a valid/recognized value", AppEnvironment.class.getSimpleName(), code));
        });
    }

    public static Optional<AppEnvironment> findFromString(String code) {
        for (AppEnvironment appEnvironment : AppEnvironment.values()) {
            if (appEnvironment.code.equals(code)) {
                return Optional.of(appEnvironment);
            }
        }
        return Optional.empty();
    }
}
