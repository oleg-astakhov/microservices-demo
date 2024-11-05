package com.olegastakhov.microservices.quiz.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
public class AppExecutorsConfigurationServiceImpl {

    @Bean(name = "GeneralPurposeVirtualThreadExecutor")
    public ExecutorService generalPurposeVirtualThreadExecutor() {
        final ThreadFactory factory = Thread.ofVirtual().name("v-Thread", 0L).factory();
        return Executors.newThreadPerTaskExecutor(factory);
    }

    @Bean(name = "GeneralPurposeVirtualThreadScheduler")
    public Scheduler generalPurposeVirtualThreadExecutorScheduler() {
        return Schedulers.fromExecutorService(generalPurposeVirtualThreadExecutor());
    }
}
