package com.olegastakhov.microservices.quiz.infrastructure.configuration.observability;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class AopConfigurationServiceImpl {
    /**
     * To have the @Observed support we need to register this aspect
     * Used for Prometheus metrics in /actuator/prometheus
     * as well as Tracing, e.g. with Grafana Tempo.
     */
    @Bean
    ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
