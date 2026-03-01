package com.afadhitya.taskmanagement.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

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
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        RedisSerializationContext.SerializationPair<Object> serializationPair =
                RedisSerializationContext.SerializationPair.fromSerializer(serializer);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(serializationPair)
                .entryTtl(TTL_10_MINUTES);

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        cacheConfigurations.put(WORKSPACE_FEATURES, 
                defaultConfig.entryTtl(TTL_5_MINUTES));
        cacheConfigurations.put(WORKSPACE_LIMITS, 
                defaultConfig.entryTtl(TTL_5_MINUTES));
        cacheConfigurations.put(USERS, 
                defaultConfig.entryTtl(TTL_10_MINUTES));
        cacheConfigurations.put(WORKSPACES, 
                defaultConfig.entryTtl(TTL_10_MINUTES));
        cacheConfigurations.put(PROJECTS, 
                defaultConfig.entryTtl(TTL_10_MINUTES));
        cacheConfigurations.put(LABELS, 
                defaultConfig.entryTtl(TTL_10_MINUTES));
        cacheConfigurations.put(PROJECT_MEMBERS, 
                defaultConfig.entryTtl(TTL_5_MINUTES));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
