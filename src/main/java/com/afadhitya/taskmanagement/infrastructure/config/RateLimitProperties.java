package com.afadhitya.taskmanagement.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {

    private int defaultRequestsPerMinute;
    private int defaultCapacity;
    private int apiKeyRequestsPerMinute;
    private int apiKeyCapacity;
}
