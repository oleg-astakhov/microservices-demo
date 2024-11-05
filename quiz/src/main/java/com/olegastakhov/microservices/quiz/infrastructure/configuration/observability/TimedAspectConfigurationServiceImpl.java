package com.olegastakhov.microservices.quiz.infrastructure.configuration.observability;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimedAspectConfigurationServiceImpl {

    /**
     * To have the @Timed support we need to register this aspect.
     * Used for Prometheus metrics in /actuator/prometheus.
     */

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
