package com.afadhitya.taskmanagement.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig {

    private final RateLimitProperties properties;

    @Bean
    public Cache<String, Bucket> bucketCache() {
        return Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofHours(1))
            .maximumSize(10000)
            .build();
    }

    @Bean
    public Bandwidth defaultBandwidth() {
        return Bandwidth.classic(
            properties.getDefaultCapacity(),
            Refill.intervally(properties.getDefaultRequestsPerMinute(), Duration.ofMinutes(1))
        );
    }

    @Bean
    public Bandwidth apiKeyBandwidth() {
        return Bandwidth.classic(
            properties.getApiKeyCapacity(),
            Refill.intervally(properties.getApiKeyRequestsPerMinute(), Duration.ofMinutes(1))
        );
    }
}
