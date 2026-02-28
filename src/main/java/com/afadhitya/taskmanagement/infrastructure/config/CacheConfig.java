package com.afadhitya.taskmanagement.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for caching feature flags and plan limits.
 * Uses Caffeine for high-performance in-memory caching.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache manager for feature flags and plan limits.
     * Configured with 5-minute TTL and maximum size of 10,000 entries.
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(10000)
            .recordStats());
        return cacheManager;
    }
}
