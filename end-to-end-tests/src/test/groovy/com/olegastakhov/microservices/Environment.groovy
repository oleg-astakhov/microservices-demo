package com.olegastakhov.microservices

import spock.util.concurrent.PollingConditions

class Environment {
    private static Environment INSTANCE
    public static final String DEFAULT_APP_URI = "http://localhost"
    private final pollingConditions = new PollingConditions(timeout: 4, delay: 0.5, factor: 1)
    private final debugPollingConditions = new PollingConditions(timeout: 60 * 15, delay: 2, factor: 1) // timeout after 15 minutes

    static synchronized Environment getInstance() {
        if (null == INSTANCE) {
            println("Creating ${Environment.class.getSimpleName()} instance...")
            INSTANCE = new Environment()
        }
        return INSTANCE
    }

    private Environment() {}

    boolean isDebugMode() {
        return Boolean.valueOf(System.getProperty("app-core.debug"))
    }

    PollingConditions getPollingConditions() {
        if (isDebugMode()) {
            return this.debugPollingConditions
        }
        return this.pollingConditions
    }
}
