package com.afadhitya.taskmanagement.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for asynchronous execution of feature handlers.
 * Used for fire-and-forget operations like search indexing.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Thread pool executor for async feature handlers.
     * Core pool size: 5, Max: 10, Queue capacity: 100
     */
    @Bean(name = "featureAsyncExecutor")
    public Executor featureAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("feature-async-");
        executor.initialize();
        return executor;
    }
}
