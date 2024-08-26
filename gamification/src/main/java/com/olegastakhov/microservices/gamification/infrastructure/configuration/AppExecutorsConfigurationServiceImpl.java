package com.olegastakhov.microservices.gamification.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AppExecutorsConfigurationServiceImpl {

    @Bean(name = "GeneralPurposeVirtualThreadExecutor")
    public ExecutorService generalPurposeVirtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean(name = "GeneralPurposeVirtualThreadScheduler")
    public Scheduler generalPurposeVirtualThreadExecutorScheduler() {
        return Schedulers.fromExecutorService(generalPurposeVirtualThreadExecutor());
    }
}
