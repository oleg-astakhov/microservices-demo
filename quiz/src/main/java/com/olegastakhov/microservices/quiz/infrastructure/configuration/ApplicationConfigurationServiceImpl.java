package com.olegastakhov.microservices.quiz.infrastructure.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ApplicationConfigurationServiceImpl  implements ApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(ApplicationConfigurationServiceImpl.class);

    @Autowired
    private Collection<Scheduler> schedulers;
    @Autowired
    private Collection<ExecutorService> executorServices;

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent.getClass().equals(ContextClosedEvent.class)) {
            log.debug("Shutting down executor services...");
            executorServices.forEach(this::shutdownExecutorService);
            log.debug("Shutting down Reactor schedulers...");
            schedulers.forEach(Scheduler::dispose);
        }
    }
    private void shutdownExecutorService(final ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(9, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
