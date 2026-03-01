package com.afadhitya.taskmanagement.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String WORKSPACE_FEATURES = "workspaceFeatures";
    public static final String WORKSPACE_LIMITS = "workspaceLimits";
    public static final String USERS = "users";
    public static final String WORKSPACES = "workspaces";
    public static final String PROJECTS = "projects";
    public static final String LABELS = "labels";
    public static final String PROJECT_MEMBERS = "projectMembers";

    private static final Duration TTL_5_MINUTES = Duration.ofMinutes(5);
    private static final Duration TTL_10_MINUTES = Duration.ofMinutes(10);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(new ObjectMapper());

        RedisSerializationContext.SerializationPair<String> keySerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());

        RedisSerializationContext.SerializationPair<Object> valueSerializer =
                RedisSerializationContext.SerializationPair.fromSerializer(serializer);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(valueSerializer)
                .entryTtl(TTL_10_MINUTES)
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(WORKSPACE_FEATURES,
                defaultConfig.entryTtl(TTL_5_MINUTES));
        cacheConfigurations.put(WORKSPACE_LIMITS,
                defaultConfig.entryTtl(TTL_5_MINUTES));
        cacheConfigurations.put(PROJECT_MEMBERS,
                defaultConfig.entryTtl(TTL_5_MINUTES));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
