package com.olegastakhov.microservices.healthcheck.processors;

import com.olegastakhov.microservices.healthcheck.processors.actuator.ActuatorModeImpl;
import com.olegastakhov.microservices.healthcheck.processors.httpstatus.HttpStatusCodeModeImpl;

import java.util.List;

public class ModeImplementerInstances {
    private static final List<ModeProcessor> PROCESSORS = List.of(
            new ActuatorModeImpl(),
            new HttpStatusCodeModeImpl()
    );

    private static ModeImplementerInstances INSTANCE;

    public static synchronized ModeImplementerInstances getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new ModeImplementerInstances();
        }
        return INSTANCE;
    }

    private ModeImplementerInstances() {
    }

    public List<ModeProcessor> getProcessors() {
        return PROCESSORS;
    }
}
